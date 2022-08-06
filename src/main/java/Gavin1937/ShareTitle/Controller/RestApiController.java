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
        resp.put("sharetitle_count", db.numberOfWebsites());
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
        
        // generate response
        int toggleRes = db.toggleVisited(id);
        HttpStatus status = HttpStatus.OK;
        JSONObject resp = new JSONObject();
        if (toggleRes == -1)
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
            resp.put("is_visited", toggleRes);
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
