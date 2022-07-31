package Gavin1937.ShareTitle.Controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Gavin1937.ShareTitle.Util.DbManager;
import Gavin1937.ShareTitle.Util.AuthManager;
import Gavin1937.ShareTitle.Util.MyLogger;


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
        
        String sess_uname = (String)request.getSession().getAttribute("username");
        if (username != null)
            model.addAttribute("username", username);
        else if (sess_uname != null)
            model.addAttribute("username", sess_uname);
        else
        {
            model.addAttribute("username", "anonymous");
            MyLogger.info("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.info("Login as anonymous user");
        }
        
        model.addAttribute("websites", db.getAllWebsites());
        return "showSharetitle";
    }
    
    
    @GetMapping(value="/login")
    public String showLogin(
        Model model,
        @CookieValue(value="username", required=false) String username,
        @CookieValue(value="auth_hash", required=false) String auth_hash,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        // Authentication
        String auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret == null)
            return "redirect:/sharetitle";
        
        return "login";
    }
    
    @PostMapping(value="/login")
    public String postLogin(
        RedirectAttributes redirectAttributes,
        @RequestParam(value="username") String username,
        @RequestParam(value="auth_hash") String auth_hash,
        @RequestParam(value="use_cookie", defaultValue="false") boolean use_cookie,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        MyLogger.debug("username = {}, auth_hash = {}", username, auth_hash);
        
        // Authentication
        String auth_ret = __doAuth(request, username, auth_hash);
        if (auth_ret != null) // auth fail
        {
            redirectAttributes.addFlashAttribute("auth_fail", true);
            response.setStatus(400);
            return auth_ret;
        }
        
        // auth success
        MyLogger.info("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
        MyLogger.info("[{}] Login Success", username);
        if (use_cookie)
        {
            response = AuthManager.issueAuthCookie(response, username, auth_hash);
            return "redirect:/sharetitle";
        }
        else
        {
            request = AuthManager.issueAuthSession(request, username, auth_hash);
            return "redirect:/sharetitle";
        }
        
    }
    
    @PostMapping(value="/logout")
    public String postLogout(
        @RequestParam(value="username") String username,
        HttpServletRequest request, HttpServletResponse response
    ) throws Exception
    {
        request.getSession().removeAttribute("username");
        request.getSession().removeAttribute("auth_hash");
        
        Cookie c1 = new Cookie("username", "");
        c1.setMaxAge(0);
        Cookie c2 = new Cookie("auth_hash", "");
        c2.setMaxAge(0);        
        response.addCookie(c1);
        response.addCookie(c2);
        
        return "redirect:/login";
    }
    
    // private helper function
    private String __doAuth(
        HttpServletRequest request,
        String username, String auth_hash
    ) throws Exception
    {
        // skip auth
        String ret = null;
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
                MyLogger.warn("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
                MyLogger.warn("Missing Authentication Cookie or Session");
                ret = "redirect:/login";
                skip_auth = true;
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
                username = sess_uname;
            }
            
            // auth fail
            if (!auth_res && !skip_auth)
            {
                MyLogger.warn("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
                MyLogger.warn("Invalid Authentication");
                ret = "redirect:/login";
            }
        }
        catch (Exception e)
        {
            MyLogger.error("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.error("Exception during auth: {}", e.getMessage());
            throw e;
        }
        
        if (ret == null)
        {
            MyLogger.info("{}, {} {}", request.getRemoteAddr(), request.getMethod(), request.getServletPath());
            MyLogger.info("Successfully Authenticate User [{}]", username);
        }
        return ret;
    }
    
    
    // private members
    private static DbManager db = null;
}