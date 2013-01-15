<%-- 
    Document   : wrapper
    Created on : Dec 29, 2012, 10:12:29 PM
    Author     : fishjord
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@tag description="generic wrapper" pageEncoding="UTF-8"%>
<%@attribute name="title" %>
<html>
    <head>
        <title>${title}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="<c:url value="/resources/manga.css"/>" />
	<link rel="icon" type="image/png" href="<c:url value="/resources/fav.png" />" />
    </head>
    <body>
        <div class="container">
            <div class="header">
                <a href="<c:url value="/" />" style="{}"><h1 class="banner" style="font-style: italic">Ionia Reader</h1></a>
                <div style="position: absolute;top: 0;right: 0;height:50px;margin-right:10%">
                    <c:choose>
                        <c:when test="${mangaUser == null or mangaUser.anonymous}">
                            <a href="<c:url value="/admin/bounce.spr" />">login</a>
                        </c:when>
                        <c:otherwise>
                            Hello, ${mangaUser.displayName}&nbsp;|&nbsp;<c:if test="${manga != null}"><a href="<c:url value="/admin/edit_manga.spr"><c:param name="id" value="${manga.id}"/></c:url>">Edit ${manga.title}</a>&nbsp;|&nbsp;</c:if><a href="<c:url value="/admin/upload.spr" />">Upload Manga</a>&nbsp;|&nbsp;<a href="<c:url value="/logout.spr" />">Logout</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="content">
              <jsp:doBody/>                
            </div>
        </div>
    </body>
</html>
