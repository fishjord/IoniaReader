/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.upload;

import fishjord.ionia.upload.ArchiveTitleParser.Title;

/**
 *
 * @author fishjord
 */
public class UploadedPage {
    private Title title;
    String uploadedFileType;
    private byte[] image;
    
    public UploadedPage(Title title, String uploadedFileType, byte[] image) {
        this.title = title;
        this.uploadedFileType = uploadedFileType;
        this.image = image;
    }

    public Title getTitle() {
        return title;
    }

    public String getUploadedFileType() {
        return uploadedFileType;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    @Override
    public String toString() {
        return this.title + " " + this.uploadedFileType + " " + image;
    }
}
