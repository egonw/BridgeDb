/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

/**
 *
 * @author Christian
 */
public class TestIDSqlFactory {

    public static SQLAccess createTestSQLAccess() {
        try {
            SQLAccess sqlAccess = new MySQLAccess("jdbc:mysql://localhost:3306/imsidtest", "imstest", "imstest");
            sqlAccess.getConnection();
            return sqlAccess;
        } catch (BridgeDbSqlException ex) {
            System.err.println(ex);
            System.err.println("**** SKIPPPING tests due to Connection error.");
            System.err.println("To run these test you must have the following:");
            System.err.println("1. A MYSQL server running on port 3306");
            System.err.println("2. A database \"imsidtest\" setup on that server");
            System.err.println("3. A user \"imstest\" with password \"imstest\"");
            System.err.println("4. Full rights for user \"imstest\" on the database \"imstest\".");
            System.err.println("      DO NOT GRANT \"imstest\" RIGHTS TO OTHER DATABASES.");
            org.junit.Assume.assumeTrue(false);        
            return null;
         }
    } 
     
}