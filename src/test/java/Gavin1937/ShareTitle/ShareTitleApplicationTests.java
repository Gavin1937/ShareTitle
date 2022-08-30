package Gavin1937.ShareTitle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import Gavin1937.ShareTitle.Util.MyLogger;
import Gavin1937.ShareTitle.Util.ConfigManager;

@SpringBootTest()
class ShareTitleApplicationTests {
    
    private static ConfigManager config = ConfigManager.getInstance();
    
    @BeforeAll
    public static void setup()
        throws Exception
    {
        config.readConfig("./data/test_config.json");
        MyLogger.init(config.getLogPath(), config.getLogLevel());
    }
    
    @Test
    void contextLoads()
    {
    }
    
}
