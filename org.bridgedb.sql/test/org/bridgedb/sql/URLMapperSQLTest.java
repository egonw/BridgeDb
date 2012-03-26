/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.url.URLMapperTest;
import org.junit.BeforeClass;

/**
 * This class depends on URLasIDMapperSQLTest having loaded the data.
 * 
 * @author Christian
 */
public class URLMapperSQLTest extends URLMapperTest {
    
    private static final String CREATOR1 = "testCreateProvenance";
    private static final String PREDICATE1 = "testMapping";
    private static final long CREATION1 = new Date().getTime();

    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{

        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestURLSQLAccess();
        connectionOk = true;
        urlMapper = new URLMapperSQL(sqlAccess);
    }
            
}