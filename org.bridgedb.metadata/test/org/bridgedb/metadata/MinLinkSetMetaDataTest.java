/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.utils.Reporter;
import org.junit.Ignore;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class MinLinkSetMetaDataTest extends MetaDataTestBase{
    
    public MinLinkSetMetaDataTest() throws DatatypeConfigurationException, MetaDataException{        
    }
    
    @Test
    public void testHasRequiredValues() throws MetaDataException{
        Reporter.report("Linkset HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet(), minLinksetSetRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testHasCorrectTypes() throws MetaDataException{
        Reporter.report("Linkset HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet(), minLinksetSetRegistry);
        checkCorrectTypes(metaData);
    }
 
    @Test
    @Ignore
    public void testAllStatementsUsed() throws MetaDataException{
        Reporter.report("LinkSet AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet(), minLinksetSetRegistry);
        checkAllStatementsUsed(metaData);
    }

    @Test
    public void testValidateOk() throws MetaDataException{
        Reporter.report("LinkSet Validate OK");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet(), minLinksetSetRegistry);
        metaData.validate();
    }

    @Test
    public void testSummary() throws MetaDataException{
        Reporter.report("LinkSet Summary");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet(), minLinksetSetRegistry);
        String expected = "(Linkset) http://www.example.com/test/linkset1 OK!\n"
                + "(Dataset) http://www.example.com/test/dataset2 OK!\n"
                + "(Dataset) http://www.example.com/test/dataset1 OK!\n";
        String summary = metaData.summary();
        assertEquals(expected, summary);
    }
}
