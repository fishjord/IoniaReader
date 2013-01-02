/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.db;

import fishjord.ionia.jcr.MangaDAO.DAOSession;
import java.util.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private DAOSession session;
    private boolean anonymous;
    private boolean matureOk;
    
    public MangaUser(String username) {
        this(null, username,null, new HashSet(), null);
        this.anonymous = true;
        this.matureOk = false;
    }
    
    public MangaUser(String username, String displayName, Date joinedAt, Set<MangaUserRole> roles, DAOSession session) {
        this.username = this.email = username;
        this.roles = Collections.unmodifiableSet(roles);
        this.displayName = displayName;
        this.joinedAt = joinedAt;
        this.session = session;
        this.anonymous = username == null;     
    }
    
    public boolean isAnonymous() {
        return anonymous;
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

    public DAOSession getSession() {
        return session;
    }

    public void setMatureOk(boolean matureOk) {
        this.matureOk = matureOk;
    }

    public boolean isMatureOk() {
        return matureOk;
    }
}
