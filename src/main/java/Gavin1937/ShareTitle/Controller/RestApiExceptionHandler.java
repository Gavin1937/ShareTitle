package Gavin1937.ShareTitle.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

import Gavin1937.ShareTitle.Util.Utilities;


@ControllerAdvice
public class RestApiExceptionHandler extends ResponseEntityExceptionHandler    
{
    
    @Autowired
    private HttpServletRequest servlet_request;
    
    @Override
    protected ResponseEntity<Object>
    handleHttpMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException ex,
        HttpHeaders headers, HttpStatus status,
        WebRequest request
    )
    {
        JSONObject resp = new JSONObject();
        resp.put("ok", false);
        resp.put("error", "Content-Type must be text/plain");
        
        Utilities.logRequestResp("WARN", servlet_request, resp);
        return Utilities.genJsonResponse(resp, status);
    }
    
    // Override handleServletRequestBindingException()
    // to handle missing header/cookie parameter exceptions
}

