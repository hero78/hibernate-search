/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.mapper.mapping.building.spi;

import org.hibernate.search.engine.backend.document.spi.DocumentState;
import org.hibernate.search.engine.backend.index.spi.IndexManager;

/**
 * @author Yoann Rodiere
 */
public interface IndexManagerBuildingState<D extends DocumentState> {

	String getIndexName();

	MappingIndexModelCollector getModelCollector();

	/**
	 * Return the created index manager; to be called only after the model collector has been fully populated.
	 * @return the created index manager
	 */
	IndexManager<D> getResult();

}