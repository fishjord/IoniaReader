<%-- 
    Document   : list
    Created on : Dec 22, 2012, 8:53:50 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Manga Listing</title>
    </head>
    <body>
        <table>
            <tr></tr>
            <c:forEach var="manga" items="${mangaList}">
                <tr>
                    <td class="manga-field"><a href="summary.spr?id=${manga.id}">${manga.title}</a><td/>
                    <td>${manga.author}</td>
                    <td>${manga.artist}</td>
                    <td>${manga.publisher}</td>
                    <td>${manga.circle}</td>
                    <td>${manga.scanGroup}</td>
                    <td>${manga.description}</td>
                    <td>${manga.publishedDate}</td>
                    <td>${manga.uploadedDate}</td>
                    <td>${manga.updatedDate}</td>
                    <%--<td>${manga.completed}</td>--%>
                    <td>${manga.numChapters}</td>
                </tr>
            </c:forEach>
        </table>
    </body>
</html>
