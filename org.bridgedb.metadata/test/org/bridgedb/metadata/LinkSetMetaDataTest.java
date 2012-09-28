/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.junit.Ignore;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.metadata.utils.Reporter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class LinkSetMetaDataTest extends MetaDataTestBase{
    
    public LinkSetMetaDataTest() throws DatatypeConfigurationException{        
    }
    
    @Test
    public void testHasRequiredValues() throws MetaDataException{
        Reporter.report("Linkset HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet());
        checkRequiredValues(metaData, RequirementLevel.SHOULD);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY));
    } 

    @Test
    public void testHasCorrectTypes() throws MetaDataException{
        Reporter.report("Linkset HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet());
        checkCorrectTypes(metaData);
    }
 
    @Test
    public void testAllStatementsUsed() throws MetaDataException{
        Reporter.report("LinkSet AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet());
        checkAllStatementsUsed(metaData);
    }

    @Test
    public void testValidateOk() throws MetaDataException{
        Reporter.report("LinkSet Validate OK");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet());
        metaData.validate(RequirementLevel.SHOULD);
    }

    @Test
    public void testSummary() throws MetaDataException{
        Reporter.report("LinkSet Summary");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet());
        String expected = "Linkset Void id http://www.example.com/test/linkset1 OK!\n"
                + "Dataset void id http://www.example.com/test/dataset2 OK!\n"
                + "Dataset void id http://www.example.com/test/dataset1 OK!\n";
        String summary = metaData.summary();
        assertEquals(expected, summary);
    }
}