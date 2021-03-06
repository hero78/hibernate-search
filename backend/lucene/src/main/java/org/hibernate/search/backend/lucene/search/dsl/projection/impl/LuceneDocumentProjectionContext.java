/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.lucene.search.dsl.projection.impl;

import org.hibernate.search.backend.lucene.search.projection.impl.LuceneSearchProjectionBuilderFactory;
import org.hibernate.search.engine.search.SearchProjection;
import org.hibernate.search.engine.search.dsl.projection.SearchProjectionTerminalContext;
import org.hibernate.search.engine.search.projection.spi.SearchProjectionBuilder;

import org.apache.lucene.document.Document;

final class LuceneDocumentProjectionContext implements SearchProjectionTerminalContext<Document> {
	private final SearchProjectionBuilder<Document> builder;

	LuceneDocumentProjectionContext(LuceneSearchProjectionBuilderFactory factory) {
		this.builder = factory.document();
	}

	@Override
	public SearchProjection<Document> toProjection() {
		return builder.build();
	}
}
