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

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A collection of data sources that share the same identifier type. These
 * sources are equivalent in that each of them uses the exact same
 * identifier type.
 * 
 * @author egonw
 */
public class DataCollection {

	private Set<DataSource> sources = null;
	private URI identifier;
	private String name;
	private String identifierPattern;
	private String namespace;

	/**
	 * Constructs an new, empty collection with the given identifier.
	 * 
	 * @param identifier a {@link URL} uniquely identifying this collection
	 */
	public DataCollection(URI identifier) {
		if (identifier == null) throw new NullPointerException("A DataCollection identifier must not be null.");
		this.identifier = identifier;
	}

	/**
	 * Returns the name of this data collection. If the name was not set, it returns an empty {@link String}.
	 * 
	 * @return a String with the name
	 * @see    #setName(String)
	 */
	public String getName() {
		if (this.name == null) return "";
		return name;
	}

	/**
	 * Sets the name of this data collection.
	 * 
	 * @param name the name
	 * @see   #getName()
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the regular expression that identifies the pattern of identifiers for this collection.
	 * 
	 * @return a {@link String} with a regular expression
	 * @see    #setIdentifierPattern(String)
	 */
	public String getIdentifierPattern() {
		if (this.identifierPattern == null) return ".*";
		return identifierPattern;
	}

	/**
	 * Sets the regular expression that identifies the pattern of identifiers for this collection.
	 * The regular expression is Java-style.
	 *
	 * @param identifierPattern
	 */
	public void setIdentifierPattern(String identifierPattern) {
		// check if the pattern compiles
		Pattern.compile(identifierPattern);
		this.identifierPattern = identifierPattern;
	}

	/**
	 * Returns the namespace of this data collection.
	 * 
	 * @return the namespace of this collection.
	 * @see    #setNamespace(String)
	 */
	public String getNamespace() {
		if (this.namespace == null) return "";
		return namespace;
	}

	/**
	 * Sets the namespace of this data collection, which is used for e.g. MIRIAM URNs.
	 * 
	 * @param namespace the namespace for this collection
	 * @see             DataCollection#getNamespace()
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Returns the unique identifier for this data collection.
	 *
	 * @return a URL for this collection
	 */
	public URI getIdentifier() {
		return identifier;
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
