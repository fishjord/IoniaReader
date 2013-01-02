/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.upload;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author fishjord
 */
public class UploadedPage {
    private String uploadedFileName;
    String uploadedFileType;
    private byte[] image;
    
    public UploadedPage(String uploadedFileName, String uploadedFileType, byte[] image) {
        this.uploadedFileName = uploadedFileName;
        this.uploadedFileType = uploadedFileType;
        this.image = image;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public String getUploadedFileType() {
        return uploadedFileType;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    @Override
    public String toString() {
        return this.uploadedFileName + " " + this.uploadedFileType;
    }
}
