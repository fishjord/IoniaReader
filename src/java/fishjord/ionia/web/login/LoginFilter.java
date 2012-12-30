/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web.login;

import fishjord.mangareader.db.MangaUser;
import fishjord.mangareader.web.ContextRelativeRedirectURL;
import fishjord.mangareader.web.SingleObjectSessionUtils;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author fishjord
 */
public class LoginFilter implements Filter {
    
    public void init(FilterConfig fc) throws ServletException {
    }

    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)sr;
        HttpServletResponse response = (HttpServletResponse)sr1;
        HttpSession session = request.getSession();
        
        System.err.println("Request for protected resource " + request.getRequestURL().toString());
        
        if(!SingleObjectSessionUtils.isInSession(session, MangaUser.class)) {
            System.err.println("No user in session, redirecting to login page");
            SingleObjectSessionUtils.addToSession(session, new ContextRelativeRedirectURL(request));
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/login.spr"));
        } else {
            fc.doFilter(sr, sr1);
        }        
    }

    public void destroy() {
    }
    
}
