/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.upload;

import fishjord.ionia.db.Chapter;
import fishjord.ionia.db.Manga;
import fishjord.ionia.db.Page;
import fishjord.ionia.upload.ArchiveTitleParser.Title;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author fishjord
 */
public class UploadUtils {

    public static Upload fromZip(String archiveName, ZipInputStream uploadedArchive) throws IOException {
        Title mangaTitle = ArchiveTitleParser.parse(archiveName);
        Manga manga = UploadUtils.guessInfoFromArchiveName(mangaTitle);

        List<UploadedChapter> uploadedChapters = UploadUtils.processZipArchive(mangaTitle, uploadedArchive);
        uploadedChapters = reprocessUploadedChapters(uploadedChapters);

        int chapCount = manga.getChapters().size();
        UploadedChapter uploadedChapter;
        for (int index = 0; index < uploadedChapters.size(); index++) {
            int chapNum = index + chapCount + 1;
            uploadedChapter = uploadedChapters.get(index);
            Title title = uploadedChapter.titleGuess;

            if (title == null) {
                title = new Title("chapter_" + chapNum, null, "Chapter " + chapNum, null, null, "Chapter " + chapNum);
                uploadedChapter.titleGuess = title;
            }

            Chapter c = new Chapter(title.getId(), title.getTitle(), title.getScanGroup(), chapNum, null);

            List<Page> chapterPages = new ArrayList();
            for(UploadedPage page : uploadedChapter.getPages()) {
                Page p = new Page(page.getTitle().getId());
                chapterPages.add(p);
            }
            c.setPages(chapterPages);

            manga.getChapters().add(c);
        }

        return new Upload(manga, uploadedChapters);
    }

    private static List<UploadedChapter> reprocessUploadedChapters(List<UploadedChapter> inchapters) {
        List<UploadedChapter> ret = new ArrayList();
        Map<String, List<UploadedPage>> chapterMap = new HashMap();

        for (UploadedChapter chapter : inchapters) {
            List<UploadedPage> remainingPages = new ArrayList();
            for (UploadedPage page : chapter.pages) {
                String chapterTitle = page.getTitle().getChapter();
                if (chapterTitle == null) {
                    remainingPages.add(page);
                } else {
                    if (!chapterMap.containsKey(chapterTitle)) {
                        chapterMap.put(chapterTitle, new ArrayList());
                    }
                    chapterMap.get(chapterTitle).add(page);
                }
            }

	    if (!remainingPages.isEmpty()) {
		String chapterTitle = null;
		chapterMap.put(chapterTitle, remainingPages);
	    }
        }

        List<String> chapterNames = new ArrayList(chapterMap.keySet());
        Collections.sort(chapterNames, new Comparator<String>() {
            public int compare(String o1, String o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return -1;
                } else if (o2 == null) {
                    return 1;
                } else {
                    return String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
                }
            }
        });

        for (String chapter : chapterNames) {
            List<UploadedPage> pages = chapterMap.get(chapter);
	    if(pages.isEmpty()) {
		continue;
	    }

            Collections.sort(pages, new Comparator<UploadedPage>() {
                public int compare(UploadedPage o1, UploadedPage o2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(o1.getTitle().getId(), o2.getTitle().getId());
                }
            });
	    Title pageTitle = pages.get(0).getTitle();

            Title title = null;
            if (chapter != null) {
		title = new Title(chapter.toLowerCase().replace(" ", "_"), pageTitle.getMagazine(), chapter, pageTitle.getScanGroup(), pageTitle.getVolume(), chapter);
            } else if (chapterNames.size() != 1) {
                continue;
            }

            ret.add(new UploadedChapter(title, pages));
        }

        return ret;
    }

    public static Upload fromZip(File f) throws IOException {
        return fromZip(f.getName(), new ZipInputStream(new BufferedInputStream(new FileInputStream(f))));
    }

    public static Manga guessInfoFromArchiveName(Title title) {
        Manga ret = new Manga();

        ret.setId(title.getId());
        ret.setTitle(title.getTitle());

        return ret;
    }

    public static String guessFormat(byte[] buf) {
        int b1 = 0xff & buf[0];
        int b2 = 0xff & buf[1];
        int b3 = 0xff & buf[2];
        int b4 = 0xff & buf[3];

        if (buf.length > 1 && b1 == 0x42 && b2 == 0x4d) {
            return "image/bmp";
        }

        if (buf.length > 3 && b1 == 0x47 && b2 == 0x49 && b3 == 0x46 && b4 == 0x38) {
            return "image/gif";
        }

        if (buf.length > 3 && b1 == 0xff && b2 == 0xd8) { //Bunch of sub-classes of jpegs, we don't really care, just that it's a jpeg
            return "image/jpeg";
        }

        if (buf.length > 3 && b1 == 0x89 && b2 == 0x50 && b3 == 0x4e && b4 == 0x47) {
            return "image/png";
        }
        
        try {
            ImageIO.createImageInputStream(new ByteArrayInputStream(buf));
            return "application/octet-stream";
        } catch(Exception ignore) {
            return null;
        }
    }

    private static UploadedPage processEntry(Title parent, ZipEntry entry, ZipInputStream zip) throws IOException {
	byte[] buf;
	String format = "application/octet-stream";
	buf = IOUtils.toByteArray(zip);
	
	format = guessFormat(buf);
	if(format == null) {
	    System.out.println(entry.getName());
	    return null;
	}
	
	if(format.equals("application/octet-stream")) {
	    System.err.println("Didn't find a magic number for " + entry.getName() + " but ImageIO could parse it");
	}
        
        return new UploadedPage(ArchiveTitleParser.parse(entry.getName(), parent), format, buf);
    }

    public static List<UploadedChapter> processZipArchive(Title titleGuess, ZipInputStream zip) throws IOException {
        ZipEntry entry;
        List<UploadedChapter> ret = new ArrayList();

        List<UploadedPage> pages = new ArrayList();
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                ret.addAll(processZipArchive(ArchiveTitleParser.parse(entry.getName(), titleGuess), zip));
            } else {
                UploadedPage page = processEntry(titleGuess, entry, zip);

                if (page != null) {
                    pages.add(page);
                }
            }
        }

        ret.add(new UploadedChapter(titleGuess, pages));

        return ret;
    }
    public static void main(String[] args) throws Exception {
        //String testZip = "Z:\\fishjord\\projects\\lilicious_fetch\\downloads\\Angels_Wings_[lililicious].zip";
        //String testZip = "Z:\\fishjord\\projects\\lilicious_fetch\\downloads\\not_loaded\\First_Love_Sisters_v2_[lililicious].zip";
        //String testZip = "Z:\\fishjord\\projects\\lilicious_fetch\\downloads\\Aqua_Blue_Cinema_booklet_[lililicious].zip";
        //String testZip = "Z:\\fishjord\\projects\\lilicious_fetch\\downloads\\A_Piece_of_Candy_[lililicious].zip";
        //String testZip = "Z:\\fishjord\\projects\\lilicious_fetch\\downloads\\All_My_Love_And_Lies_[lililicious].zip";
        String testZip = "Z:\\fishjord\\projects\\lilicious_fetch\\downloads\\Adorable_to_Me_[lililicious].zip";
        Upload upload = UploadUtils.fromZip(new File(testZip));
        
        System.out.println(upload.getManga());
        System.out.println();
        System.out.println(upload.getNewChapters());
        System.out.println();
        System.out.println(upload.getNewChapters().get(0).getPages().toString().replace(", T", "\nT"));
    }
}
