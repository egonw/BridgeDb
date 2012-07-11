package org.bridgedb.ws.client;

import org.junit.Ignore;
import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.ops.LinkSetInfo;
import org.bridgedb.url.URLMapperTestBase;
import org.bridgedb.ws.WSClientFactory;
import org.bridgedb.ws.WSInterface;
import org.bridgedb.ws.WSMapper;
import org.junit.BeforeClass;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
@Ignore //to slow on too much data boo!
public class OpsMapperTest  extends org.bridgedb.ops.OpsMapperTest{
    
    @BeforeClass
    public static void setupURLs() throws IDMapperException{
        URLMapperTestBase.setupURLs();
        link1to2 = "http://localhost:8080/OPS-IMS/linkset/1/#Test1_2";
        link1to3 = "http://localhost:8080/OPS-IMS/linkset/2/#Test1_3";
        link2to1 = "http://localhost:8080/OPS-IMS/linkset/1/#Test1_2/inverted";
        link2to3 = "http://localhost:8080/OPS-IMS/linkset/3/#Test2_3";
        link3to1 = "http://localhost:8080/OPS-IMS/linkset/2/#Test1_3/inverted";
        link3to2 = "http://localhost:8080/OPS-IMS/linkset/3/#Test2_3/inverted";
    }

    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSInterface webService = WSClientFactory.createTestWSClient();
        opsMapper = new WSMapper(webService);
    }

}