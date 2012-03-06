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
import java.net.URISyntaxException;
import java.util.regex.PatternSyntaxException;

import org.junit.Assert;
import org.junit.Test;

public class DataCollectionTest {

	@Test(expected=NullPointerException.class)
	public void testNullInConstructor() {
		new DataCollection(null);
	}

	@Test
	public void testGetIdentifier() throws URISyntaxException {
		DataCollection collection = new DataCollection(new URI("http://www.example.org/collection1"));
		Assert.assertEquals("http://www.example.org/collection1", collection.getIdentifier().toString());
	}

	@Test
	public void testDuplicateAdding() throws URISyntaxException {
		DataCollection collection = new DataCollection(new URI("http://www.example.org/collection1"));

		DataSource testSource = DataSource.register("X", "Affymetrix").asDataSource();
		Assert.assertEquals(0, collection.getDataSources().size());
		collection.addDataSource(testSource);
		Assert.assertEquals(1, collection.getDataSources().size());
		collection.addDataSource(testSource);
		Assert.assertEquals(1, collection.getDataSources().size());
	}

	@Test
	public void testEditingResultSet() throws URISyntaxException {
		DataCollection collection = new DataCollection(new URI("http://www.example.org/collection1"));

		DataSource testSource = DataSource.register("X", "Affymetrix").asDataSource();
		collection.addDataSource(testSource);
		Assert.assertEquals(1, collection.getDataSources().size());
		collection.getDataSources().add(DataSource.register("Y", "WhyThat").asDataSource());
		Assert.assertEquals(1, collection.getDataSources().size());
	}

	@Test
	public void testName() throws URISyntaxException {
		DataCollection collection = new DataCollection(new URI("http://www.example.org/collection1"));
		Assert.assertEquals("", collection.getName());
		collection.setName("ISBN");
		Assert.assertEquals("ISBN", collection.getName());
		collection.setName(null);
		Assert.assertEquals("", collection.getName());
	}

	@Test
	public void testNamespace() throws URISyntaxException {
		DataCollection collection = new DataCollection(new URI("http://www.example.org/collection1"));
		Assert.assertEquals("", collection.getNamespace());
		collection.setNamespace("isbn");
		Assert.assertEquals("isbn", collection.getNamespace());
		collection.setNamespace(null);
		Assert.assertEquals("", collection.getNamespace());
	}

	@Test
	public void testIdentifierPattern() throws URISyntaxException {
		DataCollection collection = new DataCollection(new URI("http://www.example.org/collection1"));
		Assert.assertEquals(".*", collection.getIdentifierPattern());
		collection.setIdentifierPattern("\\w+");
		Assert.assertEquals("\\w+", collection.getIdentifierPattern());
	}

	@Test(expected=PatternSyntaxException.class)
	public void testIdentifierPatternCompiling() throws URISyntaxException {
		DataCollection collection = new DataCollection(new URI("http://www.example.org/collection1"));
		collection.setIdentifierPattern("(\\w+");
	}

	@Test(expected=NullPointerException.class)
	public void testIdentifierPatternNull() throws URISyntaxException {
		DataCollection collection = new DataCollection(new URI("http://www.example.org/collection1"));
		collection.setIdentifierPattern(null);
	}

	@Test
	public void testMapIDCollection() {
		Assert.fail("to implement");
	}

	@Test
	public void testMapIDSingleXref() {
		Assert.fail("to implement");
	}

	@Test
	public void testXRefExists() {
		Assert.fail("to implement");
	}

	@Test
	public void testFreeSearch() {
		Assert.fail("to implement");
	}

	@Test
	public void testGetCapabilities() {
		Assert.fail("to implement");
	}

	@Test
	public void testClose() {
		Assert.fail("to implement");
	}

	@Test
	public void testIsConnected() {
		Assert.fail("to implement");
	}
}
