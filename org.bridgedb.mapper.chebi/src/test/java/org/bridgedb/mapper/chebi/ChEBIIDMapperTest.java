// Copyright 2015 Egon Willighagen <egonw@users.sf.net>
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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.junit.Assert;
import org.junit.Test;

public class ChEBIIDMapperTest {

	@Test
	public void testConnecting() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass,matchSubClass");
		Assert.assertNotNull(mapper);
	}

	@Test
	public void testMatchSuperClasses() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:35508", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertNotSame(0, xrefs.size());
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:35507", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:35341", DataSource.getExistingByFullName("ChEBI"))));
	}

	@Test
	public void testMatchSuperClassesDeep() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:35508", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertNotSame(0, xrefs.size());
		System.out.println("XXXXXXX: " + xrefs);
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:35341", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:18059", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:50860", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:33582", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:33675", DataSource.getExistingByFullName("ChEBI"))));
		// that's deep enough
	}

	@Test
	public void testMatchSuperClasses_Not() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSubClass");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:35508", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertNotSame(0, xrefs.size());
		Assert.assertFalse(xrefs.contains(new Xref("CHEBI:35507", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertFalse(xrefs.contains(new Xref("CHEBI:35341", DataSource.getExistingByFullName("ChEBI"))));
	}

	@Test
	public void testXrefSubSuperCombo() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass,matchSubClass");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:35508", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertNotSame(0, xrefs.size());
		Assert.assertFalse(xrefs.contains(new Xref("35507", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertFalse(xrefs.contains(new Xref("35341", DataSource.getExistingByFullName("ChEBI"))));
	}

	@Test
	public void testXrefExists() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass,matchSubClass");
		boolean exists = mapper.xrefExists(new Xref("CHEBI:9355", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertTrue(exists);
	}

	@Test
	public void testMatchSubClasses() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSubClass");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:61221", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertEquals(2, xrefs.size());
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:62524", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:61220", DataSource.getExistingByFullName("ChEBI"))));
	}

	@Test
	public void testRoles() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init(); 
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchRoles");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:66211", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertEquals(1, xrefs.size());
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:64696", DataSource.getExistingByFullName("ChEBI"))));
	}

	@Test
	public void testEnantiomers() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init(); 
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchEnantiomers");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:17561", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertEquals(1, xrefs.size());
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:16375", DataSource.getExistingByFullName("ChEBI"))));
	}

	@Test
	public void testGlucoseRoles() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init(); 
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchRoles");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:17234", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertEquals(1, xrefs.size());
		System.out.println("" + xrefs);
		Assert.assertTrue(xrefs.contains(new Xref("CHEBI:78675", DataSource.getExistingByFullName("ChEBI"))));
	}

	@Test
	public void testMatchSubClasses_Not() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass");
		Set<Xref> xrefs = mapper.mapID(new Xref("CHEBI:61221", DataSource.getExistingByFullName("ChEBI")));
		Assert.assertNotSame(0, xrefs.size());
		Assert.assertFalse(xrefs.contains(new Xref("CHEBI:62524", DataSource.getExistingByFullName("ChEBI"))));
		Assert.assertFalse(xrefs.contains(new Xref("CHEBI:61220", DataSource.getExistingByFullName("ChEBI"))));
	}

	@Test
	@SuppressWarnings("serial")
	public void testMatchCollection() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass");
		Map<Xref,Set<Xref>> xrefs = mapper.mapID(
			new ArrayList<Xref>() {{
				add(new Xref("CHEBI:61221", DataSource.getExistingByFullName("ChEBI")));
			}},
			DataSource.getExistingByFullName("ChEBI")
		);
		Assert.assertNotSame(0, xrefs.size());
	}

	@Test
	@SuppressWarnings("serial")
	public void testMatchCollection_Null() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass");
		Map<Xref,Set<Xref>> xrefs = mapper.mapID(
			new ArrayList<Xref>() {{
				add(null);
			}},
			DataSource.getExistingByFullName("ChEBI")
		);
		Assert.assertEquals(0, xrefs.size());
	}

	@Test
	@SuppressWarnings("serial")
	public void testMatchCollection_HMDB() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		DataSourceTxt.init();
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass");
		Map<Xref,Set<Xref>> xrefs = mapper.mapID(
			new ArrayList<Xref>() {{
				add(new Xref("HMDB00001", DataSource.getExistingByFullName("HMDB")));
			}},
			DataSource.getExistingByFullName("ChEBI")
		);
		Assert.assertEquals(0, xrefs.size());
	}
}
