package Gavin1937.ShareTitle.Util;

import java.time.Instant;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * Collection of Utilities as static function
 */
public class Utilities
{
    
    public static int getUnixTimestampNow()
    {
        return (int)(Instant.now().toEpochMilli()/1000);
    }
    
    public static ResponseEntity<Object> genJsonResponse(JSONObject response, HttpStatus status)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new ResponseEntity<Object>(response.toString(), headers, status);
    }
    
    public static void logRequestResp(String logLevel, HttpServletRequest request, JSONObject resp)
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
    
    
}
