package Gavin1937.ShareTitle.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.File;

import Gavin1937.ShareTitle.Model.WebsiteModel;
import Gavin1937.ShareTitle.Util.TitleParser;
import Gavin1937.ShareTitle.Util.MyLogger;
import Gavin1937.ShareTitle.Util.Utilities;


public class DbManager
{
    
    public static DbManager getInstance()
    {
        return __instance;
    }
    
    public static Connection getConnection()
    {
        return __dbConnection;
    }
    
    public void connect()
        throws Exception
    {
        __initDb("./sharetext.sqlite");
    }
    
    public void connect(String dbpath)
        throws Exception
    {
        __initDb(dbpath);
    }
    
    public ArrayList<Integer> numberOfWebsites()
        throws Exception
    {
        __checkConnection();        
        
        ArrayList<Integer> ret = new ArrayList<Integer>();
        ret.add(0);
        ret.add(0);
        ret.add(0);
        try
        {
            String sql = "SELECT COUNT(*) FROM websites;";
            MyLogger.debug("sql: {}", sql);
            Statement select = __dbConnection.createStatement();
            ResultSet rs = select.executeQuery(sql);
            if (rs.next())
                ret.set(0, rs.getInt(1));
            
            sql = "SELECT COUNT(*) FROM websites WHERE is_visited = 0;";
            MyLogger.debug("sql: {}", sql);
            select = __dbConnection.createStatement();
            rs = select.executeQuery(sql);
            if (rs.next())
                ret.set(1, rs.getInt(1));
            
            sql = "SELECT COUNT(*) FROM websites WHERE is_visited = 1;";
            MyLogger.debug("sql: {}", sql);
            select = __dbConnection.createStatement();
            rs = select.executeQuery(sql);
            if (rs.next())
                ret.set(2, rs.getInt(1));
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            return null;
        }
        return ret;
    }
    
    public int lastUpdateTime()
        throws Exception
    {
        __checkConnection();
        
        int ret = -1;
        try
        {
            String sql = "SELECT mtime from table_update_info WHERE name = \"websites\";";
            MyLogger.debug("sql: {}", sql);
            Statement select = __dbConnection.createStatement();
            ResultSet rs = select.executeQuery(sql);
            if (rs.next())
                ret = rs.getInt(1);
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            return -1;
        }
        
        return ret;
    }
    
    public WebsiteModel getWebsite(int id)
        throws Exception
    {
        __checkConnection();
        
        WebsiteModel ret = null;
        try
        {
            String sql = "SELECT * FROM websites WHERE id = ?;";
            MyLogger.debug("sql: {}", sql);
            PreparedStatement select = __dbConnection.prepareStatement(sql);
            select.setInt(1, id);
            ResultSet rs = select.executeQuery();
            if (rs.next())
            {
                ret = new WebsiteModel(
                    rs.getInt(1), rs.getString(2),
                    rs.getString(3), rs.getString(4),
                    rs.getInt(5), rs.getInt(6),
                    rs.getInt(7)
                );
            }
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            return null;
        }
        return ret;
    }
    
