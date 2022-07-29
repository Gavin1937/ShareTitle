package Gavin1937.ShareTitle.Controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Gavin1937.ShareTitle.Model.WebsiteModel;
import Gavin1937.ShareTitle.Util.DbManager;
import Gavin1937.ShareTitle.Util.MyLogger;

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
    public ResponseEntity<String> getStatus(
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        JSONObject resp = new JSONObject();
        resp.put("ok", true);
        resp.put("sharetitle_count", db.numberOfWebsites());
        resp.put("last_update_time", db.lastUpdateTime());
        
        __logRequestResp("INFO", request, resp);
        return __genJsonResponse(resp, HttpStatus.OK);
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
    public ResponseEntity<String> getAllSharetitles(
        @PathVariable(value="limit", required=false) String limit,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // generate sharetitle array
        int intlimit = -1;
        try
        {
            if (limit != null && !limit.isEmpty())
                intlimit = Integer.valueOf(limit);
        }
        catch (Exception e)
        {
            JSONObject resp = new JSONObject();
            resp.put("ok", false);
            resp.put("error", "Invalid limit input, not an integer.");
            __logRequestResp("WARN", request, resp);
            return __genJsonResponse(resp, HttpStatus.BAD_REQUEST);
        }
        JSONArray sharetitle = new JSONArray();
        ArrayList<WebsiteModel> websites = db.getAllWebsites();
        for (int idx = 0; idx < websites.size(); ++idx)
        {
            if (intlimit == 0)
                break;
            sharetitle.put(websites.get(idx).toJson());
            intlimit--;
        }
        JSONObject resp = new JSONObject();
        resp.put("ok", (sharetitle.length() >= 0));
        resp.put("length", sharetitle.length());
        resp.put("sharetitles", sharetitle);
        
        
        __logRequestResp("INFO", request, resp);
        return __genJsonResponse(resp, HttpStatus.OK);
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
    public ResponseEntity<String> getSharetitle(
        @PathVariable(value="id") String id,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        int int_id = -1;
        try
        {
            int_id = Integer.parseInt(id);
        }
        catch (Exception e)
        {
            JSONObject resp = new JSONObject();
            resp.put("ok", false);
            resp.put("error", "Input id must be an integer.");
            __logRequestResp("WARN", request, resp);
            
            return __genJsonResponse(resp, HttpStatus.BAD_REQUEST);
        }
        
        WebsiteModel website = db.getWebsite(int_id);
        HttpStatus status = HttpStatus.OK;
        JSONObject resp = new JSONObject();
        if (website == null)
        {
            status = HttpStatus.BAD_REQUEST;
            resp.put("ok", false);
            resp.put("error", "Input id does not exists.");
            __logRequestResp("WARN", request, resp);
        }
        else
        {
            status = HttpStatus.OK;
            resp.put("ok", true);
            resp.put("sharetitle", website.toJson());
            __logRequestResp("INFO", request, resp);
        }
        
        return __genJsonResponse(resp, status);
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
    public ResponseEntity<String> postSharetitle(
        @RequestBody String data,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        JSONObject resp = new JSONObject();
        WebsiteModel entry = db.addWebsite(data);
        resp.put("ok", (entry != null));
        resp.put("sharetitle", entry.toJson());
        
        __logRequestResp("INFO", request, resp);
        return __genJsonResponse(resp, HttpStatus.OK);
    }
    
    
    // private helper function
    private ResponseEntity<String> __genJsonResponse(JSONObject response, HttpStatus status)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new ResponseEntity<String>(response.toString(), headers, status);
    }
    
    private void __logRequestResp(String logLevel, HttpServletRequest request, JSONObject resp)
    {
        switch (logLevel.toUpperCase())
        {
        case "TRACE":
            MyLogger.trace("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.trace("Response data: {}", resp.toString());
            break;
        case "DEBUG":
            MyLogger.debug("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.debug("Response data: {}", resp.toString());
            break;
        case "INFO":
            MyLogger.info("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.info("Response data: {}", resp.toString());
            break;
        case "WARN":
            MyLogger.warn("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.warn("{}", resp.getString("error"));
            break;
        case "ERROR":
            MyLogger.error("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.error("{}", resp.getString("error"));
            break;
        default:
            MyLogger.info("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.info("Response data: {}", resp.toString());
            break;
        }
    }
    
    
    // private members
    private static DbManager db = null;
}
