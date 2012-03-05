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

import org.junit.Assert;
import org.junit.Test;

public class DataCollectionTest {

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

}
