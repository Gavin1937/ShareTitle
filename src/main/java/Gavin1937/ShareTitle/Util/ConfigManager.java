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
        if (__Instance == null)
            __Instance = new ConfigManager();
        return __Instance;
    }
    
    public void readConfig(String config_path)
        throws IOException
    {
        String content = FileUtils.readFileToString(new File(config_path), "UTF-8");
        __Config = new JSONObject(content);
    }
    
    public JSONObject getJson()
    {
        return __Config;
    }
    
    public String getDbPath()
    {
        return __Config.getString("database_path");
    }
    
    public String getTitleParseScriptPath()
    {
        return __Config.getString("title_parse_script");
    }
    
    public String getLogPath()
    {
        return __Config.getString("log_path");
    }
    
    public Level getLogLevel()
    {
        String level = __Config.getString("log_level").toUpperCase();
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
    private static ConfigManager __Instance = null;
    private JSONObject __Config = new JSONObject();
    
}
