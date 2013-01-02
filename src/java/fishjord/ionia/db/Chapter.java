/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.db;

import java.util.Calendar;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author fishjord
 */
public class Chapter {
    private String id;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private Calendar uploadDate;
    private String chapterTitle;
    private Integer chapterNumber;
    private int numPages;
    
    public Chapter() {
    }
    
    public Chapter(String id, String chapterTitle, Integer chapterNumber, int numPages, Calendar uploadDate) {
        this.id = id;
        this.uploadDate = uploadDate;
        this.chapterTitle = chapterTitle;
        this.chapterNumber = chapterNumber;
        this.numPages = numPages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Calendar uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public Integer getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(Integer chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    @Override
    public String toString() {
        return "Chapter{" + "id=" + id + ", uploadDate=" + uploadDate + ", chapterTitle=" + chapterTitle + ", chapterNumber=" + chapterNumber + ", numPages=" + numPages + '}';
    }
}
