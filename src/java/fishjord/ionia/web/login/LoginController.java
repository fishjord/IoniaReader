/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.web.login;

import fishjord.ionia.db.MangaUser;
import fishjord.ionia.db.MangaUser.MangaUserRole;
import fishjord.ionia.jcr.MangaDAO;
import fishjord.ionia.jcr.MangaDAO.DAOSession;
import fishjord.ionia.web.ContextRelativeRedirectURL;
import fishjord.ionia.web.SingleObjectSessionUtils;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author fishjord
 */
@Controller
public class LoginController {
    @Autowired
    private MangaDAO dao;
    //private MangaReaderDB mangaDb;

    @RequestMapping("/logout.spr")
    public ModelAndView handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
	request.getSession().invalidate();
	return new ModelAndView("redirect:/bounce.spr");
    }

    @RequestMapping("/login.spr")
    public ModelAndView handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        //MangaUser user = mangaDb.getMangaUser(request.getUserPrincipal().getName());
        MangaUser user = new MangaUser(request.getUserPrincipal().getName(), request.getUserPrincipal().getName(), new Date(), new HashSet(), dao.login(request.getUserPrincipal().getName()));
        SingleObjectSessionUtils.addToSession(session, user);

	return new ModelAndView("redirect:/admin/bounce.spr");
    }
}
