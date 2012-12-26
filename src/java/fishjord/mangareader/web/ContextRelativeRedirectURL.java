/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.web;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author fishjord
 */
public class ContextRelativeRedirectURL {

    private String url;

    public ContextRelativeRedirectURL(HttpServletRequest request) {
        StringBuilder str = new StringBuilder();
        str.append(request.getContextPath());
        str.append(request.getServletPath());
        if (request.getPathInfo() != null) {
            str.append(request.getPathInfo());
        }
        if (request.getQueryString() != null) {
            str.append(request.getQueryString());
        }

        url = str.toString();
    }

    public String getRedirectUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }
}
