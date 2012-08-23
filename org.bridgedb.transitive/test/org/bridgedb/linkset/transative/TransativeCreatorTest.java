// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.linkset.transative;

import org.bridgedb.utils.Reporter;
import org.bridgedb.linkset.LinksetLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class TransativeCreatorTest {
    
    public TransativeCreatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class TransativeCreator.
     */
    @Test
    @Ignore //BROKEN AND CODE TO EB REPLACED!
    public void testMain() throws Exception {
        Reporter.report("main");
        String[] args = new String[4];
        args[0] = "2";
        args[1] = "3";
        args[2] = "test";
        String fileName = "../org.bridgedb.transitive/test-data/linkset2To3.ttl";
//        String fileName = "test-data/linkset2To3.ttl";
        args[3] = fileName;
        TransativeCreator.main(args);
        args = new String[2];
        args[0] = fileName;
        args[1] = "validate";
        LinksetLoader.main (args);
    }
}