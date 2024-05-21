package Gavin1937.ShareTitle.Controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import Gavin1937.ShareTitle.Model.WebsiteModel;
import Gavin1937.ShareTitle.Util.DbManager;
import Gavin1937.ShareTitle.Util.AuthManager;
import Gavin1937.ShareTitle.Util.MyLogger;
import Gavin1937.ShareTitle.Util.Utilities;

@RestController
@RequestMapping("/api")
public class RestApiController
{
    
    public RestApiController()
    {
        db = DbManager.getInstance();
        MyLogger.info("Constructing RestApiController");
    }
    
    
    // api
    
    /**
     * GET database status
     * 
     * @return response json string:
     * <code>
     * {
     *  "last_update_time":int,
     *  "sharetitle_count":int,
     *  "sharetitle_visited_count":int,
     *  "sharetitle_unvisited_count":int,
     *  "ok":boolean
     * }
     * </code>
     * 
     * @throws Exception
     */
    @GetMapping(value="/status")
    public ResponseEntity<Object> getStatus(
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        ResponseEntity<Object> auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        JSONObject resp = new JSONObject();
        resp.put("ok", true);
        ArrayList<Integer> num = db.numberOfWebsites();
        resp.put("sharetitle_count", num.get(0));
        resp.put("sharetitle_unvisited_count", num.get(1));
        resp.put("sharetitle_visited_count", num.get(2));
        resp.put("last_update_time", db.lastUpdateTime());
        
        Utilities.logRequestResp("INFO", request, resp);
        return Utilities.genJsonResponse(resp, HttpStatus.OK);
    }
    
    
    /**
     * GET all sharetitles from database
     * 
     * @param
     *  is_visit => int request parameter setting value of "is_visited" field.
     *  <ul>
     *  <li>If set to 1, query all sharetitles w/ "is_visited" = 1.</li>
     *  <li>If set to 0, query all sharetitles w/ "is_visited" = 0.</li>
     *  <li>Otherwise query all sharetitles.</li>
     *  <li>Default = -1 (query all).</li>
     *  </ul>
     *  
     * @param
     *  reverse => int request parameter setting order of return sharetitles.
     *  <ul>
     *  <li>If set to 1, order in ascending order by id.</li>
     *  <li>If set to 0, order in descending order by id.</li>
     *  <li>Default = 1 (ascending order).</li>
     *  </ul>
     *  
     * @return response json string:
     * <code>
     * {
     *  "sharetitles": [
     *   {
     *    "id": id,
     *    "title": string,
     *    "url": string,
     *    "domain": string,
     *    "parent_child": int,
     *    "is_visited": int,
     *    "time": int (timestamp)
     *   },
     *   ...
     *  ],
     *  "length": int,
     *  "ok": boolean
     * }
     * </code>
     *  
     * @throws Exception
     */
    @GetMapping(value="/allsharetitles")
    public ResponseEntity<Object> getAllSharetitles(
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        @RequestParam(value="is_visit", defaultValue="-1") int is_visit,
        @RequestParam(value="reverse", defaultValue="0") int reverse,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        ResponseEntity<Object> auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        MyLogger.debug("is_visit: {}, reverse: {}", is_visit, reverse);
        
        // generate response
        JSONArray sharetitle = new JSONArray();
        int limit = -1;
        ArrayList<WebsiteModel> websites = db.getAllWebsites(is_visit, reverse);
        for (int idx = 0; idx < websites.size(); ++idx)
        {
            if (limit == 0)
                break;
            sharetitle.put(websites.get(idx).toJson());
            limit--;
        }
        JSONObject resp = new JSONObject();
        resp.put("ok", (sharetitle.length() >= 0));
        resp.put("length", sharetitle.length());
        resp.put("sharetitles", sharetitle);
        
        Utilities.logRequestResp("INFO", request, resp);
        return Utilities.genJsonResponse(resp, HttpStatus.OK);
    }
    
    
    /**
     * GET all sharetitles from database with limit
     * 
     * @param
     *  limit => int limiting how many sharetitle returns.
     *  <ul>
     *  <li>If limit is negative number, return all sharetitles. (same as getAllSharetitles())</li>
     *  </ul>
     *  
     * @param
     *  is_visit => int request parameter setting value of "is_visited" field.
     *  <ul>
     *  <li>If set to 1, query all sharetitles w/ "is_visited" = 1.</li>
     *  <li>If set to 0, query all sharetitles w/ "is_visited" = 0.</li>
     *  <li>Otherwise query all sharetitles.</li>
     *  <li>Default = -1 (query all).</li>
     *  </ul>
     *  
     * @param
     *  reverse => int request parameter setting order of return sharetitles.
     *  <ul>
     *  <li>If set to 1, order in ascending order by id.</li>
     *  <li>If set to 0, order in descending order by id.</li>
     *  <li>Default = 1 (ascending order).</li>
     *  </ul>
     *  
     * @return response json string:
     * <code>
     * {
     *  "sharetitles": [
     *   {
     *    "id": id,
     *    "title": string,
     *    "url": string,
     *    "domain": string,
     *    "parent_child": int,
     *    "is_visited": int,
     *    "time": int (timestamp)
     *   },
     *   ...
     *  ],
     *  "length": int,
     *  "ok": boolean
     * }
     * </code>
     *  
     * @throws Exception
     */
    @GetMapping(value="/allsharetitles/{limit}")
    public ResponseEntity<Object> getAllSharetitlesWithLimit(
        @PathVariable(value="limit") int limit,
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        @RequestParam(value="is_visit", defaultValue="-1") int is_visit,
        @RequestParam(value="reverse", defaultValue="0") int reverse,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        ResponseEntity<Object> auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        MyLogger.debug("is_visit: {}, reverse: {}", is_visit, reverse);
        
        // generate response
        JSONArray sharetitle = new JSONArray();
        ArrayList<WebsiteModel> websites = db.getAllWebsites(is_visit, reverse);
        for (int idx = 0; idx < websites.size(); ++idx)
        {
            if (limit == 0)
                break;
            sharetitle.put(websites.get(idx).toJson());
            limit--;
        }
        JSONObject resp = new JSONObject();
        resp.put("ok", (sharetitle.length() >= 0));
        resp.put("length", sharetitle.length());
        resp.put("sharetitles", sharetitle);
        
        Utilities.logRequestResp("INFO", request, resp);
        return Utilities.genJsonResponse(resp, HttpStatus.OK);
    }
    
    
    /**
     * GET query sharetitle(s) with parameters
     * 
     * @param
     *  page => [optional][query parameter] int page of query results.
     *  <ul>
     *  <li>Each page has 50 results, default 0</li>
     *  </ul>
     *  
     * @param
     *  limit => [optional][query parameter] int limit of query results in each page.
     *  <ul>
     *  <li>Default limit 50</li>
     *  <li>Use -1 for all</li>
     *  </ul>
     *  
     * @param
     *  order => [optional][query parameter] string order of result.
     *  <ul>
     *  <li>Can be either "ASC" or "DESC", order by id.</li>
     *  </ul>
     *  
     * @param
     *  id => [optional][query parameter] int id of sharetitle.
     *  
     * @param
     *  id_greater_then => [optional][query parameter] return sharetitle with id greater then this id.
     *  
     * @param
     *  id_greater_eq => [optional][query parameter] return sharetitle with id greater then & equal to this id.
     *  
     * @param
     *  id_less_then => [optional][query parameter] return sharetitle with id less then this id.
     *  
     * @param
     *  id_less_eq => [optional][query parameter] return sharetitle with id less then & equal to this id.
     *  
     * @param
     *  title => [optional][query parameter] str substring to search in sharetitle's title.
     *  
     * @param
     *  rtitle => [optional][query parameter] str regex to search in sharetitle's title.
     *  
     * @param
     *  url => [optional][query parameter] str substring to search in sharetitle's url.
     *  
     * @param
     *  rurl => [optional][query parameter] str regex to search in sharetitle's url.
     *  
     * @param
     *  domain => [optional][query parameter] str domain of sharetitle.
     *  
     * @param
     *  parent_child => [optional][query parameter] int value of sharetitle's parent_child status, either 0 or 1.
     *  <ul>
     *  <li>0 => parent</li>
     *  <li>1 => child</li>
     *  <li>You can set it to "parent" (0) or "child" (1) for readability.</li>
     *  <li>You can query both parent and child by supplying string "all".</li>
     *  </ul>
     *  
     * @param
     *  is_visited => [optional][query parameter] int value of sharetitle's is_visited status, either 0 or 1.
     *  <ul>
     *  <li>0 => unvisited</li>
     *  <li>1 => visited</li>
     *  <li>You can set it to "unvisited" (0) or "visited" (1) for readability.</li>
     *  <li>You can query both visited and unvisited by supplying string "all".</li>
     *  </ul>
     *  
     * @param
     *  time_until => [optional][query parameter] int value of sharetitle's time.
     *  <ul>
     *  <li>Integer unix timestamp</li>
     *  <li>When querying, this function will search for all sharetitles where sharetitle.time <= time_util</li>
     *  <li>You can set it to "now" for current unix timestamp</li>
     *  </ul>
     *  
     * @param
     *  time_after => [optional][query parameter] int value of sharetitle's time.
     *  <ul>
     *  <li>Integer unix timestamp</li>
     *  <li>When querying, this function will search for all sharetitles where sharetitle.time >= time_after</li>
     *  <li>You can set it to "now" for current unix timestamp</li>
     *  </ul>
     *  
     * @return response json string:
     * <code>
     * {
     *  "sharetitles": [
     *   {
     *    "id": id,
     *    "title": string,
     *    "url": string,
     *    "domain": string,
     *    "parent_child": int,
     *    "is_visited": int,
     *    "time": int (timestamp)
     *   },
     *   ...
     *  ],
     *  "length": int,
     *  "ok": boolean
     * }
     * </code>
     *  
     * @throws Exception
     */
    @GetMapping(value="/query")
    public ResponseEntity<Object> querySharetitles(
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        @RequestParam(value="page", required=false, defaultValue="0") Integer page,
        @RequestParam(value="limit", required=false, defaultValue="50") Integer limit,
        @RequestParam(value="order", required=false, defaultValue="ASC") String order,
        @RequestParam(value="id", required=false) String id,
        @RequestParam(value="id_greater_then", required=false) String id_greater_then,
        @RequestParam(value="id_greater_eq", required=false) String id_greater_eq,
        @RequestParam(value="id_less_then", required=false) String id_less_then,
        @RequestParam(value="id_less_eq", required=false) String id_less_eq,
        @RequestParam(value="title", required=false) String title,
        @RequestParam(value="rtitle", required=false) String rtitle,
        @RequestParam(value="url", required=false) String url,
        @RequestParam(value="rurl", required=false) String rurl,
        @RequestParam(value="domain", required=false) String domain,
        @RequestParam(value="parent_child", required=false) String parent_child,
        @RequestParam(value="is_visited", required=false) String is_visited,
        @RequestParam(value="time_until", required=false) String time_until,
        @RequestParam(value="time_after", required=false) String time_after,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        ResponseEntity<Object> auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        // generate response
        JSONArray sharetitle = new JSONArray();
        JSONObject options = new JSONObject();
        if (id != null) { options.put("id", id); }
        if (id_greater_then != null) { options.put("id_greater_then", id_greater_then); }
        if (id_greater_eq != null) { options.put("id_greater_eq", id_greater_eq); }
        if (id_less_then != null) { options.put("id_less_then", id_less_then); }
        if (id_less_eq != null) { options.put("id_less_eq", id_less_eq); }
        if (title != null) { options.put("title", title); }
        if (rtitle != null) { options.put("rtitle", rtitle); }
        if (url != null) { options.put("url", url); }
        if (rurl != null) { options.put("rurl", rurl); }
        if (domain != null) { options.put("domain", domain); }
        if (parent_child != null) { options.put("parent_child", parent_child); }
        if (is_visited != null) { options.put("is_visited", is_visited); }
        if (time_until != null) { options.put("time_until", time_until); }
        if (time_after != null) { options.put("time_after", time_after); }
        int offset = page * limit;
        
        MyLogger.debug("limit: {}", limit);
        MyLogger.debug("offset: {}", offset);
        MyLogger.debug("order: {}", order);
        MyLogger.debug("options: {}", options.toString());
        
        ArrayList<WebsiteModel> websites = db.queryWebsite(limit, offset, order, options);
        for (int idx = 0; idx < websites.size(); ++idx)
        {
            sharetitle.put(websites.get(idx).toJson());
        }
        JSONObject resp = new JSONObject();
        resp.put("ok", (sharetitle.length() >= 0));
        resp.put("length", sharetitle.length());
        resp.put("sharetitles", sharetitle);
        
        Utilities.logRequestResp("INFO", request, resp);
        return Utilities.genJsonResponse(resp, HttpStatus.OK);
    }
    
    
    /**
     * GET sharetitle form database specified by id
     * 
     * @param
     *  id => integer id exists in database
     *  
     * @return response json string:
     * <code>
     * {
     *  "sharetitle": {
     *   "domain": string,
     *   "is_visited": int,
     *   "id": int,
     *   "time": int,
     *   "title": string,
     *   "url": string,
     *   "parent_child": int
     *  },
     *  "ok": boolean
     * }
     * </code>
     *  
     * @throws Exception
     */
    @GetMapping(value={"/sharetitle/{id}"})
    public ResponseEntity<Object> getSharetitle(
        @PathVariable(value="id") int id,
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        ResponseEntity<Object> auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        MyLogger.debug("id: {}", id);
        
        // generate response
        WebsiteModel website = db.getWebsite(id);
        HttpStatus status = HttpStatus.OK;
        JSONObject resp = new JSONObject();
        if (website == null)
        {
            status = HttpStatus.BAD_REQUEST;
            resp.put("ok", false);
            resp.put("error", "Input id does not exists.");
            Utilities.logRequestResp("WARN", request, resp);
        }
        else
        {
            status = HttpStatus.OK;
            resp.put("ok", true);
            resp.put("sharetitle", website.toJson());
            Utilities.logRequestResp("INFO", request, resp);
        }
        
        return Utilities.genJsonResponse(resp, status);
    }
    
    
    /**
     * POST new sharetitle to database, (only allow Content-Type: text/plain)
     * 
     * @param
     *  data => plain text title in POST request body
     *  
     * @return response json string:
     * <code>
     * {
     *  "sharetitle": {
     *   "domain": string,
     *   "is_visited": int,
     *   "id": int,
     *   "time": int,
     *   "title": string,
     *   "url": string,
     *   "parent_child": int
     *  },
     *  "ok": boolean
     * }
     * </code>
     *  
     * @throws Exception
     */
    @PostMapping(value="/sharetitle", headers="Content-Type=text/plain")
    public ResponseEntity<Object> postSharetitle(
        @RequestBody(required=false) String data,
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        ResponseEntity<Object> auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        MyLogger.debug("data: {}", data);
        
        // handle bad input data
        if (data == null || data.strip().isEmpty())
        {
            JSONObject resp = new JSONObject();
            resp.put("ok", false);
            resp.put("error", "Input text cannot be empty");
            
            Utilities.logRequestResp("WARN", request, resp);
            return Utilities.genJsonResponse(resp, HttpStatus.BAD_REQUEST);
        }
        
        // generate response
        JSONObject resp = new JSONObject();
        WebsiteModel entry = db.addWebsite(data.strip());
        if (entry == null)
        {
            resp.put("ok", false);
            resp.put("error", "Cannot parse input text");
            Utilities.logRequestResp("WARN", request, resp);
            return Utilities.genJsonResponse(resp, HttpStatus.BAD_REQUEST);
        }
        resp.put("ok", true);
        resp.put("sharetitle", entry.toJson());
        
        Utilities.logRequestResp("INFO", request, resp);
        return Utilities.genJsonResponse(resp, HttpStatus.OK);
    }
    
    
    /**
     * DELETE sharetitle from database specified by id
     * 
     * @param
     *  id => integer id exists in database
     *  
     * @return response json string:
     * <code>
     * {
     *  "id": int,
     *  "ok": boolean
     * }
     * </code>
     *  
     * @throws Exception
     */
    @DeleteMapping(value={"/sharetitle/{id}"})
    public ResponseEntity<Object> deleteSharetitle(
        @PathVariable(value="id") int id,
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        ResponseEntity<Object> auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        MyLogger.debug("id: {}", id);
        
        // generate response
        int retId = db.deleteWebsite(id);
        HttpStatus status = HttpStatus.OK;
        JSONObject resp = new JSONObject();
        if (retId == -1)
        {
            status = HttpStatus.BAD_REQUEST;
            resp.put("ok", false);
            resp.put("error", "Input id does not exists.");
            Utilities.logRequestResp("WARN", request, resp);
        }
        else
        {
            status = HttpStatus.OK;
            resp.put("ok", true);
            resp.put("id", retId);
            Utilities.logRequestResp("INFO", request, resp);
        }
        
        return Utilities.genJsonResponse(resp, status);
    }
    
    
    /**
     * PUT toggle is_visited field of a sharetitle in database specified by id
     * 
     * @param
     *  id => integer id exists in database
     *  
     * @return response json string:
     * <code>
     * {
     *  "id": int,
     *  "is_visited": int,
     *  "time": int,
     *  "ok": boolean
     * }
     * </code>
     *  
     * @throws Exception
     */
    @PutMapping(value={"/sharetitle/{id}"})
    public ResponseEntity<Object> putToggleVisited(
        @PathVariable(value="id") int id,
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        ResponseEntity<Object> auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        MyLogger.debug("id: {}", id);
        
        // generate response
        ArrayList<Integer> toggleRes = db.toggleVisited(id);
        HttpStatus status = HttpStatus.OK;
        JSONObject resp = new JSONObject();
        if (toggleRes == null)
        {
            status = HttpStatus.BAD_REQUEST;
            resp.put("ok", false);
            resp.put("error", "Input id does not exists.");
            Utilities.logRequestResp("WARN", request, resp);
        }
        else
        {
            status = HttpStatus.OK;
            resp.put("ok", true);
            resp.put("is_visited", toggleRes.get(0));
            resp.put("time", toggleRes.get(1));
            resp.put("id", id);
            Utilities.logRequestResp("INFO", request, resp);
        }
        
        return Utilities.genJsonResponse(resp, status);
    }
    
    
    // private helper function
    private ResponseEntity<Object> __doAuth(
        HttpServletRequest request,
        String username, String auth_hash
    ) throws Exception
    {
        // skip auth
        ResponseEntity<Object> ret = null;
        if (!AuthManager.isAuthRequired())
            return ret;
        
        MyLogger.debug("username: {}, auth_hash: {}", username, auth_hash);
        
        try
        {
            String sess_uname = (String)request.getSession().getAttribute("username");
            String sess_authh = (String)request.getSession().getAttribute("auth_hash");
            
            // check cookie or session auth
            boolean no_session = 
                (AuthManager.isAuthRequired() && (sess_uname == null || sess_authh == null));
            boolean no_cookie = 
                (AuthManager.isAuthRequired() && (username == null || auth_hash == null));
            boolean auth_res = false;
            boolean skip_auth = false;
            
            if (no_session && no_cookie)
            {
                JSONObject resp = new JSONObject();
                resp.put("ok", false);
                resp.put("error", "Missing Authentication Cookie");
                
                Utilities.logRequestResp("WARN", request, resp);
                return Utilities.genJsonResponse(resp, HttpStatus.BAD_REQUEST);
            }
            else if (no_session)
            {
                skip_auth = false;
                auth_res = AuthManager.auth(username, auth_hash);
            }
            else if (no_cookie)
            {
                skip_auth = false;
                auth_res = AuthManager.auth(sess_uname, sess_authh);
            }
            
            // auth fail
            if (!auth_res && !skip_auth)
            {
                JSONObject resp = new JSONObject();
                resp.put("ok", false);
                resp.put("error", "Invalid Authentication");
                Utilities.logRequestResp("WARN", request, resp);
                ret = Utilities.genJsonResponse(resp, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e)
        {
            MyLogger.error("Exception during auth: {}", e.getMessage());
            throw e;
        }
        return ret;
    }
    
    
    // private members
    private static DbManager db = null;
}
