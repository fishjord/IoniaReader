/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web;

import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaReaderDB;
import fishjord.mangareader.db.MangaUser;
import fishjord.mangareader.upload.UploadBackgroundTask;
import fishjord.mangareader.upload.UploadBackgroundTask.UploadStatus;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author fishjord
 */
@Controller
public class UploadController {

    @Autowired
    private ThreadPoolTaskExecutor threadpool;
    @Autowired
    private MangaReaderDB mangaDb;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor editor = new StringTrimmerEditor(true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        binder.registerCustomEditor(String.class, editor);
    }

    @RequestMapping(value = "/admin/upload.spr", method = RequestMethod.GET)
    public ModelAndView processUploadGet(HttpSession session) {
        if (SingleObjectSessionUtils.isInSession(session, UploadBackgroundTask.class) || SingleObjectSessionUtils.isInSession(session, Manga.class)) {
            return new ModelAndView("redirect:upload_status.spr");
        }
        return new ModelAndView("upload");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/upload.spr")
    public ModelAndView processMangaUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.err.println("Processing upload");
        HttpSession session = request.getSession();
        MangaUser user = SingleObjectSessionUtils.getFromSession(session, MangaUser.class);
        ModelAndView mav = new ModelAndView();

        //if (user == null) {
        //    return new ModelAndView("redirect:/login.spr");
        //}

        //if (!user.isUserInRole(MangaUserRole.createManga)) {
        //    mav.setViewName("upload");
        //    mav.addObject("error", "User " + user.getDisplayName() + " cannot create manga");
        //    return mav;
        //}

        if (!ServletFileUpload.isMultipartContent(request)) {
            mav.setViewName("upload");
            mav.addObject("error", "Expected only one uploaded file");
            return mav;
        }

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);

        if (items.size() != 1) {
            mav.setViewName("upload");
            mav.addObject("error", "Expected only one uploaded file");
            return mav;
        }

        FileItem item = items.get(0);
        ZipInputStream zip;
        try {
            zip = new ZipInputStream(item.getInputStream());
            UploadBackgroundTask task = new UploadBackgroundTask(mangaDb, item.getName(), zip);
            SingleObjectSessionUtils.addToSession(session, task);
            threadpool.submit(task);

            return new ModelAndView("redirect:upload_status.spr");
        } catch (Exception e) {
            mav.setViewName("upload");
            mav.addObject("error", "Internal error processing upload: " + e.getMessage());
            return mav;
        }
    }

    @RequestMapping(value = "/admin/upload_status.spr", method = RequestMethod.GET)
    public ModelAndView getUploadStatus(HttpSession session) {
        if (SingleObjectSessionUtils.isInSession(session, Manga.class)) {
            return new ModelAndView("redirect:edit_manga.spr");
        }

        UploadBackgroundTask task = SingleObjectSessionUtils.getFromSession(session, UploadBackgroundTask.class);

        if (task == null) {
            return new ModelAndView("redirect:upload.spr");
        }

        if (task.getStatus() == UploadStatus.Complete) {
            Manga upload = task.getResult();
            SingleObjectSessionUtils.removeFromSession(session, task);
            SingleObjectSessionUtils.addToSession(session, upload);
            return new ModelAndView("redirect:edit_manga.spr");
        }

        ModelAndView mav = new ModelAndView("upload_status");
        mav.addObject("uploadTask", task);
        mav.addObject("refresh", task.getStatus() != UploadStatus.Error);
        return mav;
    }

    @RequestMapping(value = "/admin/edit_manga.spr", method = RequestMethod.GET)
    public ModelAndView getFinalizeUploadView(HttpSession session) {
        Manga upload = SingleObjectSessionUtils.getFromSession(session, Manga.class);

        if (upload == null) {
            return new ModelAndView("redirect:upload.spr");
        }

        ModelAndView mav = new ModelAndView("edit_manga");
        mav.addObject("manga", upload);
        return mav;
    }

    @RequestMapping(value = "/admin/edit_manga.spr", method = RequestMethod.POST)
    public ModelAndView getFinalizeUploadView(HttpSession session, @ModelAttribute("manga") Manga manga) {
        System.err.println("Finalizing manga " + manga);
        Manga sessionManga = SingleObjectSessionUtils.getFromSession(session, Manga.class);
        manga.setId(sessionManga.getId());
        for (int index = 0; index < manga.getChapters().size(); index++) {
            sessionManga.getChapters().get(index).setChapterId(sessionManga.getChapters().get(index).getChapterId());
            sessionManga.getChapters().get(index).setUploadedBy(sessionManga.getChapters().get(index).getUploadedBy());
            sessionManga.getChapters().get(index).setNewPages(sessionManga.getChapters().get(index).getNewPages());
        }

        System.err.println(manga.getId() + "\t" + sessionManga.getId());
        System.err.println(manga.getTitle() + "\t" + sessionManga.getTitle());
        System.err.println(manga.getAuthor() + "\t" + sessionManga.getAuthor());
        System.err.println(manga.getArtist() + "\t" + sessionManga.getArtist());
        System.err.println(manga.getPublisher() + "\t" + sessionManga.getPublisher());
        System.err.println(manga.getCircle() + "\t" + sessionManga.getCircle());
        System.err.println(manga.getScanGroup() + "\t" + sessionManga.getScanGroup());
        System.err.println(manga.getDescription() + "\t" + sessionManga.getDescription());
        System.err.println(manga.getPublishedDate() + "\t" + sessionManga.getPublishedDate());
        System.err.println(manga.getUpdatedDate() + "\t" + sessionManga.getUpdatedDate());
        System.err.println(manga.getUploadedDate() + "\t" + sessionManga.getUploadedDate());
        System.err.println(manga.isComplete() + "\t" + sessionManga.isComplete());
        System.err.println(manga.isMature() + "\t" + sessionManga.isMature());
        
        mangaDb.updateManga(manga);
        SingleObjectSessionUtils.removeFromSession(session, Manga.class);

        return new ModelAndView("redirect:/view.spr?id=" + manga.getId());
    }

    @RequestMapping(value = "/admin/cancel_upload.spr")
    public ModelAndView cancelUpload(HttpSession session) {
        SingleObjectSessionUtils.removeFromSession(session, Manga.class);
        return new ModelAndView("redirect:upload.spr");
    }
}
