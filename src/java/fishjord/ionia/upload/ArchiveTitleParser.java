/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author fishjord
 */
public class ArchiveTitleParser {

    public static class Title {

        private String magazine;
        private String title;
        private String scanGroup;
        private String volume;
        private String chapter;
        private String id;

        public Title(String id, String magazine, String title, String scanGroup, String volume, String chapter) {
            this.magazine = magazine;
            this.title = title;
            this.scanGroup = scanGroup;
            this.volume = volume;
            this.chapter = chapter;
            this.id = id;
        }

        public String getMagazine() {
            return magazine;
        }

        public String getTitle() {
            return title;
        }

        public String getScanGroup() {
            return scanGroup;
        }

        public String getVolume() {
            return volume;
        }

        public String getChapter() {
            return chapter;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Title{" + "magazine=" + magazine + ", title=" + title + ", scanGroup=" + scanGroup + ", volume=" + volume + ", chapter=" + chapter + ", id=" + id + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Title other = (Title) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            return true;
        }
    }

    public static Title parse(String name) {
        return parse(name, null);
    }

    public static Title parse(String name, Title parentTitle) {
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }

        if (name.contains("/")) {
            name = name.substring(name.lastIndexOf('/') + 1, name.length());
        }

        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }

        String[] lexemes = name.split("[\\s|_]+");
        StringBuilder titleBuilder = new StringBuilder();
        String chapter = (parentTitle == null) ? null : parentTitle.getChapter();
        String vol = (parentTitle == null) ? null : parentTitle.getVolume();
        String scanGroup = (parentTitle == null) ? null : parentTitle.getScanGroup();
        String magazine = (parentTitle == null) ? null : parentTitle.getMagazine();
        String title;
        if (name.startsWith("_")) {
            title = name;
        } else {
            for (String lexeme : lexemes) {
                if (lexeme.startsWith("[") && lexeme.endsWith("]")) {
                    scanGroup = lexeme.substring(1, lexeme.length() - 1);
                } else if (lexeme.startsWith("(") && lexeme.endsWith(")")) {
                    magazine = lexeme.substring(1, lexeme.length() - 1);
                } else if (lexeme.matches("ch?\\d+")) {
                    chapter = "Chapter " + lexeme.replaceAll("[^\\d]+", "");
                } else if (lexeme.matches("v(ol)?\\d+")) {
                    vol = "Volume " + lexeme.replaceAll("[^\\d]+", "");;
                } else {
                    titleBuilder.append(lexeme).append(" ");
                }
            }

            title = titleBuilder.toString().trim();
        }
        String id = title.replaceAll("[^0-9a-zA-Z_ ]+", "").replaceAll("\\s+", "_").toLowerCase();

        return new Title(id, magazine, title, scanGroup, vol, chapter);
    }
}
