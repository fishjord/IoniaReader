/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web.login;

import fishjord.mangareader.db.MangaReaderDB;
import fishjord.mangareader.db.MangaUser;
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
    public static String REDIRECT_SESSION_KEY = "redirect_to";
    
    @RequestMapping("/login.spr")
    public void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        MangaUser user = mangaDb.getMangaUser(request.getUserPrincipal().getName());
        SingleObjectSessionUtils.addToSession(session, user);
        
        String newUrl = (String)session.getAttribute(REDIRECT_SESSION_KEY);
        
        if(newUrl == null) {
            response.sendRedirect(newUrl);
        } else {
            response.sendRedirect(request.getContextPath());
        }
    }
}
