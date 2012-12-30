/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web;

import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaReaderDB;
import fishjord.mangareader.db.MangaUser;
import fishjord.mangareader.upload.Upload;
import fishjord.mangareader.upload.UploadBackgroundTask;
import fishjord.mangareader.upload.UploadStatus;
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
            UploadBackgroundTask task = new UploadBackgroundTask(mangaDb, user, item.getName(), zip);
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

        UploadBackgroundTask task = SingleObjectSessionUtils.getFromSession(session, UploadBackgroundTask.class);

        if (task == null) {
            return new ModelAndView("redirect:upload.spr");
        }

        if (task.getStatus() == UploadStatus.Complete) {
            SingleObjectSessionUtils.removeFromSession(session, task);
            SingleObjectSessionUtils.addToSession(session, task.getResult());
            return new ModelAndView("redirect:edit_manga.spr");
        }

        ModelAndView mav = new ModelAndView("upload_status");
        mav.addObject("uploadTask", task);
        mav.addObject("refresh", task.getStatus() != UploadStatus.Error);
        return mav;
    }

    @RequestMapping(value = "/admin/edit_manga.spr", method = RequestMethod.GET)
    public ModelAndView getFinalizeUploadView(HttpSession session) {
        Upload upload = SingleObjectSessionUtils.getFromSession(session, Upload.class);

        if (upload == null) {
            return new ModelAndView("redirect:upload.spr");
        }

        ModelAndView mav = new ModelAndView("edit_manga");
        mav.addObject("manga", upload.getManga());
        mav.addObject("newChapters", upload.getNewChapters());
        //mav.addObject("allTags", mangaDb.getTagList());
        return mav;
    }

    @RequestMapping(value = "/admin/edit_manga.spr", method = RequestMethod.POST)
    public ModelAndView getFinalizeUploadView(HttpSession session, @ModelAttribute("manga") Manga manga) {
        Upload upload = SingleObjectSessionUtils.getFromSession(session, Upload.class);

        if (upload == null) {
            return new ModelAndView("redirect:upload.spr");
        }

        if(upload.getManga().getId() != manga.getId()) {
            throw new IllegalStateException("Manga ID has been changed, can't commit changes");
        }

        //mangaDb.updateManga(manga, upload.getNewChapters());
        SingleObjectSessionUtils.removeFromSession(session, upload);

        return new ModelAndView("redirect:/summary.spr?id=" + manga.getId());
    }

    @RequestMapping(value = "/admin/cancel_upload.spr")
    public ModelAndView cancelUpload(HttpSession session) {
        SingleObjectSessionUtils.removeFromSession(session, Upload.class);
        return new ModelAndView("redirect:upload.spr");
    }
}
