/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.upload;

import fishjord.mangareader.db.Chapter;
import fishjord.mangareader.db.Manga;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author fishjord
 */
public class UploadUtils {

    public static Upload fromZip(String archiveName, ZipInputStream uploadedArchive) throws IOException {
        Manga manga = UploadUtils.guessInfoFromArchiveName(archiveName);

        List<UploadedChapter> uploadedChapters = UploadUtils.processZipArchive(null, uploadedArchive);
        Map<Integer, UploadedChapter> newChapters = new HashMap();

        int chapCount = manga.getChapters().size();
        UploadedChapter uploadedChapter;
        for (int index = 0; index < uploadedChapters.size(); index++) {
            int chapNum = index + chapCount + 1;
            uploadedChapter = uploadedChapters.get(index);

            if (uploadedChapter.titleGuess == null) {
                uploadedChapter.titleGuess = "Chapter " + chapNum;
            }
            uploadedChapter.id = uploadedChapter.titleGuess.toLowerCase().replace(" ", "_");

            Chapter c = new Chapter(uploadedChapter.id, uploadedChapter.titleGuess, chapNum, uploadedChapter.pages.size(), null);
            newChapters.put(chapNum, uploadedChapter);

            manga.getChapters().add(c);
        }

        return new Upload(manga, uploadedChapters);
    }

    public static Upload fromZip(File f) throws IOException {
        return fromZip(f.getName(), new ZipInputStream(new BufferedInputStream(new FileInputStream(f))));
    }

    public static Manga guessInfoFromArchiveName(String archiveName) {
        Manga ret = new Manga();

        int lastDot = archiveName.lastIndexOf('.');
        if (lastDot != -1) {
            archiveName = archiveName.substring(0, lastDot);
        }

        StringBuilder title = new StringBuilder();
        String scanlationGroup = null;
        String[] tokens = archiveName.split("[_|\\s]+");
        title.append(tokens[0]);

        for (int index = 1; index < tokens.length; index++) {
            if ((tokens[index].toLowerCase().equals("ch") || tokens[index].toLowerCase().equals("chapter")) && index + 1 < tokens.length) {
                index++;
            } else if (tokens[index].toLowerCase().startsWith("ch")) {
            } else if (tokens[index].charAt(0) == '[' && tokens[index].endsWith("]")) {
                scanlationGroup = tokens[index].substring(1, tokens[index].length() - 1);
            } else {
                title.append(" ").append(tokens[index]);
            }
        }

        ret.setId(title.toString().toLowerCase().replace(" ", "_"));
        ret.setTitle(title.toString());
        ret.setScanGroup(scanlationGroup);

        return ret;
    }

    public static String guessFormat(byte[] buf) {
        int b1 = 0xff & buf[0];
        int b2 = 0xff & buf[1];
        int b3 = 0xff & buf[2];
        int b4 = 0xff & buf[3];

        if (buf.length > 1 && b1 == 0x42 && b2 == 0x4d) {
            return "bmp";
        }

        if (buf.length > 3 && b1 == 0x47 && b2 == 0x49 && b3 == 0x46 && b4 == 0x38) {
            return "gif";
        }

        if (buf.length > 3 && b1 == 0xff && b2 == 0xd8 && b3 == 0xff && b4 == 0xe0) {
            return "jpeg";
        }

        if (buf.length > 3 && b1 == 0x89 && b2 == 0x50 && b3 == 0x4e && b4 == 0x47) {
            return "png";
        }

        return "unknown";
    }

    private static UploadedPage processEntry(ZipEntry entry, ZipInputStream zip) throws IOException {
        byte[] buf = IOUtils.toByteArray(zip);

        return new UploadedPage(entry.getName(), guessFormat(buf), buf);
    }

    public static List<UploadedChapter> processZipArchive(String titleGuess, ZipInputStream zip) throws IOException {
        List<UploadedChapter> ret = new ArrayList();

        ZipEntry entry;

        List<UploadedPage> pages = new ArrayList();
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                ret.addAll(processZipArchive(entry.getName().substring(0, entry.getName().length() - 1), zip));
            } else {
                UploadedPage page = processEntry(entry, zip);

                if (page != null) {
                    pages.add(page);
                }
            }
        }

        ret.add(new UploadedChapter(null, titleGuess, pages));

        return ret;
    }
}
