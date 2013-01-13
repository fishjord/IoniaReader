/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
    private String description;
    private Calendar publishedDate;
    private Calendar uploadedDate;
    private Calendar updatedDate;
    private String uploadedBy;
    
    private List<Chapter> chapters;
    private Set<String> tags;
    
    public Manga() {
        chapters = new ArrayList();
        tags = new HashSet();
    }

    Manga(String id, String title, String author, String artist, String publisher, String circle, String description, Calendar publishedDate, Calendar uploadedDate, Calendar updatedDate, String uploadedBy, List<Chapter> chapters, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.artist = artist;
        this.publisher = publisher;
        this.circle = circle;
        this.description = description;
        this.publishedDate = publishedDate;
        this.uploadedDate = uploadedDate;
        this.updatedDate = updatedDate;
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
        return tags.contains("complete");
    }

    public boolean isMature() {
        return tags.contains("mature");
    }

    public boolean isPrivate() {
        return tags.contains("private");
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public Set<String> getTags() {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublishedDate(Calendar publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public void setTags(Set<String> tags) {
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
        return "Manga{" + "id=" + id + ", title=" + title + ", author=" + author + ", artist=" + artist + ", publisher=" + publisher + ", circle=" + circle + ", description=" + description + ", publishedDate=" + publishedDate + ", uploadedDate=" + uploadedDate + ", updatedDate=" + updatedDate + ", uploadedBy=" + uploadedBy + ", chapters=" + chapters + ", tags=" + tags + '}';
    }
}
