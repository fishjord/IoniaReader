/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.db;

import java.util.Calendar;
import java.util.List;
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
    private String scanGroup;
    
    private List<Page> pages;
    
    public Chapter() {
    }
    
    public Chapter(String id, String chapterTitle, String scanGroup, Integer chapterNumber, Calendar uploadDate) {
        this.id = id;
        this.uploadDate = uploadDate;
        this.chapterTitle = chapterTitle;
        this.chapterNumber = chapterNumber;
        this.scanGroup = scanGroup;
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

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public String getScanGroup() {
        return scanGroup;
    }

    public void setScanGroup(String scanGroup) {
        this.scanGroup = scanGroup;
    }

    @Override
    public String toString() {
        return "Chapter{" + "id=" + id + ", uploadDate=" + uploadDate + ", chapterTitle=" + chapterTitle + ", chapterNumber=" + chapterNumber + ", scanGroup=" + scanGroup + ", pages=" + pages + '}';
    }
}
