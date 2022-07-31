package Gavin1937.ShareTitle.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import Gavin1937.ShareTitle.Util.DbManager;
import Gavin1937.ShareTitle.Util.AuthManager;
import Gavin1937.ShareTitle.Util.MyLogger;
import Gavin1937.ShareTitle.Util.Utilities;


@Controller
public class WebappController
{
    
    public WebappController()
    {
        db = DbManager.getInstance();
        MyLogger.info("Constructing WebappController");
    }
    
    // expose views
    @GetMapping("/sharetitle")
    public String showSharetitle(
        Model model,
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        String auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null)
            return auth_ret;
        
        model.addAttribute("websites", db.getAllWebsites());
        return "showSharetitle";
    }
    
    
    // private helper function
    private String __doAuth(
        HttpServletRequest request,
        String username, String auth_hash
    ) throws Exception
    {
        String ret = null;
        try
        {
            // missing cookie
            if (AuthManager.isAuthRequired() && (username == null || auth_hash == null))
            {
                MyLogger.warn("Missing Authentication Cookie");
                ret = "login";
            }
            // auth failed
            else if (AuthManager.isAuthRequired() && !AuthManager.auth(username, auth_hash))
            {
                MyLogger.warn("Invalid Authentication");
                ret = "login";
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