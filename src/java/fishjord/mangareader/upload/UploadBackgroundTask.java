/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.upload;

import fishjord.mangareader.db.Chapter;
import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaReaderDB;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author fishjord
 */
public class UploadBackgroundTask implements Runnable {

    public static enum UploadStatus {

        Pending, Processing, Complete, Error
    };
    private UploadStatus status = UploadStatus.Pending;
    private List<String> messages = new ArrayList();
    private String archiveName;
    private ZipInputStream uploadedArchive;
    private MangaReaderDB db;
    private Manga result;

    private static class UploadedChapter {
        String titleGuess;
        List<UploadedPage> pages = new ArrayList();
    }

    public UploadBackgroundTask(MangaReaderDB db, String archiveName, ZipInputStream uploadedArchive) {
        this.db = db;
        this.uploadedArchive = uploadedArchive;
        this.archiveName = archiveName;
    }

    private void addMessage(String message) {
        this.messages.add(message);
    }

    public Manga getResult() {
        return result;
    }

    public void run() {
        try {
            this.status = UploadStatus.Processing;
            addMessage("Processing uploaded archive " + archiveName);

            Manga manga = guessInfoFromArchiveName(archiveName);
            Manga tmp = db.getManga(manga.getTitle());
            if (tmp != null) {
                manga = tmp;
            }

            List<UploadedChapter> uploadedChapters = processZipArchive(null, uploadedArchive);

            int chapCount = manga.getChapters().size();
            UploadedChapter uploadedChapter;
            for (int index = 0; index < uploadedChapters.size(); index++) {
                int chapNum = index + chapCount + 1;
                uploadedChapter = uploadedChapters.get(index);
                
                addMessage("Creating chapter " + chapNum);
                String titleGuess = uploadedChapter.titleGuess;
                if(titleGuess == null) {
                    titleGuess = "Chapter " + chapNum;
                }
                
                Chapter c = new Chapter(null, chapNum, uploadedChapter.pages.size(), 1, null, titleGuess, "jrdn.fish@gmail.com");// user.getUsername());
                c.setNewPages(uploadedChapter.pages);
                manga.getChapters().add(c);
            }
            this.status = UploadStatus.Complete;
            result = manga;
        } catch (Exception e) {
            this.status = UploadStatus.Error;
            addMessage("Error processing upload: " + e.getMessage());
        }
    }

    public static Manga guessInfoFromArchiveName(String archiveName) {
        Manga ret = new Manga();

        int lastDot = archiveName.lastIndexOf('.');
        if (lastDot != -1) {
            archiveName = archiveName.substring(0, lastDot);
        }

        StringBuilder title = new StringBuilder();
        String scanlationGroup = null;
        String[] tokens = archiveName.split("[_|\\s]+");
        title.append(tokens[0]);

        for (int index = 1; index < tokens.length; index++) {
            if ((tokens[index].toLowerCase().equals("ch") || tokens[index].toLowerCase().equals("chapter")) && index + 1 < tokens.length) {
                index++;
            } else if (tokens[index].toLowerCase().startsWith("ch")) {
            } else if (tokens[index].charAt(0) == '[' && tokens[index].endsWith("]")) {
                scanlationGroup = tokens[index].substring(1, tokens[index].length() - 1);
            } else {
                title.append(" ").append(tokens[index]);
            }
        }

        ret.setTitle(title.toString());
        ret.setScanGroup(scanlationGroup);

        return ret;
    }

    private static String guessFormat(byte[] buf) {
        int b1 = 0xff & buf[0];
        int b2 = 0xff & buf[1];
        int b3 = 0xff & buf[2];
        int b4 = 0xff & buf[3];
        
        if (buf.length > 1 && b1 == 0x42 && b2 == 0x4d) {
            return "bmp";
        }

        if (buf.length > 3 && b1 == 0x47 && b2 == 0x49 && b3 == 0x46 && b4 == 0x38) {
            return "gif";
        }

        if (buf.length > 3 && b1 == 0xff && b2 == 0xd8 && b3 == 0xff && b4 == 0xe0) {
            return "jpeg";
        }

        if (buf.length > 3 && b1 == 0x89 && b2 == 0x50 && b3 == 0x4e && b4 == 0x47) {
            return "png";
        }

        return "unknown";
    }

    private UploadedPage processEntry(ZipEntry entry, ZipInputStream zip) throws IOException {
        byte[] buf = IOUtils.toByteArray(zip);

        return new UploadedPage(entry.getName(), guessFormat(buf), buf);
    }

    private List<UploadedChapter> processZipArchive(String titleGuess, ZipInputStream zip) throws IOException {
        List<UploadedChapter> ret = new ArrayList();

        ZipEntry entry;
        UploadedChapter currChapter = new UploadedChapter();
        currChapter.titleGuess = titleGuess;
        ret.add(currChapter);
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                ret.addAll(processZipArchive(entry.getName().substring(0, entry.getName().length() - 1), zip));
            } else {
                UploadedPage page = processEntry(entry, zip);
                addMessage("Processed page: " + page);
                if (page != null) {
                    currChapter.pages.add(page);
                }
            }
        }

        return ret;
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public UploadStatus getStatus() {
        return status;
    }
}
