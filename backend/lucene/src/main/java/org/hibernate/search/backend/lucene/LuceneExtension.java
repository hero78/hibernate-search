/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.lucene;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.hibernate.search.backend.lucene.search.dsl.predicate.LuceneSearchPredicateFactoryContext;
import org.hibernate.search.backend.lucene.search.dsl.predicate.impl.LuceneSearchPredicateFactoryContextImpl;
import org.hibernate.search.backend.lucene.search.dsl.projection.LuceneSearchProjectionFactoryContext;
import org.hibernate.search.backend.lucene.search.dsl.projection.impl.LuceneSearchProjectionFactoryContextImpl;
import org.hibernate.search.backend.lucene.search.projection.impl.LuceneSearchProjectionBuilderFactory;
import org.hibernate.search.engine.backend.types.dsl.IndexFieldTypeFactoryContext;
import org.hibernate.search.engine.backend.types.dsl.IndexFieldTypeFactoryContextExtension;
import org.hibernate.search.backend.lucene.types.dsl.LuceneIndexFieldTypeFactoryContext;
import org.hibernate.search.backend.lucene.logging.impl.Log;
import org.hibernate.search.backend.lucene.search.dsl.sort.LuceneSearchSortContainerContext;
import org.hibernate.search.backend.lucene.search.dsl.sort.impl.LuceneSearchSortContainerContextImpl;
import org.hibernate.search.backend.lucene.search.predicate.impl.LuceneSearchPredicateBuilderFactory;
import org.hibernate.search.backend.lucene.search.sort.impl.LuceneSearchSortBuilder;
import org.hibernate.search.backend.lucene.search.sort.impl.LuceneSearchSortBuilderFactory;
import org.hibernate.search.engine.search.dsl.predicate.SearchPredicateFactoryContext;
import org.hibernate.search.engine.search.dsl.predicate.SearchPredicateFactoryContextExtension;
import org.hibernate.search.engine.search.dsl.projection.SearchProjectionFactoryContext;
import org.hibernate.search.engine.search.dsl.projection.SearchProjectionFactoryContextExtension;
import org.hibernate.search.engine.search.dsl.sort.SearchSortContainerContext;
import org.hibernate.search.engine.search.dsl.sort.SearchSortContainerContextExtension;
import org.hibernate.search.engine.search.dsl.sort.spi.SearchSortDslContext;
import org.hibernate.search.engine.search.predicate.spi.SearchPredicateBuilderFactory;
import org.hibernate.search.engine.search.projection.spi.SearchProjectionBuilderFactory;
import org.hibernate.search.engine.search.sort.spi.SearchSortBuilderFactory;
import org.hibernate.search.util.impl.common.LoggerFactory;

/**
 * An extension for the Lucene backend, giving access to Lucene-specific features.
 * <p>
 * <strong>WARNING:</strong> while this type is API, because instances should be manipulated by users,
 * all of its methods are considered SPIs and therefore should never be called directly by users.
 * In short, users are only expected to get instances of this type from an API and pass it to another API.
 *
 * @param <R> The reference type for projections.
 * Users should not have to care about this, as the parameter will automatically take the appropriate value when calling
 * {@code .extension( LuceneExtension.get() }.
 * @param <O> The loaded object type for projections.
 * Users should not have to care about this, as the parameter will automatically take the appropriate value when calling
 * {@code .extension( LuceneExtension.get() }.
 */
public final class LuceneExtension<R, O>
		implements SearchPredicateFactoryContextExtension<LuceneSearchPredicateFactoryContext>,
		SearchSortContainerContextExtension<LuceneSearchSortContainerContext>,
		SearchProjectionFactoryContextExtension<LuceneSearchProjectionFactoryContext<R, O>, R, O>,
		IndexFieldTypeFactoryContextExtension<LuceneIndexFieldTypeFactoryContext> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final LuceneExtension<Object, Object> INSTANCE = new LuceneExtension<>();

	@SuppressWarnings("unchecked") // The instance works for any R and O
	public static <R, O> LuceneExtension<R, O> get() {
		return (LuceneExtension<R, O>) INSTANCE;
	}

	private LuceneExtension() {
		// Private constructor, use get() instead.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C, B> Optional<LuceneSearchPredicateFactoryContext> extendOptional(
			SearchPredicateFactoryContext original, SearchPredicateBuilderFactory<C, B> factory) {
		if ( factory instanceof LuceneSearchPredicateBuilderFactory ) {
			return Optional.of( new LuceneSearchPredicateFactoryContextImpl(
					original, (LuceneSearchPredicateBuilderFactory) factory
			) );
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C, B> Optional<LuceneSearchSortContainerContext> extendOptional(
			SearchSortContainerContext original, SearchSortBuilderFactory<C, B> factory,
			SearchSortDslContext<? super B> dslContext) {
		if ( factory instanceof LuceneSearchSortBuilderFactory ) {
			return Optional.of( extendUnsafe( original, (LuceneSearchSortBuilderFactory) factory, dslContext ) );
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<LuceneSearchProjectionFactoryContext<R, O>> extendOptional(
			SearchProjectionFactoryContext<R, O> original, SearchProjectionBuilderFactory factory) {
		if ( factory instanceof LuceneSearchProjectionBuilderFactory ) {
			return Optional.of( new LuceneSearchProjectionFactoryContextImpl<>(
					original, (LuceneSearchProjectionBuilderFactory) factory
			) );
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuceneIndexFieldTypeFactoryContext extendOrFail(IndexFieldTypeFactoryContext original) {
		if ( original instanceof LuceneIndexFieldTypeFactoryContext ) {
			return (LuceneIndexFieldTypeFactoryContext) original;
		}
		else {
			throw log.luceneExtensionOnUnknownType( original );
		}
	}

	@SuppressWarnings("unchecked") // If the target is Lucene, then we know B = LuceSearchSortBuilder
	private <B> LuceneSearchSortContainerContext extendUnsafe(
			SearchSortContainerContext original, LuceneSearchSortBuilderFactory factory,
			SearchSortDslContext<? super B> dslContext) {
		return new LuceneSearchSortContainerContextImpl(
				original, factory,
				(SearchSortDslContext<? super LuceneSearchSortBuilder>) dslContext
		);
	}
}
