package Gavin1937.ShareTitle.Util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import ch.qos.logback.classic.Level;


public class ConfigManager
{
    
    public static ConfigManager getInstance()
    {
        if (__instance == null)
            __instance = new ConfigManager();
        return __instance;
    }
    
    public void readConfig(String config_path)
        throws IOException
    {
        String content = FileUtils.readFileToString(new File(config_path), "UTF-8");
        __config = new JSONObject(content);
    }
    
    public JSONObject getJson()
    {
        return __config;
    }
    
    public boolean isAuthRequired()
    {
        return __config.getBoolean("require_auth");
    }
    
    public String getAuthDbPath()
    {
        if (!isAuthRequired())
            return null;
        return __config.getString("auth_database_path");
    }
    
    public String getDbPath()
    {
        return __config.getString("database_path");
    }
    
    public String getTitleParseScriptPath()
    {
        return __config.getString("title_parse_script");
    }
    
    public String getLogPath()
    {
        return __config.getString("log_path");
    }
    
    public Level getLogLevel()
    {
        String level = __config.getString("log_level").toUpperCase();
        switch (level)
        {
        case "TRACE":
            return Level.TRACE;
        case "DEBUG":
            return Level.DEBUG;
        case "INFO":
            return Level.INFO;
        case "WARN":
            return Level.WARN;
        case "ERROR":
            return Level.ERROR;
        default:
            return Level.INFO;
        }
    }
    
    
    // private constructor for singleton
    private ConfigManager() {}
    
    
    // private members 
    private static ConfigManager __instance = null;
    private JSONObject __config = new JSONObject();
    
}
