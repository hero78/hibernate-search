/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.elasticsearch.types.dsl.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.backend.elasticsearch.document.model.impl.esnative.DataType;
import org.hibernate.search.backend.elasticsearch.document.model.impl.esnative.PropertyMapping;
import org.hibernate.search.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.backend.elasticsearch.types.codec.impl.ElasticsearchStringFieldCodec;
import org.hibernate.search.backend.elasticsearch.types.impl.ElasticsearchIndexFieldType;
import org.hibernate.search.backend.elasticsearch.types.predicate.impl.ElasticsearchStandardFieldPredicateBuilderFactory;
import org.hibernate.search.backend.elasticsearch.types.projection.impl.ElasticsearchStandardFieldProjectionBuilderFactory;
import org.hibernate.search.backend.elasticsearch.types.sort.impl.ElasticsearchStandardFieldSortBuilderFactory;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.engine.backend.types.converter.FromDocumentFieldValueConverter;
import org.hibernate.search.engine.backend.types.converter.ToDocumentFieldValueConverter;
import org.hibernate.search.engine.backend.types.dsl.StringIndexFieldTypeContext;
import org.hibernate.search.engine.backend.types.IndexFieldType;
import org.hibernate.search.util.impl.common.LoggerFactory;

/**
 * @author Yoann Rodiere
 * @author Guillaume Smet
 */
class ElasticsearchStringIndexFieldTypeContext
		extends AbstractElasticsearchStandardIndexFieldTypeContext<ElasticsearchStringIndexFieldTypeContext, String>
		implements StringIndexFieldTypeContext<ElasticsearchStringIndexFieldTypeContext> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private String analyzerName;
	private String normalizerName;
	private Projectable projectable = Projectable.DEFAULT;
	private Sortable sortable = Sortable.DEFAULT;

	ElasticsearchStringIndexFieldTypeContext(ElasticsearchIndexFieldTypeBuildContext buildContext) {
		super( buildContext, String.class );
	}

	@Override
	public ElasticsearchStringIndexFieldTypeContext analyzer(String analyzerName) {
		this.analyzerName = analyzerName;
		return this;
	}

	@Override
	public ElasticsearchStringIndexFieldTypeContext normalizer(String normalizerName) {
		this.normalizerName = normalizerName;
		return this;
	}

	@Override
	public ElasticsearchStringIndexFieldTypeContext projectable(Projectable projectable) {
		this.projectable = projectable;
		return this;
	}

	@Override
	public ElasticsearchStringIndexFieldTypeContext sortable(Sortable sortable) {
		this.sortable = sortable;
		return this;
	}

	@Override
	public IndexFieldType<String> toIndexFieldType() {
		PropertyMapping mapping = new PropertyMapping();

		boolean resolvedSortable = resolveDefault( sortable );
		boolean resolvedProjectable = resolveDefault( projectable );

		// TODO HSEARCH-3048 allow to configure indexed/not indexed
		mapping.setIndex( true );

		// TODO Use sub-fields? (but in that case, adjust projections accordingly)
		if ( analyzerName != null ) {
			mapping.setType( DataType.TEXT );
			mapping.setAnalyzer( analyzerName );

			if ( normalizerName != null ) {
				throw log.cannotApplyAnalyzerAndNormalizer( analyzerName, normalizerName, getBuildContext().getEventContext() );
			}

			if ( resolvedSortable ) {
				throw log.cannotUseAnalyzerOnSortableField( analyzerName, getBuildContext().getEventContext() );
			}
		}
		else {
			mapping.setType( DataType.KEYWORD );
			mapping.setNormalizer( normalizerName );
			mapping.setDocValues( resolvedSortable );
		}

		mapping.setStore( resolvedProjectable );

		ToDocumentFieldValueConverter<?, ? extends String> dslToIndexConverter =
				createDslToIndexConverter();
		FromDocumentFieldValueConverter<? super String, ?> indexToProjectionConverter =
				createIndexToProjectionConverter();
		ElasticsearchStringFieldCodec codec = ElasticsearchStringFieldCodec.INSTANCE;

		return new ElasticsearchIndexFieldType<>(
				codec,
				new ElasticsearchStandardFieldPredicateBuilderFactory<>( dslToIndexConverter, codec ),
				new ElasticsearchStandardFieldSortBuilderFactory<>( resolvedSortable, dslToIndexConverter, codec ),
				new ElasticsearchStandardFieldProjectionBuilderFactory<>( resolvedProjectable, indexToProjectionConverter, codec ),
				mapping
		);
	}

	@Override
	protected ElasticsearchStringIndexFieldTypeContext thisAsS() {
		return this;
	}
}
