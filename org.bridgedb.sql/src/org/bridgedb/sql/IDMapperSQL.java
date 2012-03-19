package org.bridgedb.sql;

import org.bridgedb.ws.ByPossitionIterator;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.linkset.LinkListener;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceException;
import org.bridgedb.provenance.ProvenanceFactory;
import org.bridgedb.provenance.SimpleProvenance;
import org.bridgedb.ws.XrefByPossition;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
public class IDMapperSQL implements IDMapper, IDMapperCapabilities, LinkListener, ProvenanceFactory, 
        XrefIterator, XrefByPossition{
    
    //Numbering should not clash with any GDB_COMPAT_VERSION;
	private static final int SQL_COMPAT_VERSION = 4;
    private static final int BLOCK_SIZE = 10000;
    
    private static final int SQL_TIMEOUT = 2;

    private Connection possibleOpenConnection;
    private PreparedStatement pstInsertLink = null;
    private PreparedStatement pstCheckLink = null;
    private SQLAccess sqlAccess;
    private int insertCount = 0;
    private int doubleCount = 0;
    
    public IDMapperSQL(SQLAccess sqlAccess) throws BridgeDbSqlException{
        if (sqlAccess == null){
            throw new IllegalArgumentException("sqlAccess can not be null");
        }
        this.sqlAccess = sqlAccess;
        checkVersion();
    }   

    private Statement createStatement() throws BridgeDbSqlException{
        try {
            if (possibleOpenConnection == null){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (possibleOpenConnection.isClosed()){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (!possibleOpenConnection.isValid(SQL_TIMEOUT)){
                possibleOpenConnection.close();
                possibleOpenConnection = sqlAccess.getConnection();
            }  
            return possibleOpenConnection.createStatement();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException ("Error creating a new statement");
        }
    }
    
    /**
     * Checks that the schema is for this version.
     * 
     * @throws BridgeDbSqlException If the schema version is not the expected one.
     */
	public void checkVersion() throws BridgeDbSqlException
	{
        Statement stmt = createStatement();
        ResultSet r = null;
        int version = 0;
        try {
            r = stmt.executeQuery("SELECT schemaversion FROM info");
            if(r.next()) version = r.getInt(1);
        } catch (SQLException e) {
            //probably new databse do nothing.
            version = SQL_COMPAT_VERSION;
        }
		finally
		{
            if (r != null) try { r.close(); } catch (SQLException ignore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ignore) {}
		}
 		switch (version)
		{
            case SQL_COMPAT_VERSION:
                return;
    		case 2:
        		throw new BridgeDbSqlException("Please use the SimpleGdbFactory in the org.bridgedb.rdb package");
            case 3:
                throw new BridgeDbSqlException("Please use the SimpleGdbFactory in the org.bridgedb.rdb package");
            //NB add future schema versions here
            default:
                throw new BridgeDbSqlException ("Unrecognized schema version '" + version + "', please make sure you have the latest " +
					"version of this software and databases");
		}		
	}

    /**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @throws IDMapperException 
	 */
	public void createSQLTables() throws BridgeDbSqlException
	{
		try 
		{
			Statement sh = createStatement();
 			sh.execute("CREATE TABLE                            "
                    + "IF NOT EXISTS                            "
					+ "info                                     " 
					+ "(    schemaversion INTEGER PRIMARY KEY	"
                    + ")");
			sh.execute( //Add compatibility version of GDB
					"INSERT INTO info VALUES ( " + SQL_COMPAT_VERSION + ")");
			sh.execute("CREATE TABLE                        "
                    + "IF NOT EXISTS                        "
                    + "link                                 " 
					+ " (   idLeft VARCHAR(50) NOT NULL,	" 
					+ "     codeLeft VARCHAR(100) NOT NULL,	" 
					+ "     idRight VARCHAR(50) NOT NULL,	" 
					+ "     codeRight VARCHAR(100) NOT NULL," 
					+ "     provenance INT,                 "  //Type still under review
					+ "     PRIMARY KEY (idLeft, codeLeft,  " 
					+ "		idRight, codeRight, provenance) 			" 
					+ " )									");
            //provenance table sitll under development.
			sh.execute(	"CREATE TABLE                           " 
                    + "IF NOT EXISTS                            "
					+ "		provenance                          " 
					+ " (   id INT AUTO_INCREMENT PRIMARY KEY,  " 
					+ "     creator VARCHAR(100),                "
                    + "     linkPredicate VARCHAR(50) NOT NULL, "
                    + "     dateCreated DATE NOT NULL,          "
                    + "     dateUploaded DATE NOT NULL         "
					+ " )                                       ");   
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}

    /**
	 * Excecutes several SQL statements to drop the tables 
	 * @throws IDMapperException 
	 */
	public void dropSQLTables() throws BridgeDbSqlException
	{
    	Statement sh = createStatement();
		try 
		{
 			sh.execute("DROP TABLE  "
                    + "IF EXISTS    "
					+ "info         ");
			sh.execute("DROP TABLE  "
                    + "IF EXISTS    "
                    + "link         ");
            //provenance table sitll under development.
			sh.execute(	"DROP TABLE " 
                    + "IF EXISTS    "
					+ "provenance   ");   
            sh.close();
		} catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error dropping the tables ", e);
		}
	}

    @Override
    public void init(Provenance provenance) throws BridgeDbSqlException {
		try
		{
            if (possibleOpenConnection == null){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (possibleOpenConnection.isClosed()){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (!possibleOpenConnection.isValid(SQL_TIMEOUT)){
                possibleOpenConnection.close();
                possibleOpenConnection = sqlAccess.getConnection();
            }  
  			possibleOpenConnection.setAutoCommit(false);
			pstInsertLink = possibleOpenConnection.prepareStatement("INSERT INTO link    "
                    + "(idLeft, codeLeft,                       "   
                    + " idRight, codeRight,                     "
                    + " provenance )                            " 
                    + "VALUES (?, ?, ?, ?,                      " 
                    + provenance.getId() + ")                   ");
			pstCheckLink = possibleOpenConnection.prepareStatement("SELECT EXISTS "
                    + "(SELECT * FROM link      "
                    + "where                    "
                    + "   idLeft = ?            "
                    + "   AND codeLeft = ?      "   
                    + "   AND idRight = ?       "
                    + "   AND codeRight = ?     "
                    + "   AND provenance = " + provenance.getId() + ")");
		}
		catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error creating prepared statements", e);
		}
        insertCount = 0;
        doubleCount = 0;
	}

/*    @Override
    public void insertLink(URI source, String predicate, URI target) throws BridgeDbSqlException {
        DataSource sourceDataSource = DataSource.getByNameSpace(source.getNamespace());
        String sourceCode = sourceDataSource.getSystemCode();
        DataSource targetDataSource = DataSource.getByNameSpace(target.getNamespace());
        String targetCode = targetDataSource.getSystemCode();
        try {
            pstInsertLink.setString(1, source.getLocalName());
            pstInsertLink.setString(2, sourceDataSource.getSystemCode());
            pstInsertLink.setString(3, target.getLocalName());
            pstInsertLink.setString(4, targetDataSource.getSystemCode());
            pstInsertLink.setInt(5, 0);
            pstInsertLink.executeUpdate();
            insertCount++;
            if (insertCount % BLOCK_SIZE == 0){
                System.out.println("Inserted " + insertCount + " links loaded so far");
                possibleOpenConnection.commit();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            throw new BridgeDbSqlException ("Error inserting link");
        }
    }
*/
    @Override
    public void insertLink(Xref source, Xref target) throws BridgeDbSqlException {
        boolean exists = false;
        try {
            pstCheckLink.setString(1, source.getId());
            pstCheckLink.setString(2, source.getDataSource().getSystemCode());
            pstCheckLink.setString(3, target.getId());
            pstCheckLink.setString(4, target.getDataSource().getSystemCode());
            ResultSet rs = pstCheckLink.executeQuery();
            if (rs.next()) {
                exists = rs.getBoolean(1);
            }
            if (exists){
                doubleCount++;
                if (doubleCount % BLOCK_SIZE == 0){
                    System.out.println("Already skipped " + doubleCount + " links that already exist with this provenance");
                }
            } else {
                pstInsertLink.setString(1, source.getId());
                pstInsertLink.setString(2, source.getDataSource().getSystemCode());
                pstInsertLink.setString(3, target.getId());
                pstInsertLink.setString(4, target.getDataSource().getSystemCode());
                pstInsertLink.executeUpdate();
                insertCount++;
                if (insertCount % BLOCK_SIZE == 0){
                    System.out.println("Inserted " + insertCount + " links loaded so far");
                    possibleOpenConnection.commit();
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            throw new BridgeDbSqlException ("Error inserting link");
        }
    }

    @Override
    public void closeInput() throws BridgeDbSqlException {
        System.out.println ("Inserted " + this.insertCount + " links");
        System.out.println ("Skipped " + this.doubleCount + " links that where already there");
        if (possibleOpenConnection != null){
            try {
                possibleOpenConnection.commit();
                possibleOpenConnection.close();
            } catch (SQLException ex) {
               throw new BridgeDbSqlException ("Error closing connection ", ex);
            }
        }
    }

    // **** ProvenanceFactory Methods ****
    @Override
    public Provenance createProvenance(String createdBy, String predicate, long creation, long upload) 
            throws ProvenanceException{
        Provenance result = findProvenanceNumber(createdBy, predicate, creation);
        if (result != null){
            return result;
        }
        createMissingProvenance(createdBy, predicate, creation, upload);
        return findProvenanceNumber(createdBy, predicate, creation);
    }
    
    @Override
    public Provenance createProvenance(String createdBy, String predicate, long creation) throws ProvenanceException {
        Provenance result = findProvenanceNumber(createdBy, predicate, creation);
        if (result != null){
            return result;
        }
        createMissingProvenance(createdBy, predicate, creation, new GregorianCalendar().getTimeInMillis());
        return findProvenanceNumber(createdBy, predicate, creation);
    }

    @Override
    public Provenance createProvenace(Provenance first, Provenance second) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Provenance findProvenanceNumber(String createdBy, String predicate, long creation) throws ProvenanceException{
        Statement statement;
        try {
            statement = this.createStatement();
        } catch (BridgeDbSqlException ex) {
            throw new ProvenanceException ("Unable to create the statement ", ex);
        }
        String query = "SELECT id, creator, linkPredicate, dateCreated, dateUploaded from provenance "
                + "where creator = \"" + createdBy + "\""
                + "  AND linkPredicate = \"" + predicate +"\"" 
                + "  AND dateCreated = \"" + new Date(creation).toString() + "\"";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return new SimpleProvenance(rs.getInt("id"), 
                                            rs.getString("creator"), 
                                            rs.getString("linkPredicate"), 
                                            rs.getDate("dateCreated").getTime(), 
                                            rs.getDate("dateUploaded").getTime());
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.err.println(query);
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to check if Provenance already exists ", ex);
        }
    }

    private void createMissingProvenance(String createdBy, String predicate, long creation, long uploaded) throws ProvenanceException {
        Statement statement;
        try {
            statement = this.createStatement();
        } catch (BridgeDbSqlException ex) {
            throw new ProvenanceException ("Unable to create the statement ", ex);
        }
        String update = "INSERT INTO provenance "
                + "(creator, linkPredicate, dateCreated, dateUploaded) "
                + "VALUES ( \"" + createdBy + "\", \"" + predicate + "\", \"" 
                + new Date(creation).toString() + "\", \"" + new Date(uploaded).toString() + "\")";
        try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            System.err.println(update);
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to check if Provenance already exists ", ex);
        }
    }

    //***** IDMapper funtctions  *****
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        if (ref.getId() == null || ref.getDataSource() == null){
            return new HashSet<Xref>();
        }
        if (tgtDataSources.length == 0){
            return mapID(ref);
        }
        String query = "SELECT idRight as id, codeRight as code "
                + "FROM link      "
                + "where                    "
                + "   idLeft = \"" + ref.getId() + "\""
                + "   AND codeLeft = \"" + ref.getDataSource().getSystemCode() + "\""
                + "   AND (" 
                + "      codeRight = \"" + tgtDataSources[0].getSystemCode() + "\"";
        for (int i = 1; i < tgtDataSources.length; i++){
            query+= "      OR "
                   + "      codeRight = \"" + tgtDataSources[i].getSystemCode() + "\"";
        }
        query+= "   )";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    private Set<Xref> mapID(Xref ref) throws IDMapperException {
        String query = "SELECT distinct idRight as id, codeRight as code  "
                + "FROM link      "
                + "where                    "
                + "   idLeft = \"" + ref.getId() + "\""
                + "   AND codeLeft = \"" + ref.getDataSource().getSystemCode() + "\"";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    private Set<Xref> resultSetToXrefSet(ResultSet rs ) throws IDMapperException{
        HashSet<Xref> results = new HashSet<Xref>();
        try {
            while (rs.next()){
                String id = rs.getString("id");
                String sysCode = rs.getString("code");
                DataSource ds = DataSource.getBySystemCode(sysCode);
                Xref xref = new Xref(id, ds);
                results.add(xref);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }
    
    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        if (xref.getId() == null || xref.getDataSource() == null){
            return false;
        }
        String query = "SELECT EXISTS "
                + "(SELECT * FROM link      "
                + "where                    "
                + "   idLeft = \"" + xref.getId() + "\""
                + "   AND codeLeft = \"" + xref.getDataSource().getSystemCode() + "\""   
                + ")";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        String query = "SELECT distinct idRight as id, codeRight as code  "
                + "FROM link      "
                + "where                    "
                + "   idRight = \"" + text + "\" "
                + "UNION "
                + "SELECT distinct idLeft as id, codeLeft as code  "
                + "FROM link      "
                + "where                    "
                + "   idLeft = \"" + text + "\" ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return this;
    }

    private boolean isConnected = true;
    // In the case of DataCollection, there is no need to discard associated resources.
    
    @Override
    /** {@inheritDoc} */
    public void close() throws IDMapperException { 
        isConnected = false;
        if (this.possibleOpenConnection != null){
            try {
                this.possibleOpenConnection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    /** {@inheritDoc} */
    public boolean isConnected() { 
        if (isConnected){
            try {
                sqlAccess.getConnection();
                return true;
            } catch (BridgeDbSqlException ex) {
                return false;
            }
        }
        return isConnected; 
    }
    
    //***** IDMapperCapabilities funtctions  *****
    @Override
    public boolean isFreeSearchSupported() {
        return true;
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT codeLeft as code "
                + "FROM link      ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToDataSourceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    private Set<DataSource> resultSetToDataSourceSet(ResultSet rs ) throws IDMapperException{
        HashSet<DataSource> results = new HashSet<DataSource>();
        try {
            while (rs.next()){
                String sysCode = rs.getString("code");
                DataSource ds = DataSource.getBySystemCode(sysCode);
                results.add(ds);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT codeRight as code "
                + "FROM link      ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToDataSourceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        String query = "SELECT EXISTS "
                + "(SELECT * FROM link      "
                + "where                    "
                + "   codeLeft = \"" + src.getSystemCode() + "\""
                + "   AND codeRight = \"" + tgt.getSystemCode() + "\""   
                + ")";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public String getProperty(String key) {
        return null;
    }

    @Override
    public Set<String> getKeys() {
        return new HashSet<String>();
    }

    //*** Support method for interation ****
    /**
     * Gets the Xref currently at this possition in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each possition can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param possition
     * @return
     * @throws IDMapperException 
     */
    public Set<Xref> getXrefByPossition(int possition, int limit) throws IDMapperException {
        String query = "SELECT distinct idRight as id, codeRight as code  "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct idLeft as id, codeLeft as code  "
                + "FROM link      "
                + "LIMIT " + possition + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<Xref> results = new HashSet<Xref>();
            while (rs.next()){
                String id = rs.getString("id");
                String sysCode = rs.getString("code");
                DataSource ds = DataSource.getBySystemCode(sysCode);
                results.add(new Xref(id, ds));
            } 
            return results;
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    //*** Support method for interation ****
    /**
     * Gets the Xref currently at this possition in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each possition can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param possition
     * @return
     * @throws IDMapperException 
     */
    public Xref getXrefByPossition(int possition) throws IDMapperException {
        String query = "SELECT distinct idRight as id, codeRight as code  "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct idLeft as id, codeLeft as code  "
                + "FROM link      "
                + "LIMIT " + possition + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                String id = rs.getString("id");
                String sysCode = rs.getString("code");
                DataSource ds = DataSource.getBySystemCode(sysCode);
                return new Xref(id, ds);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    /**
     * Gets the Xref currently at this possition in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each possition can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param possition
     * @return
     * @throws IDMapperException 
     */
    public Set<Xref> getXrefByPossition(DataSource ds, int possition, int limit) throws IDMapperException {
        String query = "SELECT distinct idRight as id "
                + "FROM link      "
                + "WHERE "
                + "codeRight = \"" + ds.getSystemCode() + "\" "
                + "UNION "
                + "SELECT distinct idLeft as id  "
                + "FROM link      "
                + "WHERE "
                + "codeLeft = \"" + ds.getSystemCode() + "\" "
                + "LIMIT " + possition + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<Xref> results = new HashSet<Xref>();
            while (rs.next()){
                String id = rs.getString("id");
                results.add(new Xref(id, ds));
            } 
            return results;
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    /**
     * Gets the Xref currently at this possition in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each possition can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param possition
     * @return
     * @throws IDMapperException 
     */
    public Xref getXrefByPossition(DataSource ds, int possition) throws IDMapperException {
        String query = "SELECT distinct idRight as id "
                + "FROM link      "
                + "WHERE "
                + "codeRight = \"" + ds.getSystemCode() + "\" "
                + "UNION "
                + "SELECT distinct idLeft as id  "
                + "FROM link      "
                + "WHERE "
                + "codeLeft = \"" + ds.getSystemCode() + "\" "
                + "LIMIT " + possition + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                String id = rs.getString("id");
                return new Xref(id, ds);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public Iterable<Xref> getIterator(DataSource ds) throws IDMapperException {
        return new ByPossitionIterator(this, ds);
    }

    @Override
    public Iterable<Xref> getIterator() throws IDMapperException {
        return new ByPossitionIterator(this);
    }
}
