/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author fishjord
 */
public class Manga {    
    private String id;
    private String title;
    private String author;
    private String artist;
    private String publisher;
    private String circle;
    private String scanGroup;
    private String description;
    private Calendar publishedDate;
    private Calendar uploadedDate;
    private Calendar updatedDate;
    private boolean complete;
    private boolean mature;
    private String uploadedBy;
    
    private List<Chapter> chapters;
    private List<String> tags;
    
    public Manga() {
        chapters = new ArrayList();
        tags = new ArrayList();
    }

    Manga(String id, String title, String author, String artist, String publisher, String circle, String scanGroup, String description, Calendar publishedDate, Calendar uploadedDate, Calendar updatedDate, String uploadedBy, boolean complete, boolean mature, List<Chapter> chapters, List<String> tags) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.artist = artist;
        this.publisher = publisher;
        this.circle = circle;
        this.scanGroup = scanGroup;
        this.description = description;
        this.publishedDate = publishedDate;
        this.uploadedDate = uploadedDate;
        this.updatedDate = updatedDate;
        this.complete = complete;
        this.mature = mature;
        this.uploadedBy = uploadedBy;
        this.chapters = chapters;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getArtist() {
        return artist;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCircle() {
        return circle;
    }

    public String getScanGroup() {
        return scanGroup;
    }

    public String getDescription() {
        return description;
    }

    public Calendar getPublishedDate() {
        return publishedDate;
    }

    public Calendar getUploadedDate() {
        return uploadedDate;
    }

    public Calendar getUpdatedDate() {
        return updatedDate;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isMature() {
        return mature;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public List<String> getTags() {
        return tags;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public void setScanGroup(String scanGroup) {
        this.scanGroup = scanGroup;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublishedDate(Calendar publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setMature(boolean mature) {
        this.mature = mature;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setUpdatedDate(Calendar updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setUploadedDate(Calendar uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    @Override
    public String toString() {
        return "Manga{" + "id=" + id + ", title=" + title + ", author=" + author + ", artist=" + artist + ", publisher=" + publisher + ", circle=" + circle + ", scanGroup=" + scanGroup + ", description=" + description + ", publishedDate=" + publishedDate + ", uploadedDate=" + uploadedDate + ", updatedDate=" + updatedDate + ", complete=" + complete + ", mature=" + mature + ", uploadedBy=" + uploadedBy + ", chapters=" + chapters + ", tags=" + tags + '}';
    }
}
