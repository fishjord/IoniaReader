/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.upload;

import fishjord.mangareader.db.Manga;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jordan Fish <fishjord at msu.edu>
 */
public class Upload {
    private Manga manga;
    private Map<Integer, UploadedChapter> newChapters;

    public Upload(Manga manga, Map<Integer, UploadedChapter> newChapters) {
        this.manga = manga;
        this.newChapters = newChapters;
    }

    public Map<Integer, UploadedChapter> getNewChapters() {
        return newChapters;
    }

    public Manga getManga() {
        return manga;
    }



}
