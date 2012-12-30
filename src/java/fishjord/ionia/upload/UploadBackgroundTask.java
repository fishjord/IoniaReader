/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.upload;

import fishjord.mangareader.db.Chapter;
import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaReaderDB;
import fishjord.mangareader.db.MangaUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

/**
 *
 * @author fishjord
 */
public class UploadBackgroundTask implements Runnable {
    private UploadStatus status = UploadStatus.Pending;
    private List<String> messages = new ArrayList();
    private String archiveName;
    private ZipInputStream uploadedArchive;
    private MangaReaderDB db;
    private MangaUser uploader;

    private Upload result;

    public UploadBackgroundTask(MangaReaderDB db, MangaUser uploader, String archiveName, ZipInputStream uploadedArchive) {
        this.db = db;
        this.uploadedArchive = uploadedArchive;
        this.archiveName = archiveName;
        this.uploader = uploader;
    }

    private void addMessage(String message) {
        this.messages.add(message);
    }

    public Upload getResult() {
        return result;
    }

    public void run() {
        try {
            this.status = UploadStatus.Processing;
            addMessage("Processing uploaded archive " + archiveName);

            Manga manga = UploadUtils.guessInfoFromArchiveName(archiveName);

            List<UploadedChapter> uploadedChapters = UploadUtils.processZipArchive(null, uploadedArchive);
            Map<Integer, UploadedChapter> newChapters = new HashMap();

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

                Chapter c = new Chapter(titleGuess.toLowerCase().replace(" ", "_"), titleGuess, chapNum, uploadedChapter.pages.size(), null);
                newChapters.put(chapNum, uploadedChapter);

                manga.getChapters().add(c);
            }
            this.status = UploadStatus.Complete;
            result = new Upload(manga, uploadedChapters);
        } catch (Exception e) {
            this.status = UploadStatus.Error;
            addMessage("Error processing upload: " + e.getMessage());
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Error processing upload from user " + uploader.getUsername(), e);
        }
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public UploadStatus getStatus() {
        return status;
    }
}
