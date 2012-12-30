/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web;

import fishjord.mangareader.db.Chapter;
import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaUser;
import fishjord.mangareader.jcr.MangaDAO;
import fishjord.mangareader.jcr.MangaDAO.DAOSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
        DAOSession daoSession = null;
        MangaUser user = SingleObjectSessionUtils.getFromSession(session, MangaUser.class);
        if(user != null) {
            daoSession = user.getSession();
        } else {
            daoSession = dao.getAnonSession();
        }
        
        return daoSession;        
    }
    
    @RequestMapping(value="/list.spr")
    public ModelAndView listManga(HttpSession session) {
        DAOSession daoSession = getDAOSession(session);
        
        List<Manga> mangaList = daoSession.listManga();
        ModelAndView mav = new ModelAndView("list");
        mav.addObject("mangaList", mangaList);
        return mav;
    }
    
    @RequestMapping(value="/summary.spr", params={"id"})
    public ModelAndView showSummary(HttpSession session, @RequestParam("id")String id) {
        DAOSession daoSession = getDAOSession(session);
        
        Manga m = daoSession.getManga(id);
        ModelAndView mav = new ModelAndView("summary");
        mav.addObject("manga", m);
        return mav;
    }
    
    @RequestMapping(value="/read.spr", params={"id", "chap"})
    public ModelAndView showSummary(HttpSession session, @RequestParam("id")String id, @RequestParam("chap")String chap) {
        DAOSession daoSession = getDAOSession(session);
        Manga m = daoSession.getManga(id);
        Chapter c = null;
        String nextChapter = null;
        
        for(int index = 0;index < m.getChapters().size();index++) {
            if(m.getChapters().get(index).getId().equals(chap)) {
                c = m.getChapters().get(index);
                if(index < m.getChapters().size() - 1) {
                    nextChapter = m.getChapters().get(index + 1).getId();
                }
            }
        }
        
        int numPages = daoSession.getPageCount(id, chap);
        c.setNumPages(numPages);
        
        ModelAndView mav = new ModelAndView("read");
        mav.addObject("manga", m);
        mav.addObject("chapter", c);
        mav.addObject("nextChapter", nextChapter);
                
        return mav;
    }
    
    @RequestMapping(value="/page.spr", params={"id", "chap", "page"})
    public void showPage(HttpServletRequest request, HttpServletResponse response, @RequestParam("id")String id, @RequestParam("chap")String chap, @RequestParam("page")Integer page) throws IOException {
        DAOSession session = getDAOSession(request.getSession());
        
        session.copyPageToStream(id, chap, page, response.getOutputStream());
    }
}
