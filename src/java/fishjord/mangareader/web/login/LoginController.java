/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web.login;

import fishjord.mangareader.db.MangaReaderDB;
import fishjord.mangareader.db.MangaUser;
import fishjord.mangareader.web.ContextRelativeRedirectURL;
import fishjord.mangareader.web.SingleObjectSessionUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author fishjord
 */
@Controller
public class LoginController {
    
    @Autowired
    private MangaReaderDB mangaDb;
    
    @RequestMapping("/login.spr")
    public void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        MangaUser user = mangaDb.getMangaUser(request.getUserPrincipal().getName());
        SingleObjectSessionUtils.addToSession(session, user);
        
        ContextRelativeRedirectURL url = SingleObjectSessionUtils.getFromSession(session, ContextRelativeRedirectURL.class);
        
        System.err.println("User " + user.getDisplayName() + " logged in, redirecting to " + url);
        
        if(url != null) {
            response.sendRedirect(response.encodeRedirectURL(url.getRedirectUrl()));
        } else {
            response.sendRedirect(request.getContextPath());
        }
    }
}
