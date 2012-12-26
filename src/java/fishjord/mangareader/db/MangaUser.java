/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.db;

import java.util.Date;
import java.util.Collections;
import java.util.Set;
import javax.servlet.http.HttpSession;

/**
 *
 * @author fishjord
 */
public class MangaUser {    
    public enum MangaUserRole { manga_user, admin };
    private Set<MangaUserRole> roles;
    private String username;
    private String email;
    private String displayName;
    private Date joinedAt;
    
    MangaUser(String username, String displayName, Date joinedAt, Set<MangaUserRole> roles) {
        this.username = this.email = username;
        this.roles = Collections.unmodifiableSet(roles);
        this.displayName = displayName;
        this.joinedAt = joinedAt;
    }

    public boolean isUserInRole(MangaUserRole role) {
        return roles.contains(role);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }
}
