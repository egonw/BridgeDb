/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.FoafConstants;
import org.bridgedb.linkset.constants.FrequencyOfChange;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.VoagConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class DataSetMetaData extends MetaData{

    DataSetMetaData parent;
    Set<DataSetMetaData> subsets;
    
    public DataSetMetaData(Resource id, RDFData input){
        super(id, input);
    }
    
    void setupValues(){   
        //Type is used seperately to extract the id
        values.add(new SingletonValue("Title", DctermsConstants.TITLE, String.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Description", DctermsConstants.DESCRIPTION, String.class, RequirementLevel.MUST));
        //Webpages
            values.add(new SingletonValue("Homepage", FoafConstants.HOMEPAGE, URI.class, RequirementLevel.MUST));
            values.add(new MultipleValue("Page", FoafConstants.PAGE, URI.class, RequirementLevel.MAY));
        values.add(new SingletonValue("License", DctermsConstants.LICENSE, URI.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Namespace", VoidConstants.URI_SPACE, String.class, RequirementLevel.TECHNICAL_MUST));
        SingletonValue version = new SingletonValue("Version", PavConstants.VERSION, String.class, RequirementLevel.MUST);
        values.add(version);
        //Provenance of Data Origin
            values.add(new SingletonValue("Publisher", DctermsConstants.PUBLISHER, URI.class, RequirementLevel.MAY));
            SingletonValue created = new SingletonValue("Created", DctermsConstants.CREATED, Date.class, RequirementLevel.MAY);
            values.add(created);
            SingletonValue modified = new SingletonValue("Modified", DctermsConstants.MODIFIED, Date.class, RequirementLevel.MAY);
            values.add(modified);
            modified.addAlternative(created);
            version.addAlternative(modified);
            values.add(new SingletonValue("Retrieved From",PavConstants.RETRIEVED_FROM, URI.class, RequirementLevel.MAY));
            SingletonValue retrievedOn = new SingletonValue("Retrieved On", PavConstants.RETRIEVED_ON , Date.class, RequirementLevel.MAY);
            values.add(retrievedOn);
            version.addAlternative(retrievedOn);
            values.add(new SingletonValue("Retrieved By",PavConstants.RETRIEVED_BY, URI.class, RequirementLevel.MAY));
            values.add(new SingletonValue("Imported From",PavConstants.IMPORTED_FROM, URI.class, RequirementLevel.MAY));
            SingletonValue importedOn = new SingletonValue("Imported On", PavConstants.IMPORTED_ON , Date.class, RequirementLevel.MAY);
            values.add(importedOn);
            version.addAlternative(importedOn);
            values.add(new SingletonValue("Imported By",PavConstants.IMPORTED_BY, URI.class, RequirementLevel.MAY));
            values.add(new SingletonValue("Derived From",PavConstants.DERIVED_FROM, URI.class, RequirementLevel.MAY));
            SingletonValue derivedOn = new SingletonValue("Derived On", PavConstants.DERIVED_ON , Date.class, RequirementLevel.MAY);
            values.add(derivedOn);
            version.addAlternative(derivedOn);
            values.add(new SingletonValue("Derived By",PavConstants.DERIVED_BY, URI.class, RequirementLevel.MAY));
        //Distinguishing Subsets is handled seperately  
        values.add(new MultipleValue("Vocabularies",VoidConstants.VOCABULARY, URI.class, RequirementLevel.SHOULD));
        values.add(new MultipleValue("Topics",DctermsConstants.SUBJECT, URI.class, RequirementLevel.SHOULD));
        values.add(new MultipleValue("Examples",VoidConstants.EXAMPLE_RESOURCE, Resource.class, RequirementLevel.SHOULD));
        values.add(new SingletonValue("Data Dump",VoidConstants.DATA_DUMP, URI.class, RequirementLevel.MUST));
        values.add(new SingletonValue("Sparql End Point",VoidConstants.SPARQL_ENDPOINT, URI.class, RequirementLevel.MAY));
        values.add(new SingletonValue("Frequency Of Change",VoagConstants.FREQUENCY_OF_CHANGE, FrequencyOfChange.class, RequirementLevel.SHOULD));        
    }

}