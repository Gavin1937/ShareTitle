package Gavin1937.ShareTitle;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Gavin1937.ShareTitle.Util.MyLogger;
import Gavin1937.ShareTitle.Util.ConfigManager;
import Gavin1937.ShareTitle.Util.DbManager;
import Gavin1937.ShareTitle.Util.AuthManager;
import Gavin1937.ShareTitle.Util.TitleParser;


@SpringBootApplication(scanBasePackages="Gavin1937.ShareTitle")
public class ShareTitleApplication
{
    
    private static ConfigManager config = ConfigManager.getInstance();
    private static DbManager db = DbManager.getInstance();
    
    
    public static void main(String[] args)
    {
        // init config, db, & logger
        try
        {
            if (args.length == 1)
            {
                config.readConfig(args[0]);
            }
            else // try to read config from current directory
            {
                config.readConfig("./config.json");
            }
            MyLogger.init(config.getLogPath(), config.getLogLevel());
            db.connect(config.getDbPath());
            AuthManager.connect(config.isAuthRequired(), config.getAuthDbPath());
            TitleParser.setParseScript(config.getTitleParseScriptPath());
        }
        catch (Exception e)
        {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
        
        
        MyLogger.info("Finish configuration, starting spring application...");
        MyLogger.trace("Trace msg for testing.");
        MyLogger.debug("Debug msg for testing.");
        MyLogger.info("Info msg for testing.");
        MyLogger.warn("Warn msg for testing.");
        MyLogger.error("Error msg for testing.");
        
        SpringApplication.run(ShareTitleApplication.class, args);
        
    }
    
    @PostConstruct
    public void postInit()
    {
        MyLogger.init(config.getLogPath(), config.getLogLevel());
    }
    
}
