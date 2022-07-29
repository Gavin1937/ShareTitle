package Gavin1937.ShareTitle.Util;

import java.time.Instant;


/**
 * Collection of Utilities as static function
 */
public class Utilities
{
    
    public static int getUnixTimestampNow()
    {
        return (int)(Instant.now().toEpochMilli()/1000);
    }
    
}
