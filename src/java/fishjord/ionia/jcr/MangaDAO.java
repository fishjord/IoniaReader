/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.jcr;

import fishjord.ionia.db.Chapter;
import fishjord.ionia.db.Manga;
import fishjord.ionia.db.MangaUser;
import fishjord.ionia.db.Page;
import fishjord.ionia.upload.Upload;
import fishjord.ionia.upload.UploadedChapter;
import fishjord.ionia.upload.UploadedPage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.value.ValueFactoryImpl;

/**
 *
 * @author fishjord
 */
public class MangaDAO {

    private static final Logger log = Logger.getLogger(MangaDAO.class.getCanonicalName());
    private Repository repo;
    private DAOSession anonSession;

    public MangaDAO(String repoDir, String repoXml) {
        try {
            RepositoryConfig config = RepositoryConfig.create(repoXml, repoDir);
            repo = RepositoryImpl.create(config);

            anonSession = new DAOSession("anon", repo.login());
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to repository", e);
        }
    }

    public DAOSession getAnonSession() {
        return anonSession;
    }

    public DAOSession login(String username) {
        try {
            return new DAOSession(username, repo.login(new SimpleCredentials("admin", "admin".toCharArray())));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to log user in", e);
            return null;
        }
    }

    public DAOSession login(MangaUser user) {
        return login(user.getUsername());
    }

    public void close() {
        ((RepositoryImpl) repo).shutdown();
    }

    public static class DAOSession implements Iterable<Manga> {

        private final Session session;
        private static final Logger log = Logger.getLogger(DAOSession.class.getCanonicalName());
        private final Lock lock = new ReentrantLock();
        private String username;

        public DAOSession(String username, Session session) {
            this.session = session;
        }

        public void logout() {
            lock.lock();
            try {
                session.logout();
            } finally {
                lock.unlock();
            }
        }

        public void persist(Upload upload) {
            persist(upload.getManga());

            lock.lock();
            try {
                for (UploadedChapter uploadedChapter : upload.getNewChapters()) {
                    Node chapterNode = session.getNode("/manga/" + upload.getManga().getId() + "/chapters/" + uploadedChapter.getTitleGuess().getId());
                    Node addTo = chapterNode.getNode("pages");
                    for (UploadedPage page : uploadedChapter.getPages()) {
                        JcrUtils.putFile(addTo, page.getTitle().getId(), page.getUploadedFileType(), new ByteArrayInputStream(page.getImage()));
                    }
                }

                session.save();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error persisting new chapters", e);
            } finally {
                lock.unlock();
            }
        }

        public void persist(Manga manga) {
            lock.lock();
            try {
                Node mangaNode;
                if (!session.nodeExists("/manga/" + manga.getId())) {
                    manga.setUploadedDate(new GregorianCalendar());
                    manga.setUploadedBy(session.getUserID());
                    mangaNode = JcrUtils.getOrAddNode(session.getRootNode(), "manga").addNode(manga.getId());
                    mangaNode.addNode("chapters");
                } else {
                    mangaNode = session.getNode("/manga/" + manga.getId());
                }

                mangaNode.setProperty("Artist", manga.getArtist());
                mangaNode.setProperty("Author", manga.getAuthor());
                mangaNode.setProperty("Circle", manga.getCircle());
                mangaNode.setProperty("Description", manga.getDescription());
                mangaNode.setProperty("PublishedDate", manga.getPublishedDate());
                mangaNode.setProperty("Publisher", manga.getPublisher());
                mangaNode.setProperty("Title", manga.getTitle());
                mangaNode.setProperty("UpdatedDate", manga.getUpdatedDate());
                mangaNode.setProperty("UploadedBy", username);
                mangaNode.setProperty("UploadedDate", manga.getUploadedDate());

                Node chapParentNode = mangaNode.getNode("chapters");
                Node chapterNode;
                for (Chapter chap : manga.getChapters()) {
                    if (!chapParentNode.hasNode(chap.getId())) {
                        chap.setUploadDate(new GregorianCalendar());
                        chapterNode = chapParentNode.addNode(chap.getId());
                        chapterNode.addNode("pages");
                    } else {
                        chapterNode = chapParentNode.getNode(chap.getId());
                    }

                    chapterNode.setProperty("ChapterNumber", chap.getChapterNumber());
                    chapterNode.setProperty("ChapterTitle", chap.getChapterTitle());
                    chapterNode.setProperty("UploadedBy", username);
                    chapterNode.setProperty("UploadedBy", chap.getScanGroup());
                    chapterNode.setProperty("UploadDate", chap.getUploadDate());
                }

                this.setMultivalue(mangaNode, "tags", manga.getTags());

                session.save();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to persist manga " + manga.getId(), e);
            } finally {
                lock.unlock();
            }
        }

