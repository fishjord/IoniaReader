/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.db;

import java.util.Date;

/**
 *
 * @author fishjord
 */
public class MangaListing {
    private Integer id;
    private String title;
    private String author;
    private String artist;
    private String publisher;
    private String circle;
    private String scanGroup;
    private String description;
    private Date publishedDate;
    private Date uploadedDate;
    private Date updatedDate;
    private String uploadedBy;
    private boolean completed;
    private int numChapters;    

    MangaListing(Integer id, String title, String author, String artist, String publisher, String circle, String scanGroup, String description, Date publishedDate, Date uploadedDate, Date updatedDate, String uploadedBy, boolean completed, int numChapters) {
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
        this.uploadedBy = uploadedBy;
        this.completed = completed;
        this.numChapters = numChapters;
    }

    public Integer getId() {
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

    public Date getPublishedDate() {
        return publishedDate;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getNumChapters() {
        return numChapters;
    }
}
