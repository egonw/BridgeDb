// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
//
// Copyright 2012  Egon Willighagen <egonw@users.sf.net>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.bridgedb;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A collection of data sources that share the same identifier type. These
 * sources are equivalent in that each of them uses the exact same
 * identifier type.
 * 
 * @author egonw
 */
public class DataCollection {

	private Set<DataSource> sources = null;
	
	public DataCollection() {
		// no intialization of sources; that happens when needed
	}

	/**
	 * Returns the set of {@link DataSource}s contained in this collection.
	 * 
	 * The returned set must be a copy, and any operation on that set must
	 * not affect this data collection.
	 * 
	 * @return the {@link Set} of {@link DataSource}s in this collection
	 * @see    
	 */
	public Set<DataSource> getDataSources() {
		if (sources == null)
			// if there are no sources, return an empty set
			return Collections.emptySet();
		// make a copy
		Set<DataSource> resultSet = new HashSet<DataSource>();
		resultSet.addAll(sources);
		return resultSet;
	}

	/**
	 * Adds an additional {@link DataSource} to this collection.
	 */
	public void addDataSource(DataSource source) {
		if (sources == null) sources = new HashSet<DataSource>();
		sources.add(source);
	}
}
