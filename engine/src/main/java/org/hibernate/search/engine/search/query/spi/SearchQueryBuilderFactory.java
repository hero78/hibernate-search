/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.search.query.spi;

import java.util.List;

import org.hibernate.search.engine.mapper.session.context.spi.SessionContextImplementor;
import org.hibernate.search.engine.search.SearchProjection;

/**
 * A factory for search query builders.
 * <p>
 * This is the main entry point for the engine
 * to ask the backend to build search queries.
 *
 * @param <C> The type of query element collector
 */
public interface SearchQueryBuilderFactory<C> {

	<O> SearchQueryBuilder<O, C> asObject(SessionContextImplementor sessionContext,
			ProjectionHitMapper<?, O> projectionHitMapper);

	<T> SearchQueryBuilder<T, C> asReference(SessionContextImplementor sessionContext,
			ProjectionHitMapper<?, ?> projectionHitMapper);

	<T> SearchQueryBuilder<T, C> asProjection(SessionContextImplementor sessionContext,
			ProjectionHitMapper<?, ?> projectionHitMapper, SearchProjection<T> projection);

	SearchQueryBuilder<List<?>, C> asProjections(SessionContextImplementor sessionContext,
			ProjectionHitMapper<?, ?> projectionHitMapper, SearchProjection<?>... projections);

}
