/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.linkset.URLLinkListener;
import org.junit.BeforeClass;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author Christian
 */
public abstract class LinksetHandlerTest extends IDMapperTest {
    
    protected static URLLinkListener listener;
    
    public static void loadMappings() throws IDMapperException, IOException, RDFParseException, RDFHandlerException, RepositoryException{
        LinksetParser parser = new LinksetParser();
        //ystem.out.println("sample1to2.ttl");
        //The up and back file reference is important for other modules.
        System.out.println("sample1to2.ttl");
        parser.parse (listener, "../org.bridgedb.linksets/test-data/sample1to2.ttl", "http://foo/test1to2");
        System.out.println("sample1to3.ttl");
        parser.parse (listener, "../org.bridgedb.linksets/test-data/sample1to3.ttl", "http://foo/test3to1");
        System.out.println("sample2to1.ttl");
        parser.parse (listener, "../org.bridgedb.linksets/test-data/sample2to1.ttl", "http://foo/test2to1");
        System.out.println("sample2to3.ttl");
        parser.parse (listener, "../org.bridgedb.linksets/test-data/sample2to3.ttl", "http://foo/test2to3");
        System.out.println("sample3to1.ttl");
        parser.parse (listener, "../org.bridgedb.linksets/test-data/sample3to1.ttl", "http://foo/test3to1");
        System.out.println("sample3to2.ttl");
        parser.parse (listener, "../org.bridgedb.linksets/test-data/sample3to2.ttl", "http://foo/test3to2");
	}

}