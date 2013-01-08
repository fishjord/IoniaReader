/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.upload;

import fishjord.ionia.upload.ArchiveTitleParser.Title;
import java.util.List;

/**
 *
 * @author Jordan Fish <fishjord at msu.edu>
 */
public class UploadedChapter {
    Title titleGuess;
    List<UploadedPage> pages;

    public UploadedChapter(Title titleGuess, List<UploadedPage> pages) {
        this.titleGuess = titleGuess;
        this.pages = pages;
    }

    public List<UploadedPage> getPages() {
        return pages;
    }

    public Title getTitleGuess() {
        return titleGuess;
    }
}
