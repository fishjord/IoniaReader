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
    private Integer chapterNumber;
    private int numPages;
    private int titlePage;
    
    public Chapter() {
    }
    
    public Chapter(Integer chapterId, Integer chapterNumber, int numPages, int titlePage, Date uploadDate, String chapterTitle) {
        this.chapterId = chapterId;
        this.uploadDate = uploadDate;
        this.chapterTitle = chapterTitle;
        this.chapterNumber = chapterNumber;
        this.numPages = numPages;
        this.titlePage = titlePage;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
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

    public int getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(int titlePage) {
        this.titlePage = titlePage;
    }
}
