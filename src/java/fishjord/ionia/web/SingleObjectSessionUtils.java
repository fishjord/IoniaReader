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
        session.setAttribute(getSessionKey(obj.getClass()), obj);
    }
    
    public static <T> T getFromSession(HttpSession session, Class<T> clazz) {
        return (T)session.getAttribute(getSessionKey(clazz));
    }
    
    public static <T> void removeFromSession(HttpSession session, Class<T> clazz) {
        session.removeAttribute(getSessionKey(clazz));
    }
    
    public static <T> void removeFromSession(HttpSession session, T t) {
        session.removeAttribute(getSessionKey(t.getClass()));
    }
    
    public static <T> boolean isInSession(HttpSession session, Class<T> clazz) {
        return getFromSession(session, clazz) != null;
    }

    private static String getSessionKey(Class clazz) {
	String ret = clazz.getSimpleName();
	if(ret != null && ret.length() > 1) {
	    ret = Character.toLowerCase(ret.charAt(0)) + ret.substring(1);
	}

	return ret;
    }
}
