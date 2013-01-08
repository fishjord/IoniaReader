/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.db;

import fishjord.ionia.db.MangaUser.MangaUserRole;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author fishjord
 */
public class MangaReaderDB {

    private static final Logger log = Logger.getLogger(MangaReaderDB.class.getCanonicalName());
    @Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
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

            return null;// new MangaUser(username, rset.getString("display_name"), new Date(rset.getTimestamp("joined_at").getTime()), roles);
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
}
