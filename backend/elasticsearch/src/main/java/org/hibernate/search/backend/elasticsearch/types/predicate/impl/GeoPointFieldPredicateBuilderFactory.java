/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.elasticsearch.types.predicate.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.backend.elasticsearch.search.predicate.impl.ElasticsearchSearchPredicateCollector;
import org.hibernate.search.engine.search.predicate.spi.MatchPredicateBuilder;
import org.hibernate.search.engine.search.predicate.spi.RangePredicateBuilder;
import org.hibernate.search.util.impl.common.LoggerFactory;


public class GeoPointFieldPredicateBuilderFactory implements ElasticsearchFieldPredicateBuilderFactory {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	public static final GeoPointFieldPredicateBuilderFactory INSTANCE = new GeoPointFieldPredicateBuilderFactory();

	private GeoPointFieldPredicateBuilderFactory() {
	}

	@Override
	public MatchPredicateBuilder<ElasticsearchSearchPredicateCollector> createMatchPredicateBuilder(String absoluteFieldPath) {
		throw log.matchPredicatesNotSupportedByGeoPoint( absoluteFieldPath );
	}

	@Override
	public RangePredicateBuilder<ElasticsearchSearchPredicateCollector> createRangePredicateBuilder(String absoluteFieldPath) {
		throw log.rangePredicatesNotSupportedByGeoPoint( absoluteFieldPath );
	}
}