    /**
     * 
     * @param is_visit => int setting value of "is_visited" field.
     *  <ul>
     *  <li>If set to 1, query all sharetitles w/ "is_visited" = 1.</li>
     *  <li>If set to 0, query all sharetitles w/ "is_visited" = 0.</li>
     *  <li>Otherwise query all sharetitles.</li>
     *  </ul>
     *  
     * @param reverse => int setting order of return sharetitles.
     *  <ul>
     *  <li>If set to 1, order in ascending order by id.</li>
     *  <li>If set to 0, order in descending order by id.</li>
     *  </ul>
     *  
     * @return
     * @throws Exception
     */
    public ArrayList<WebsiteModel> getAllWebsites(int is_visit, int reverse)
        throws Exception
    {
        __checkConnection();        
        
        ArrayList<WebsiteModel> ret = new ArrayList<WebsiteModel>();
        try
        {
            // build sql from param
            String sql = "SELECT * FROM websites ";
            switch (is_visit)
            {
            case 0:
                sql += "WHERE is_visited = 0 ";
                break;
            case 1:
                sql += "WHERE is_visited = 1 ";
                break;
            default:
                break;
            }
            switch (reverse)
            {
            case 0: // false
                sql += "ORDER BY id ";
                break;
            case 1: // true
                sql += "ORDER BY id DESC ";
                break;
            default:
                sql += "ORDER BY id ";
                break;
            }
            sql += ";";
            
            
            MyLogger.debug("sql: {}", sql);
            Statement select = __dbConnection.createStatement();
            ResultSet rs = select.executeQuery(sql);
            while (rs.next())
            {
                ret.add(
                    new WebsiteModel(
                        rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4),
                        rs.getInt(5), rs.getInt(6),
                        rs.getInt(7)
                    )
                );
            }
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            return null;
        }
        return ret;
    }
    
    public WebsiteModel addWebsite(String text)
        throws Exception
    {
        __checkConnection();        
        
        WebsiteModel ret = null;
        try
        {
            // insert
            String sql = """
                INSERT INTO websites(title, url, domain, parent_child, time)
                VALUES(?, ?, ?, ?, ?)
            ;""";
            MyLogger.debug("sql: {}", sql);
            PreparedStatement insert = __dbConnection.prepareStatement(sql);
            
            if (TitleParser.parse(text))
            {
                insert.setString(1, TitleParser.getTitle());
                insert.setString(2, TitleParser.getUrl());
                insert.setString(3, TitleParser.getDomain());
                insert.setInt(4, TitleParser.getParentChild());
                insert.setInt(5, Utilities.getUnixTimestampNow());
                
                insert.executeUpdate();
            }
            else
                return null;
            
            // get newly inserted website
            sql = """
                SELECT * FROM websites
                WHERE id = (SELECT MAX(id) FROM websites)
            ;""";
            MyLogger.debug("sql: {}", sql);
            Statement select = __dbConnection.createStatement();
            ResultSet rs = select.executeQuery(sql);
            if (rs.next())
            {
                ret = new WebsiteModel(
                    rs.getInt(1), rs.getString(2),
                    rs.getString(3), rs.getString(4),
                    rs.getInt(5), rs.getInt(6),
                    rs.getInt(7)
                );
            }
            
            __updateMtime();
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            return null;
        }
        
        return ret;
    }
    
    public int deleteWebsite(int id)
        throws Exception
    {
        __checkConnection();
        
        if (!isWebsiteExists(id))
            return -1;
        
        try
        {
            // delete
            String sql = "DELETE FROM websites WHERE id = ?;";
            MyLogger.debug("sql: {}", sql);
            PreparedStatement delete = __dbConnection.prepareStatement(sql);
            
            delete.setInt(1, id);
            delete.executeUpdate();
            
            __updateMtime();
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            return -1;
        }
        return id;
    }
    
    public ArrayList<Integer> toggleVisited(int id)
        throws Exception
    {
        __checkConnection();
        
        if (!isWebsiteExists(id))
            return null;
        
        ArrayList<Integer> ret = new ArrayList<Integer>();
        try
        {
            // set visited
            String sql = "UPDATE websites SET is_visited = NOT is_visited, time = ? WHERE id = ?;";
            MyLogger.debug("sql: {}", sql);
            PreparedStatement setvisit = __dbConnection.prepareStatement(sql);
            setvisit.setInt(1, Utilities.getUnixTimestampNow());
            setvisit.setInt(2, id);
            setvisit.executeUpdate();
            
            // get latest is_visited value
            sql = "SELECT is_visited, time FROM websites WHERE id = ?;";
            PreparedStatement getIsVisited = __dbConnection.prepareStatement(sql);
            getIsVisited.setInt(1, id);
            ResultSet rs = getIsVisited.executeQuery();
            if (rs.next())
            {
                ret.add(rs.getInt(1));
                ret.add(rs.getInt(2));
                
            }
            
            __updateMtime();
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            return null;
        }
        return ret;
    }
    
    public boolean isWebsiteExists(int id)
        throws Exception
    {
        __checkConnection();
        
        boolean ret = false;
        try
        {
            // set visited
            String sql = "SELECT * FROM websites WHERE id = ?;";
            MyLogger.debug("sql: {}", sql);
            PreparedStatement checkexists = __dbConnection.prepareStatement(sql);
            
            checkexists.setInt(1, id);
            ResultSet rs = checkexists.executeQuery();
            ret = rs.next();
            
            __updateMtime();
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            return false;
        }
        return ret;
    }
    
    public void resetLastInsert()
        throws Exception
    {
        __checkConnection();
        
        int id = -1;
        try
        {
            // get last id
            String sql = "SELECT MAX(id) FROM websites;";
            MyLogger.debug("sql: {}", sql);
            Statement maxid = __dbConnection.createStatement();
            ResultSet rs = maxid.executeQuery(sql);
            if (rs.next())
                id = rs.getInt(1);
            
            // delete last entry
            sql = "DELETE FROM websites WHERE id = ?;";
            MyLogger.debug("sql: {}", sql);
            PreparedStatement delete = __dbConnection.prepareStatement(sql);
            delete.setInt(1, id);
            delete.executeUpdate();
            
            // decrement auto_increment counter
            sql = "UPDATE sqlite_sequence SET seq = seq-1;";
            MyLogger.debug("sql: {}", sql);
            PreparedStatement update = __dbConnection.prepareStatement(sql);
            update.executeUpdate();
            
            __updateMtime();
        }
        catch (Exception e)
        {
            throw e;
        }
    }
    
    
    // private helper functions
    private ArrayList<String> __parseSql(String sql)
    {
        int spos = 0;
        int epos = sql.indexOf(";", spos)+1;
        ArrayList<String> sql_lines = new ArrayList<String>();
        while (epos >= 0 && spos >= 0)
        {
            sql_lines.add(sql.substring(spos, epos).strip());
            spos = epos;
            epos = sql.indexOf(";", spos);
            if (epos == -1)
                break;
            epos += 1;
        }
        return sql_lines;
    }
    
    private void __initDb(String dbpath)
        throws Exception
    {
        try
        {
            // connect to db
            File dbfile = new File(dbpath);
            boolean invalidPath = (
                !dbpath.equals(":memory:") &&
                !dbfile.isFile() &&
                !dbfile.isDirectory() &&
                !dbfile.getParentFile().isDirectory()
            );
            
            String abspath;
            if (dbpath.equals(":memory:"))
                abspath = dbpath;
            else if (!invalidPath) // valid existing db
                abspath = dbfile.getCanonicalPath();
            else // invalid non-existing db, create a new one
                abspath = dbpath;
            __dbConnection = DriverManager.getConnection("jdbc:sqlite:"+abspath);
            
            // get sql from sql/init_db.sql
            ApplicationContext appContext = new ClassPathXmlApplicationContext();
            Resource resource = appContext.getResource("classpath:sql/init_db.sql");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            String sql = FileCopyUtils.copyToString(reader);;
            reader.close();
            ((ConfigurableApplicationContext)appContext).close();
            
            // parse sql string to individual statements
            ArrayList<String> sql_commands = __parseSql(sql);
            
            // execute statements
            for (String sc : sql_commands)
            {
                PreparedStatement ps = __dbConnection.prepareStatement(sc);
                ps.executeUpdate();
            }
            
        }
        catch (Exception e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            throw e;
        }
    }
    
    public void __checkConnection()
        throws Exception
    {
        if (__dbConnection == null)
        {
            MyLogger.warn("Did not connect to database.");
            throw new Exception("Did not connect to database.");
        }
    }
    
    private void __updateMtime()
        throws Exception
    {
        try
        {
            String sql = """
                UPDATE table_update_info
                SET mtime = ?
                WHERE name = \"websites\"
            ;""";
            PreparedStatement update = __dbConnection.prepareStatement(sql);
            update.setInt(1, Utilities.getUnixTimestampNow());
            update.executeUpdate();
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            throw e;
        }
    }
    
    
    // private constructor for singleton
    private DbManager() {}
    
    // destructor
    public void finalize() throws Exception
    {
        if (__dbConnection != null)
            __dbConnection.close();
    }
    
    
    // private members
    private static DbManager __instance = new DbManager();
    private static Connection __dbConnection = null;
}