        public List<Manga> listManga() {
            List<Manga> ret = new ArrayList();
            lock.lock();
            try {
                for (Node n : JcrUtils.getChildNodes(session.getNode("/manga"))) {
                    ret.add(getManga(n));
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "Manga listing failed", e);
            } finally {
                lock.unlock();
            }

            return ret;
        }

        public Manga getManga(String id) {
            lock.lock();

            try {
                Node readFrom = session.getNode("/manga/" + id);
                return getManga(readFrom);
            } catch (PathNotFoundException e) {
                return null;
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception retrieving manga " + id, e);
                return null;
            } finally {
                lock.unlock();
            }
        }

        public Set<String> getAllTags() {
            lock.lock();
            try {
                return readMultivalue(session.getRootNode(), "allTags");
            } catch (RepositoryException e) {
                log.log(Level.SEVERE, "Exception reading tags", e);
                return null;
            } finally {
                lock.unlock();
            }
        }

        public void setTags(Set<String> tags) {
            lock.lock();
            try {
                Node root = session.getRootNode();
                setMultivalue(root, "allTags", tags);
                session.save();
            } catch (RepositoryException e) {
                log.log(Level.SEVERE, "Exception setting tags ", e);
            } finally {
                lock.unlock();
            }

        }

        public boolean deleteManga(String id) {
            lock.lock();

            try {
                Node readFrom = session.getNode("/manga/" + id);
                readFrom.remove();
                session.save();
                return true;
            } catch (PathNotFoundException e) {
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception deleting manga " + id, e);
            } finally {
                lock.unlock();
            }


            return false;
        }

        public boolean deleteChapter(String mangaId, String chapId) {
            lock.lock();

            try {
                Node readFrom = session.getNode("/manga/" + mangaId + "/chapters/" + chapId);
                readFrom.remove();
                session.save();
                return true;
            } catch (PathNotFoundException e) {
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception deleting chapter " + mangaId + ", " + chapId, e);
            } finally {
                lock.unlock();
            }


            return false;
        }

        private String readString(Node readFrom, String prop) throws ValueFormatException, RepositoryException {
            try {
                return readFrom.getProperty(prop).getString();
            } catch (PathNotFoundException e) {
                return null;
            }
        }

        private Calendar readDate(Node readFrom, String prop) throws ValueFormatException, RepositoryException {
            try {
                return readFrom.getProperty(prop).getDate();
            } catch (PathNotFoundException e) {
                return null;
            }
        }

        private int readInt(Node readFrom, String prop) throws ValueFormatException, RepositoryException {
            try {
                return (int) readFrom.getProperty(prop).getLong();
            } catch (PathNotFoundException e) {
                return 0;
            }
        }

