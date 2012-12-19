<%-- 
    Document   : complete_upload
    Created on : Dec 5, 2012, 9:19:55 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Summary: ${manga.title}</title>
    </head>
    <body>
        <span class="manga-label">Title</span><span class="manga-field">${manga.title}</span><br/>
        <span class="manga-label">Author</span><span class="manga-field">${manga.author}</span><br/>
        <span class="manga-label">Artist</span><span class="manga-field">${manga.artist}</span><br/>
        <span class="manga-label">Publisher</span><span class="manga-field">${manga.publisher}</span><br/>
        <span class="manga-label">Circle</span><span class="manga-field">${manga.circle}</span><br/>
        <span class="manga-label">Scanlation Group</span><span class="manga-field">${manga.scanGroup}</span><br/>
        <span class="manga-label">Description</span><span class="manga-field">${manga.description}</span><br/>
        <span class="manga-label">Published Date (yyyy-mm-dd)</span><span class="manga-field">${manga.publishedDate}</span><br/>
        <span class="manga-label">Uploaded On</span><span class="manga-field">${manga.uploadedDate}</span><br/>
        <span class="manga-label">Last Updated On</span><span class="manga-field">${manga.updatedDate}</span><br/>
        <span class="manga-label">Complete</span><span class="manga-field">${manga.complete}</span><br/>
        <span class="manga-label">Mature</span><span class="manga-field">${manga.mature}</span><br/>
        <br/>
        <table>
            <tr>
                <th>Chapter</th><th>Pages</th><th>Uploaded</th><th>By</th>
            </tr>
            <c:forEach var="chapter" varStatus="status" items="${manga.chapters}">
                <tr>
                    <td><a href="read.spr?id=${manga.id}&chap=${chapter.chapterId}">${chapter.chapterTitle}</a></td>
                    <td>${chapter.numPages}</td>
                    <td>${chapter.uploadDate}</td>
                    <td>${chapter.uploadedBy}</td>
                </tr>
            </c:forEach>
        </table>
    </body>
</html>
