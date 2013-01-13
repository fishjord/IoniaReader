/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.web;

import fishjord.ionia.db.Chapter;
import fishjord.ionia.db.Manga;
import fishjord.ionia.db.MangaUser;
import fishjord.ionia.jcr.MangaDAO;
import fishjord.ionia.jcr.MangaDAO.DAOSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author fishjord
 */
@Controller
public class MangaController {

    @Autowired
    private MangaDAO dao;

    private DAOSession getDAOSession(HttpSession session) {
        DAOSession daoSession;
        MangaUser user = SingleObjectSessionUtils.getFromSession(session, MangaUser.class);
        if (user != null && user.getSession() != null) {
            daoSession = user.getSession();
        } else {
            daoSession = dao.getAnonSession();
        }

        return daoSession;
    }

    @RequestMapping(value = "/list.spr")
    public ModelAndView listManga(HttpSession session) {
        DAOSession daoSession = getDAOSession(session);

        ModelAndView mav = new ModelAndView("list");
        mav.addObject("mangaList", daoSession.iterator());
        return mav;
    }

    @RequestMapping("/*")
    public ModelAndView mangaSummary(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DAOSession daoSession = getDAOSession(session);
        MangaUser user = SingleObjectSessionUtils.getFromSession(session, MangaUser.class);

        String[] lexemes = request.getPathInfo().substring(1).split("/");
        String id = lexemes[0];
        Manga m = daoSession.getManga(id);

        if (m == null) {
            response.sendError(404);
            return null;
        }

        if (m.isMature() && (user == null || !user.isMatureOk())) {
            ContextRelativeRedirectURL redirect = new ContextRelativeRedirectURL(request);
            SingleObjectSessionUtils.addToSession(session, redirect);
            return new ModelAndView("redirect:/mature_warning.spr");
        }

        ModelAndView mav = new ModelAndView("summary");
        mav.addObject("manga", m);
        return mav;
    }

    @RequestMapping("/*/*")
    public ModelAndView readChapter(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DAOSession daoSession = getDAOSession(session);
        MangaUser user = SingleObjectSessionUtils.getFromSession(session, MangaUser.class);

        String[] lexemes = request.getPathInfo().substring(1).split("/");

        String id = lexemes[0];
        String chap = lexemes[1];
        Manga m = daoSession.getManga(id);
        if (m == null) {
            response.sendError(404);
            return null;
        }

        if (m.isMature() && (user == null || !user.isMatureOk())) {
            ContextRelativeRedirectURL redirect = new ContextRelativeRedirectURL(request);
            SingleObjectSessionUtils.addToSession(session, redirect);
            return new ModelAndView("redirect:/mature_warning.spr");
        }

        Chapter c = null;
        String nextChapter = null;

        for (int index = 0; index < m.getChapters().size(); index++) {
            if (m.getChapters().get(index).getId().equals(chap)) {
                c = m.getChapters().get(index);
                if (index < m.getChapters().size() - 1) {
                    nextChapter = m.getChapters().get(index + 1).getId();
                }
            }
        }

        ModelAndView mav = new ModelAndView("read");
        mav.addObject("manga", m);
        mav.addObject("chapter", c);
        mav.addObject("nextChapter", nextChapter);

        return mav;
    }

    @RequestMapping("/*/*/*")
    public ModelAndView getChapterPage(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DAOSession daoSession = getDAOSession(session);
        MangaUser user = SingleObjectSessionUtils.getFromSession(session, MangaUser.class);

        String[] lexemes = request.getPathInfo().substring(1).split("/");

        String id = lexemes[0];
        String chap = lexemes[1];
        if (user == null || !user.isMatureOk()) {
            Manga m = daoSession.getManga(id);
            if (m.isMature()) {
                ContextRelativeRedirectURL redirect = new ContextRelativeRedirectURL(request);
                SingleObjectSessionUtils.addToSession(session, redirect);
                return new ModelAndView("redirect:/mature_warning.spr");
            }
        }
        
        String page = lexemes[2];
        if (daoSession.copyPageToStream(id, chap, page, response.getOutputStream())) {
            return null;
        }

        response.sendError(404);
        return null;
    }

    @RequestMapping(value = "/mature_warning.spr", method = RequestMethod.GET)
    public ModelAndView showMatureWarning() {
        return new ModelAndView("warning");
    }

    @RequestMapping(value = "/mature_warning.spr", method = RequestMethod.GET, params = {"ok"})
    public ModelAndView matureOk(HttpSession session, HttpServletResponse response, @RequestParam("ok") boolean ok) throws IOException {
        MangaUser user = SingleObjectSessionUtils.getFromSession(session, MangaUser.class);
        if (user == null) {
            user = new MangaUser(session.getId());
            SingleObjectSessionUtils.addToSession(session, user);
        }

        user.setMatureOk(ok);
        if (ok) {
            ContextRelativeRedirectURL url = SingleObjectSessionUtils.getFromSession(session, ContextRelativeRedirectURL.class);
            if (url != null) {
                SingleObjectSessionUtils.removeFromSession(session, url);
                response.sendRedirect(url.getRedirectUrl());
                return null;
            }
        }
        return new ModelAndView("redirect:/");
    }
}
