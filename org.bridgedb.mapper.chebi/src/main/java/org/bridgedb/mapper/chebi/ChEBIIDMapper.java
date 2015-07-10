// BridgeDb, An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009 BridgeDb developers
//                2015 Egon Willighagen <egonw@users.sf.net>
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
//
package org.bridgedb.mapper.chebi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AbstractIDMapper;
import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

public class ChEBIIDMapper extends AbstractIDMapper implements AttributeMapper {

	static {
		BridgeDb.register ("idmapper-chebi", new Driver());
	}

	private final static class Driver implements org.bridgedb.Driver {
		/** private constructor to prevent outside instantiation. */
		private Driver() { } 

		/** {@inheritDoc} */
		public IDMapper connect(String config) throws IDMapperException  {
			return new ChEBIIDMapper(
                config.contains("matchSuperClass"),
				config.contains("matchSubClass"),
				config.contains("matchChargeStates"),
				config.contains("matchTautomers")
            );
		}
	}

	private final class ChEBICapabilities implements IDMapperCapabilities {
		private Map<String, String> properties = new HashMap<String, String>();

		public ChEBICapabilities() throws IDMapperException {}

		@SuppressWarnings("serial")
		private Set<DataSource> loadDataSources(String cmd) throws IDMapperException {
			return new HashSet<DataSource>() {{
				add(
					DataSource.systemCodeExists("Ce")
					    ? DataSource.getExistingBySystemCode("Ce")
						: DataSource.register("Ce", "ChEBI").asDataSource()
				);
			}};
		}

		/** {@inheritDoc} */
		public Set<String> getKeys() {
			return properties.keySet();
		}

		/** {@inheritDoc} */
		public String getProperty(String key) {
			return properties.get(key);
		}

		/** {@inheritDoc} */
		public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
			if (supportedSourceDataSources==null) {
				supportedSourceDataSources = loadDataSources("sourceDataSources");
			}
			return supportedSourceDataSources;
		}

		/** {@inheritDoc} */
		public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
			if (supportedTargetDataSources==null) {
				supportedTargetDataSources = loadDataSources("targetDataSources");
			}
			return supportedTargetDataSources;
		}

		/** {@inheritDoc} */
		public boolean isFreeSearchSupported() {
			return false;
		}

		/** {@inheritDoc} */
		public boolean isMappingSupported(DataSource source, DataSource target) throws IDMapperException {
			return source.getFullName().equals("ChEBI") &&
				target.getFullName().equals("ChEBI");
		}
	}

	private IDMapperCapabilities capabilities;

	private Set<DataSource> supportedSourceDataSources = null;
	private Set<DataSource> supportedTargetDataSources = null;

	private boolean matchSuperClass;
	private boolean matchSubClass;
	private boolean matchChargeStates;
	private boolean matchTautomers;

	private ChEBIIDMapper(
		boolean matchSuperClass, boolean matchSubClass,
		boolean matchChargeStates, boolean matchTautomers) throws IDMapperException {
		this.matchSuperClass = matchSuperClass;
		this.matchSubClass = matchSubClass;
		this.matchChargeStates = matchChargeStates;
		this.matchTautomers = matchTautomers;
		capabilities = new ChEBICapabilities();
		loadData();
	}

	private Map<String,List<String>> superClasses = null;
	private Map<String,List<String>> subClasses = null;

	private void loadData() {
		if (matchSuperClass) {
			superClasses = new HashMap<String, List<String>>(44000);
			InputStream superClassStream = this.getClass().getClassLoader().getResourceAsStream("superclasses.txt");
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(superClassStream));
				String line = reader.readLine();
				while (line != null) {
					String[] parts = line.split(" ");
					String key = parts[0];
					String[] supers = parts[1].split(",");
					superClasses.put(key, Arrays.asList(supers));
					line = reader.readLine();
				}
				superClassStream.close();
			} catch (IOException e) {
				// ignore
			}
		} else {
			superClasses = Collections.emptyMap();
		}
		if (matchSubClass) {
			subClasses = new HashMap<String, List<String>>(44000);
			InputStream subClassStream = this.getClass().getClassLoader().getResourceAsStream("subclasses.txt");
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(subClassStream));
				String line = reader.readLine();
				while (line != null) {
//					System.out.println("Sub line: " + line);
					String[] parts = line.split(" ");
					String key = parts[0];
					String[] supers = parts[1].split(",");
					subClasses.put(key, Arrays.asList(supers));
					line = reader.readLine();
				}
				subClassStream.close();
			} catch (IOException e) {
				// ignore
			}
		} else {
			subClasses = Collections.emptyMap();
		}
	}

	/** {@inheritDoc} */
	public void close() throws IDMapperException {}

	@Override
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs,
			DataSource... tgtDataSources) throws IDMapperException {
		Map<Xref, Set<Xref>> results = new HashMap<Xref, Set<Xref>>(10);
		for (Xref xref : srcXrefs) {
			if (matchSuperClass) {
				Set<Xref> trgXrefs = new HashSet<Xref>();
				for (String targetIDs : superClasses.get(shorten(xref.getId()))) {
					trgXrefs.add(new Xref("CHEBI:" + targetIDs, xref.getDataSource()));
				}
				results.put(xref, trgXrefs);
			}
			if (matchSubClass) {
				Set<Xref> trgXrefs = new HashSet<Xref>();
				for (String targetIDs : subClasses.get(shorten(xref.getId()))) {
					trgXrefs.add(new Xref("CHEBI:" + targetIDs, xref.getDataSource()));
				}
				results.put(xref, trgXrefs);
			}
		}
		return results;
	}

	private Object shorten(String id) {
		if (id.startsWith("CHEBI:")) return id.substring(6);
		return id;
	}

	@Override
	public boolean xrefExists(Xref xref) throws IDMapperException {
		return superClasses.containsKey(shorten(xref.getId()));
	}

	@Override
	public Set<Xref> freeSearch(String text, int limit)
			throws IDMapperException {
		return Collections.emptySet();
	}

	@Override
	public IDMapperCapabilities getCapabilities() {
		return this.capabilities;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public Set<String> getAttributes(Xref ref, String attrType)
			throws IDMapperException {
		return Collections.emptySet();
	}

	@Override
	public Map<String, Set<String>> getAttributes(Xref ref)
			throws IDMapperException {
		return Collections.emptyMap();
	}

	@Override
	public boolean isFreeAttributeSearchSupported() {
		return false;
	}

	@Override
	public Map<Xref, String> freeAttributeSearch(String query, String attrType,
			int limit) throws IDMapperException {
		return Collections.emptyMap();
	}

	@Override
	public Map<Xref, Set<String>> freeAttributeSearchEx(String query,
			String attrType, int limit) throws IDMapperException {
		return Collections.emptyMap();
	}

	@Override
	public Set<String> getAttributeSet() throws IDMapperException {
		return Collections.emptySet();
	}

}
