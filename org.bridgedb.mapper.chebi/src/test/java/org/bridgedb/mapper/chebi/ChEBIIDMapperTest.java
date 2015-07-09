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

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.junit.Assert;
import org.junit.Test;

public class ChEBIIDMapperTest {

	@Test
	public void testConnecting() throws IDMapperException, ClassNotFoundException {
		Class.forName("org.bridgedb.mapper.chebi.ChEBIIDMapper");
		IDMapper mapper = BridgeDb.connect("idmapper-chebi:matchSuperClass,matchSubClass");
		Assert.assertNotNull(mapper);
	}

}
