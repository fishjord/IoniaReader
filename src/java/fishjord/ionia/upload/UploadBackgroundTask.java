/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.upload;

import fishjord.ionia.db.Chapter;
import fishjord.ionia.db.Manga;
import fishjord.ionia.db.MangaReaderDB;
import fishjord.ionia.db.MangaUser;
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

    public UploadBackgroundTask(MangaUser uploader, String archiveName, ZipInputStream uploadedArchive) {
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
            
            result = UploadUtils.fromZip(archiveName, uploadedArchive);
            
            this.status = UploadStatus.Complete;
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
