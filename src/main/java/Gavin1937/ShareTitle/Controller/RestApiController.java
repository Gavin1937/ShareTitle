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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * {
     *  "last_update_time":int,
     *  "sharetitle_count":int,
     *  "ok":boolean
     * }
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
     *  limit => [Optional] int limiting how many sharetitle returns.
     *  If supplied, this function will limit the "sharetitle" output.
     *  Otherwise function will return all the sharetitle
     * @return response json string:
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
     *  
     * @throws Exception
     */
    @GetMapping(value={"/allsharetitles", "/allsharetitles/{limit}"})
    public ResponseEntity<Object> getAllSharetitles(
        @PathVariable(value="limit", required=false) int limit,
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
        JSONArray sharetitle = new JSONArray();
        ArrayList<WebsiteModel> websites = db.getAllWebsites();
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
     * 
     * @param
     *  id => integer id exists in database
     *  
     * @return response json string:
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
     * @return response json string:
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
    
    
    // private helper function
    private ResponseEntity<Object> __doAuth(
        HttpServletRequest request,
        String username, String auth_hash
    ) throws Exception
    {
        ResponseEntity<Object> ret = null;
        try
        {
            // missing cookie
            if (AuthManager.isAuthRequired() && (username == null || auth_hash == null))
            {
                JSONObject resp = new JSONObject();
                resp.put("ok", false);
                resp.put("error", "Missing Authentication Cookie");
                
                Utilities.logRequestResp("WARN", request, resp);
                return Utilities.genJsonResponse(resp, HttpStatus.BAD_REQUEST);
            }
            // auth failed
            else if (AuthManager.isAuthRequired() && !AuthManager.auth(username, auth_hash))
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