        private Manga getManga(Node readFrom) throws Exception {
            Manga ret = new Manga();
            ret.setId(readFrom.getName());
            ret.setArtist(readString(readFrom, "Artist"));
            ret.setAuthor(readString(readFrom, "Author"));
            ret.setCircle(readString(readFrom, "Circle"));
            ret.setDescription(readString(readFrom, "Description"));
            ret.setPublishedDate(readDate(readFrom, "PublishedDate"));
            ret.setPublisher(readString(readFrom, "Publisher"));
            ret.setTitle(readString(readFrom, "Title"));
            ret.setUpdatedDate(readDate(readFrom, "UpdatedDate"));
            ret.setUploadedBy(readString(readFrom, "UploadedBy"));
            ret.setUploadedDate(readDate(readFrom, "UploadedDate"));

            List<Chapter> chapters = new ArrayList();
            for (Node chapNode : JcrUtils.getChildNodes(readFrom.getNode("chapters"))) {
                Chapter chapter = new Chapter();
                chapter.setId(chapNode.getName());
                chapter.setChapterNumber(readInt(chapNode, "ChapterNumber"));
                chapter.setChapterTitle(readString(chapNode, "ChapterTitle"));
                chapter.setUploadDate(readDate(chapNode, "UploadDate"));

                List<Page> pages = new ArrayList();
                for (Node pageNode : JcrUtils.getChildNodes(chapNode.getNode("pages"))) {
                    pages.add(new Page(pageNode.getName()));
                }
                chapter.setPages(pages);

                chapters.add(chapter);
            }

            ret.setChapters(chapters);
            ret.setTags(readMultivalue(readFrom, "tags"));

            return ret;
        }

        public boolean copyPageToStream(String manga, String chapter, String page, OutputStream os) {
            lock.lock();

            try {
                Node n = session.getNode("/manga/" + manga + "/chapters/" + chapter + "/pages/" + page);

                if (n != null) {
                    n = n.getNode("jcr:content");
                    Binary data = n.getProperty("jcr:data").getBinary();
                    InputStream is = data.getStream();
                    IOUtils.copy(is, os);
                    is.close();
                    data.dispose();
                    return true;
                }

            } catch (PathNotFoundException e) {
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to write page to stream", e);
            } finally {
                lock.unlock();
            }

            return false;
        }

        private void setMultivalue(Node writeTo, String propName, Set<String> multivalues) {
            try {
                Value[] values = new Value[multivalues.size()];
                List<String> tagList = new ArrayList(multivalues);
                Collections.sort(tagList);

                ValueFactory valueFact = ValueFactoryImpl.getInstance();
                int i = 0;
                for (String tag : tagList) {
                    values[i++] = valueFact.createValue(tag);
                }

                writeTo.setProperty(propName, values);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception setting tags ", e);
            }
        }

        private Set<String> readMultivalue(Node readFrom, String propName) {
            try {
                Set<String> tags = new LinkedHashSet();
                Property prop = readFrom.getProperty(propName);

                for (Value v : prop.getValues()) {
                    tags.add(v.getString());
                }

                return tags;
            } catch (PathNotFoundException e) {
                return new HashSet();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception retrieving tags ", e);
                return null;
            }
        }

        public Iterator<Manga> iterator() {
            try {
                final NodeIterator iter = session.getNode("/manga").getNodes();

                return new MangaIterator(iter);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to create manga iterator", e);
            }
            return EmptyIterator.INSTANCE;
        }

        public class MangaIterator implements Iterator<Manga> {

            private Manga next;
            private NodeIterator iter;

            public MangaIterator(NodeIterator iter) {
                next = nextManga();
            }

            private Manga nextManga() {
                Manga ret = null;

                while (iter.hasNext()) {
                    Node n = iter.nextNode();
                    lock.lock();

                    try {
                        Manga tmp = DAOSession.this.getManga(n);
                        if(tmp.isPrivate() && !username.equals(tmp.getUploadedBy())) {
                            continue;
                        }
                        
                        ret = tmp;
                        break;                        
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Exception reading next manga", e);
                        return null;
                    } finally {
                        lock.unlock();
                    }
                }

                return ret;
            }

            public boolean hasNext() {
                return next != null;
            }

            public Manga next() {
                Manga ret = next;
                next = nextManga();
                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }
    }
}
