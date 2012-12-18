/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web;

import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaReaderDB;
import javax.servlet.http.HttpSession;
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
    
    @RequestMapping(value="/summary.spr", params={"id"})
    public ModelAndView showSummary(HttpSession session, @RequestParam("id")Integer id) {
        Manga m = db.getManga(id);
        ModelAndView mav = new ModelAndView("summary");
        mav.addObject("manga", m);
        return mav;
    }
}
