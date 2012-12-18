/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.db;

import fishjord.mangareader.db.MangaUser.MangaUserRole;
import fishjord.mangareader.upload.UploadedPage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author fishjord
 */
public class MangaReaderDB {

    private static final Logger log = Logger.getLogger(MangaReaderDB.class.getCanonicalName());
    @Autowired
    private DataSource dataSource;
    private File pageImageDir;

    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    public void setPageImageDir(File dir) {
        this.pageImageDir = dir;
    }
    
    public File getChapterDir(int mangaId, int chapterId) {
        return new File(pageImageDir, mangaId + "/" + chapterId);
    }
    
    private void saveChapterImages(int mangaId, int chapId, List<UploadedPage> newPages) throws IOException {
        File chapDir = getChapterDir(mangaId, chapId);
        
        if(chapDir.exists()) {
            FileUtils.deleteDirectory(chapDir);
        }
        
        if(!chapDir.mkdirs()) {
            throw new IOException("Failed to make chapter directory " + chapDir);
        }
        
        for(int index = 0;index < newPages.size();index++) {
            UploadedPage page = newPages.get(index);
                        
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(page.getImage()));
            ImageIO.write(image, "png", new File(chapDir, index + ".png"));
        }
    }

    public int updateManga(Manga m) {
        Connection conn = null;
        PreparedStatement mangaInsertStmt = null;
        PreparedStatement mangaUpdateStmt = null;
        PreparedStatement chapterInsertStmt = null;
        PreparedStatement chapterUpdateStmt = null;
        PreparedStatement stmt = null;
        Statement tmp = null;
        ResultSet rset = null;
        int ret;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            tmp = conn.createStatement();

            mangaInsertStmt = conn.prepareStatement("insert into manga "
                    + "(title, author, artist, publisher, circle, scan_grp, description, published_on, complete, mature, id, updated_at, uploaded_at)"
                    + " VALUES "
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())");
            mangaUpdateStmt = conn.prepareStatement("update manga set title=?, author=?, artist=?, publisher=?, circle=?, scan_grp=?, description=?, published_on=?, complete=?, mature=?, updated_at=now() where id=?");
            chapterInsertStmt = conn.prepareStatement("insert into chapter "
                    + "(chap_title, chap_num, num_pages, title_page, chap_id, uploaded_at, uploaded_by, manga_id) "
                    + "VALUES (?, ?, ?, ?, ?, now(), ?, ?)");
            chapterUpdateStmt = conn.prepareStatement("update chapter set chap_title=?, chap_num=?, num_pages=?, title_page=? where chap_id=?");

            if (m.getId() == null) {
                rset = tmp.executeQuery("select nextval('manga_id_seq')");
                rset.next();
                m.setId(rset.getInt(1));
                rset.close();

                stmt = mangaInsertStmt;
            } else {
                stmt = mangaUpdateStmt;
            }

            stmt.setString(1, m.getTitle());
            stmt.setString(2, m.getAuthor());
            stmt.setString(3, m.getArtist());
            stmt.setString(4, m.getPublisher());
            stmt.setString(5, m.getCircle());
            stmt.setString(6, m.getScanGroup());
            stmt.setString(7, m.getDescription());
            if(m.getPublishedDate() == null) {
                stmt.setNull(8, java.sql.Types.DATE);
            } else {
                stmt.setDate(8, new java.sql.Date(m.getPublishedDate().getTime()));
            }
            stmt.setBoolean(9, m.isComplete());
            stmt.setBoolean(10, m.isMature());
            stmt.setInt(11, m.getId());

            stmt.execute();

            for (Chapter c : m.getChapters()) {
                if (c.getChapterId() == null) {
                    stmt = chapterInsertStmt;
                    rset = tmp.executeQuery("select nextval('chapter_id_seq')");
                    rset.next();
                    c.setChapterId(rset.getInt(1));
                    rset.close();
                    
                    stmt.setString(6, "jrdn.fish@gmail.com");
                    stmt.setInt(7, m.getId());
                }
                
                if(c.getNewPages() != null) {
                    this.saveChapterImages(m.getId(), c.getChapterId(), c.getNewPages());
                }

                stmt.setString(1, c.getChapterTitle());
                stmt.setInt(2, c.getChapterNumber());
                stmt.setInt(3, c.getNumPages());
                stmt.setInt(4, c.getTitlePage());
                stmt.setInt(5, c.getChapterId());

                stmt.execute();
            }


            conn.commit();
            conn.setAutoCommit(true);

            return m.getId();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ee) {
            }
            throw new RuntimeException(e);
        } finally {

            try {
                if (rset != null) {
                    rset.close();
                }

                if (tmp != null) {
                    tmp.close();
                }

                if (mangaInsertStmt != null) {
                    mangaInsertStmt.close();
                }

                if (mangaUpdateStmt != null) {
                    mangaUpdateStmt.close();
                }

                if (chapterInsertStmt != null) {
                    chapterInsertStmt.close();
                }

                if (chapterUpdateStmt != null) {
                    chapterUpdateStmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Failed to close connection resources", e);
            }
        }

    }

    public Manga getManga(int mangaId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement("select chap_id, chap_num, manga_id, chap_title, title_page, uploaded_at, uploaded_by, num_pages from chapter where manga_id=? order by chap_num");
            stmt.setInt(1, mangaId);

            List<Chapter> chapters = new ArrayList();
            rset = stmt.executeQuery();
            while (rset.next()) {
                chapters.add(new Chapter(rset.getInt("chap_id"), rset.getInt("chap_num"), rset.getInt("title_page"), rset.getInt("num_pages"), new Date(rset.getTimestamp("uploaded_at").getTime()), rset.getString("chap_title"), rset.getString("uploaded_by")));
            }

            rset.close();
            stmt.close();

            stmt = conn.prepareStatement("select tag from tag where manga_id=?");
            stmt.setInt(1, mangaId);
            rset = stmt.executeQuery();

            List<String> tags = new ArrayList();
            while (rset.next()) {
                tags.add(rset.getString("tag"));
            }

            rset.close();
            stmt.close();

            stmt = conn.prepareStatement("select title, author, artist, publisher, circle, scan_grp, description, published_on, uploaded_at, updated_at, complete, mature from manga where id=?");
            stmt.setInt(1, mangaId);

            rset = stmt.executeQuery();

            if (!rset.next()) {
                throw new SQLException("No manga with id " + mangaId);
            }

            return new Manga(mangaId,
                    rset.getString("title"),
                    rset.getString("author"),
                    rset.getString("artist"),
                    rset.getString("publisher"),
                    rset.getString("circle"),
                    rset.getString("scan_grp"),
                    rset.getString("description"),
                    new Date(rset.getDate("published_on").getTime()),
                    new Date(rset.getTimestamp("uploaded_at").getTime()),
                    new Date(rset.getTimestamp("updated_at").getTime()),
                    rset.getBoolean("complete"),
                    rset.getBoolean("mature"),
                    chapters,
                    tags);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {

            try {
                if (rset != null) {
                    rset.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Failed to close connection resources", e);
            }
        }
    }

    public Manga getManga(String mangaName) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        Integer mangaId;

        try {
            conn = dataSource.getConnection();

            stmt = conn.prepareStatement("select id from manga where title=?");
            stmt.setString(1, mangaName);

            rset = stmt.executeQuery();

            if (!rset.next()) {
                return null;
            }

            mangaId = rset.getInt("id");

            stmt = conn.prepareStatement("select chap_id, manga_id, chap_title, uploaded_at, uploaded_by from chapter where manga_id=? order by chap_id");
            stmt.setInt(1, mangaId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {

            try {
                if (rset != null) {
                    rset.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Failed to close connection resources", e);
            }
        }

        return getManga(mangaId);
    }

    public MangaUser getMangaUser(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement("select userrole from user_role where username=?");
            stmt.setString(1, username);

            rset = stmt.executeQuery();
            Set<MangaUserRole> roles = new HashSet();

            while (rset.next()) {
                roles.add(MangaUserRole.valueOf(rset.getString("userrole")));
            }

            rset.close();
            stmt.close();

            stmt = conn.prepareStatement("select display_name, joined_at from manga_user where username=?");
            stmt.setString(1, username);

            rset = stmt.executeQuery();

            if (!rset.next()) {
                throw new SQLException("No user for username " + username);
            }

            return new MangaUser(username, rset.getString("display_name"), new Date(rset.getTimestamp("joined_at").getTime()), roles);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {

            try {
                if (rset != null) {
                    rset.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.log(Level.SEVERE, "Failed to close connection resources", e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BasicDataSource ds = new BasicDataSource();
        ds.setUsername("manga");
        ds.setPassword("manga");
        ds.setDriverClassName(org.postgresql.Driver.class.getCanonicalName());
        ds.setUrl("jdbc:postgresql://arael/manga_db");

        MangaReaderDB test = new MangaReaderDB();
        test.dataSource = ds;

        MangaUser user = test.getMangaUser("jrdn.fish@gmail.com");

        System.out.println(user.getDisplayName());
        System.out.println(user.getEmail());
        System.out.println(user.getJoinedAt());

        System.out.println(test.getManga(1).getTitle());
        System.out.println(test.getManga("test_title"));
    }
}
