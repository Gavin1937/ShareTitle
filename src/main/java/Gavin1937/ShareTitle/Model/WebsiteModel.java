package Gavin1937.ShareTitle.Model;

import org.json.JSONObject;

public class WebsiteModel
{
    
    public WebsiteModel(
        int id, String title,
        String url, String domain,
        int parentChild, int isVisited,
        int time
    )
    {
        __id = id;
        __title = title;
        __url = url;
        __domain = domain;
        __parentChild = parentChild;
        __isVisited = isVisited;
        __time = time;
    }
    
    public int getId()
    {
        return __id;
    }
    public String getTitle()
    {
        return __title;
    }
    public String getUrl()
    {
        return __url;
    }
    public String getDomain()
    {
        return __domain;
    }
    public int getParentChild()
    {
        return __parentChild;
    }
    public int getIsVisited()
    {
        return __isVisited;
    }
    public int getTime()
    {
        return __time;
    }
    
    public boolean isVisited()
    {
        return (__isVisited == 1);
    }
    
    public JSONObject toJson()
    {
        JSONObject ret = new JSONObject();
        ret.put("id", __id);
        ret.put("title", __title);
        ret.put("url", __url);
        ret.put("domain", __domain);
        ret.put("parent_child", __parentChild);
        ret.put("is_visited", __isVisited);
        ret.put("time", __time);
        return ret;
    }
    
    public void fromJson(JSONObject json)
    {
        __id = json.getInt("id");
        __title = json.getString("title");
        __url = json.getString("url");
        __domain = json.getString("domain");
        __parentChild = json.getInt("parent_child");
        __isVisited = json.getInt("is_visited");
        __time = json.getInt("time");
    }
    
    // private members
    private int __id;
    private String __title;
    private String __url;
    private String __domain;
    private int __parentChild;
    private int __isVisited;
    private int __time;
}
