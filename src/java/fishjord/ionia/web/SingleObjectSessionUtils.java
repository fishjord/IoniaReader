/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.web;

import javax.servlet.http.HttpSession;

/**
 *
 * @author fishjord
 */
public class SingleObjectSessionUtils {    
    public static <T> void addToSession(HttpSession session, T obj) {
        session.setAttribute(obj.getClass().getCanonicalName(), obj);
    }
    
    public static <T> T getFromSession(HttpSession session, Class<T> clazz) {
        return (T)session.getAttribute(clazz.getCanonicalName());
    }
    
    public static <T> void removeFromSession(HttpSession session, Class<T> clazz) {
        session.removeAttribute(clazz.getCanonicalName());
    }
    
    public static <T> void removeFromSession(HttpSession session, T t) {
        session.removeAttribute(t.getClass().getCanonicalName());
    }
    
    public static <T> boolean isInSession(HttpSession session, Class<T> clazz) {
        return getFromSession(session, clazz) != null;
    }
}
