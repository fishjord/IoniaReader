/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.upload;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jordan Fish <fishjord at msu.edu>
 */
public class UploadedChapter {
    String titleGuess;
    String id;
    List<UploadedPage> pages;

    public UploadedChapter(String id, String titleGuess, List<UploadedPage> pages) {
        this.titleGuess = titleGuess;
        this.id = id;
        this.pages = pages;
    }

    public String getId() {
        return id;
    }

    public List<UploadedPage> getPages() {
        return pages;
    }

    public String getTitleGuess() {
        return titleGuess;
    }
}
