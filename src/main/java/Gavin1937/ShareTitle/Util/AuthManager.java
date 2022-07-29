package Gavin1937.ShareTitle.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.File;

import Gavin1937.ShareTitle.Util.MyLogger;
import Gavin1937.ShareTitle.Util.Utilities;


public class AuthManager
{
    
    public static void connect(boolean require_auth, String dbpath)
        throws Exception
    {
        __requireAuth = require_auth;
        if (__requireAuth)
            __initDb(dbpath);
    }
    
    public static boolean auth(String username, String auth_hash)
        throws Exception
    {
        __checkConnection();
        
        if (__requireAuth == false)
            return true;
        
        boolean ret = false;
        try
        {
            String sql = """
                SELECT auth_hash = ?
                FROM auth WHERE username = ?
            ;""";
            PreparedStatement compare = __dbConnection.prepareStatement(sql);
            compare.setString(1, auth_hash);
            compare.setString(2, username);
            ResultSet rs = compare.executeQuery();
            if (rs.next())
                ret = rs.getBoolean(1);
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            throw e;
        }
        
        return ret;
    }
    
    public static boolean register(String username, String auth_hash)
        throws Exception
    {
        __checkConnection();
        
        boolean ret = false;
        
        try
        {
            String sql = "INSERT INTO auth VALUES (?, ?);";
            PreparedStatement insert = __dbConnection.prepareStatement(sql);
            insert.executeUpdate();
            __updateMtime();
            ret = true;
        }
        catch (SQLException e)
        {
            MyLogger.error("SQLException: " + e.getMessage());
            // duplicate username
            if (e.getMessage().toLowerCase().contains("unique constraint"))
                return false;
            throw e;
        }
        
        return ret;
    }
    
    public static boolean isAuthRequired()
        throws Exception
    {
        __checkConnection();
        return __requireAuth;
    }
    
    
    // private helper functions
    private static ArrayList<String> __parseSql(String sql)
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
    
    private static void __initDb(String dbpath)
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
            Resource resource = appContext.getResource("classpath:sql/init_auth_db.sql");
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
    
    public static void __checkConnection()
        throws Exception
    {
        if (__dbConnection == null)
        {
            MyLogger.warn("Did not connect to database.");
            throw new Exception("Did not connect to database.");
        }
    }
    
    private static void __updateMtime()
        throws Exception
    {
        try
        {
            String sql = """
                UPDATE table_update_info
                SET mtime = ?
                WHERE name = \"auth\"
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
    private AuthManager() {}
    
    // destructor
    public void finalize() throws Exception
    {
        if (__dbConnection != null)
            __dbConnection.close();
    }
    
    
    // private members
    private static Connection __dbConnection = null;
    private static boolean __requireAuth = false;
}
