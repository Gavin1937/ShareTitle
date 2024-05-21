package Gavin1937.ShareTitle.Util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.json.JSONArray;

public class TitleParser
{
    
    public static boolean parse(String text)
        throws Exception
    {
        if (__parseScript == null || __parseScript.length() == 0)
            throw new Exception("Do not have parse script.");
        
        
        __clearMembers();
        
        // loop through all parse script and try to parse input text
        for (int i = 0; i < __parseScript.length(); ++i)
        {
            JSONObject obj = __parseScript.getJSONObject(i);
            
            Matcher m = Pattern.compile(obj.getString("regex"), Pattern.CASE_INSENSITIVE).matcher(text);
            if (m.find())
            {
                __title = m.group(1);
                __url = m.group(2);
                __domain = obj.getString("domain");
                __parentChild = obj.getInt("parent_child");
                return true;
            }
        }
        
        return false;
    }
    
    public static void loadParseScript()
        throws IOException
    {
        String content = FileUtils.readFileToString(new File(__path), "UTF-8");
        __parseScript = new JSONArray(content);
        MyLogger.debug("__parseScript: {}", __parseScript.toString());
    }
    
    public static void setParseScript(String path)
        throws IOException
    {
        __path = path;
        loadParseScript();
    }
    
    public static boolean isEmpty()
    {
        return (
            __title == null ||
            __url == null ||
            __domain == null ||
            __parentChild == -1
        );
    }
    
    public static String getTitle()
    {
        return __title;
    }
    public static String getDomain()
    {
        return __domain;
    }
    public static String getUrl()
    {
        return __url;
    }
    public static int getParentChild()
    {
        return __parentChild;
    }
    public static boolean isParent()
    {
        return (__parentChild == 0);
    }
    public static boolean isChild()
    {
        return (__parentChild == 1);
    }
    
    
    // private helper function
    private static void __clearMembers()
    {
        __title = null;
        __url = null;
        __domain = null;
        __parentChild = -1;
    }
    
    private TitleParser() {}
    
    
    // private members
    private static JSONArray __parseScript;
    private static String __path;
    private static String __title;
    private static String __url;
    private static String __domain;
    private static int __parentChild; // 0 (Parent), 1 (Child)
}
