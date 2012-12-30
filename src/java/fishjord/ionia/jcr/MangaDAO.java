/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.jcr;

import fishjord.mangareader.db.Chapter;
import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaUser;
import fishjord.mangareader.upload.Upload;
import fishjord.mangareader.upload.UploadedChapter;
import fishjord.mangareader.upload.UploadedPage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;

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
            
            anonSession = new DAOSession(repo.login());
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to repository", e);
        }
    }
    
    public DAOSession getAnonSession() {
        return anonSession;
    }

    public DAOSession login(MangaUser user) {
        try {
            return new DAOSession(repo.login(new SimpleCredentials(user.getUsername(), new char[]{})));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to log user in", e);
            return null;
        }
    }
    
    public void close() {
        ((RepositoryImpl)repo).shutdown();
    }

    public static class DAOSession {

        private Session session;
        private static Logger log = Logger.getLogger(DAOSession.class.getCanonicalName());
        private Lock lock = new ReentrantLock();

        public DAOSession(Session session) {
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
                    System.out.println("Persisting uploaded chapter " + uploadedChapter.getId());
                    Node chapterNode = session.getNode("/manga/" + upload.getManga().getId() + "/chapters/" + uploadedChapter.getId());
                    Node addTo = chapterNode.getNode("pages");
                    for (UploadedPage page : uploadedChapter.getPages()) {
                        System.out.println("persisting " + page.getUploadedFileName());
                        JcrUtils.putFile(addTo, page.getUploadedFileName(), page.getUploadedFileType(), new ByteArrayInputStream(page.getImage()));
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
                Node addTo;
                if (!session.nodeExists("/manga/" + manga.getId())) {
                    manga.setUploadedDate(new GregorianCalendar());
                    manga.setUploadedBy(session.getUserID());
                    addTo = session.getNode("/manga").addNode(manga.getId());
                    addTo.addNode("chapters");
                } else {
                    addTo = session.getNode("/manga/" + manga.getId());
                }

                JCRUtils.setProperties(addTo, manga);

                Node chapterNode = addTo.getNode("chapters");
                for (Chapter chap : manga.getChapters()) {
                    if (!chapterNode.hasNode(chap.getId())) {
                        chap.setUploadDate(new GregorianCalendar());
                        addTo = chapterNode.addNode(chap.getId());
                        addTo.addNode("pages");
                    } else {
                        addTo = chapterNode.getNode(chap.getId());
                    }

                    JCRUtils.setProperties(addTo, chap);
                }

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

        private Manga getManga(Node readFrom) throws Exception {
            Manga ret = new Manga();
            JCRUtils.getProperties(readFrom, ret);

            List<Chapter> chapters = new ArrayList();
            for (Node chapNode : JcrUtils.getChildNodes(readFrom.getNode("chapters"))) {
                Chapter chapter = new Chapter();
                JCRUtils.getProperties(chapNode, chapter);
                chapters.add(chapter);
            }

            ret.setChapters(chapters);

            return ret;
        }
        
        public int getPageCount(String manga, String chapter) {
            lock.lock();

            try {
                Node n = session.getNode("/manga/" + manga + "/chapters/" + chapter + "/pages");

                int ret = 0;
                NodeIterator iter = n.getNodes();
                while(iter.hasNext()) {
                    ret++;
                    iter.nextNode();
                }

                return ret;
            } catch (PathNotFoundException e) {
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to write page to stream", e);
            } finally {
                lock.unlock();
            }
            return -1;
        }

        public void copyPageToStream(String manga, String chapter, int page, OutputStream os) {
            lock.lock();

            try {
                Node n = session.getNode("/manga/" + manga + "/chapters/" + chapter + "/pages");

                NodeIterator iter = n.getNodes();
                for (int index = 0; index < page; index++) {
                    iter.nextNode();
                }

                n = iter.nextNode();
                if (n != null) {
                    n = n.getNode("jcr:content");
                    Binary data = n.getProperty("jcr:data").getBinary();
                    InputStream is = data.getStream();
                    IOUtils.copy(is, os);
                    is.close();
                    data.dispose();
                }

            } catch (PathNotFoundException e) {
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to write page to stream", e);
            } finally {
                lock.unlock();
            }
        }
    }
}
