/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.db;

import fishjord.mangareader.upload.UploadedPage;
import java.util.Date;
import java.util.List;

/**
 *
 * @author fishjord
 */
public class Chapter {
    private Integer chapterId;
    private Date uploadDate;
    private String chapterTitle;
    private String uploadedBy;
    private Integer chapterNumber;
    private int numPages;
    private int titlePage;
    
    public Chapter() {
    }
    
    public Chapter(Integer chapterId, Integer chapterNumber, int numPages, int titlePage, Date uploadDate, String chapterTitle, String uploadedBy) {
        this.chapterId = chapterId;
        this.uploadDate = uploadDate;
        this.chapterTitle = chapterTitle;
        this.uploadedBy = uploadedBy;
        this.chapterNumber = chapterNumber;
        this.numPages = numPages;
        this.titlePage = titlePage;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public Integer getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public void setChapterNumber(Integer chapterNumber) {
        this.chapterNumber = chapterNumber;
    }
    
    public int getNumPages() {
        return numPages;
    }

    public int getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(int titlePage) {
        this.titlePage = titlePage;
    }
}
