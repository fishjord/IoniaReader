/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web;

import fishjord.mangareader.db.Chapter;
import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaListing;
import fishjord.mangareader.db.MangaReaderDB;
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
    private MangaReaderDB db;
    
    @RequestMapping(value="/list.spr")
    public ModelAndView listManga() {
        List<MangaListing> mangaList = db.listManga();
        ModelAndView mav = new ModelAndView("list");
        mav.addObject("mangaList", mangaList);
        return mav;
    }
    
    @RequestMapping(value="/summary.spr", params={"id"})
    public ModelAndView showSummary(HttpSession session, @RequestParam("id")Integer id) {
        Manga m = db.getManga(id);
        ModelAndView mav = new ModelAndView("summary");
        mav.addObject("manga", m);
        return mav;
    }
    
    @RequestMapping(value="/read.spr", params={"id", "chap"})
    public ModelAndView showSummary(HttpSession session, @RequestParam("id")Integer id, @RequestParam("chap")Integer chap) {
        Manga m = db.getManga(id);
        Chapter c = null;
        Integer nextChapter = null;
        
        for(int index = 0;index < m.getChapters().size();index++) {
            if(m.getChapters().get(index).getChapterId() == chap) {
                c = m.getChapters().get(index);
                if(index < m.getChapters().size() - 1) {
                    nextChapter = m.getChapters().get(index + 1).getChapterId();
                }
            }
        }
        
        ModelAndView mav = new ModelAndView("read");
        mav.addObject("manga", m);
        mav.addObject("chapter", c);
        mav.addObject("nextChapter", nextChapter);
                
        return mav;
    }
    
    @RequestMapping(value="/page.spr", params={"id", "chap", "page"})
    public void showPage(HttpServletRequest request, HttpServletResponse response, @RequestParam("id")Integer id, @RequestParam("chap")Integer chap, @RequestParam("page")Integer page) throws IOException {
        InputStream is = db.getPage(id, chap, page);
        response.setContentType("image/png");
        IOUtils.copy(is, response.getOutputStream());
        
        is.close();
        response.getOutputStream().close();
    }
}
