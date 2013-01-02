<%-- 
    Document   : complete_upload
    Created on : Dec 5, 2012, 9:19:55 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Finish Upload</title>
    </head>
    <body>
        <form:form action="edit_manga.spr" commandName="manga" method="POST" autocomplete="false">
            <form:hidden path="id" />
            <form:hidden path="uploadedDate" />
            <form:hidden path="updatedDate" />
            <form:hidden path="uploadedBy" />

            <form:label path="id" cssClass="manga-edit-label">Id</form:label><form:input path="id" disabled="true" cssClass="manga-edit-field" /><br/>
            <form:label path="title" cssClass="manga-edit-label">Title</form:label><form:input path="title" cssClass="manga-edit-field" /><br/>
            <form:label path="author" cssClass="manga-edit-label">Author</form:label><form:input path="author" cssClass="manga-edit-field" /><br/>
            <form:label path="artist" cssClass="manga-edit-label">Artist</form:label><form:input path="artist" cssClass="manga-edit-field" /><br/>
            <form:label path="publisher" cssClass="manga-edit-label">Publisher</form:label><form:input path="publisher" cssClass="manga-edit-field" /><br/>
            <form:label path="circle" cssClass="manga-edit-label">Circle</form:label><form:input path="circle" cssClass="manga-edit-field" /><br/>
            <form:label path="scanGroup" cssClass="manga-edit-label">Scanlation Group</form:label><form:input path="scanGroup" cssClass="manga-edit-field" /><br/>
            <form:label path="description" cssClass="manga-edit-label">Description</form:label><form:textarea path="description" cssClass="manga-edit-field" /><br/>
            <form:label path="publishedDate" cssClass="manga-edit-label">Published Date (yyyy-mm-dd)</form:label><form:input path="publishedDate" cssClass="manga-edit-field" /><br/>
            <form:label path="uploadedDate" cssClass="manga-edit-label">Uploaded On</form:label><form:input path="uploadedDate" disabled="true" cssClass="manga-edit-field" /><br/>
            <form:label path="updatedDate" cssClass="manga-edit-label">Last Updated On</form:label><form:input path="updatedDate" disabled="true" cssClass="manga-edit-field" /><br/>
            <form:label path="uploadedBy" cssClass="manga-edit-label">Uploaded By</form:label><form:input path="uploadedBy" disabled="true" cssClass="manga-edit-field" /><br/>
            <br/><br/>
            <c:forEach var="tag" items="${allTags}">
                <form:checkbox path="tags" value="${tag}" />${tag}<br/>
            </c:forEach>
                <br/>
                <br/>
                <br/>
                <table>
                    <tr>
                    <%--<th>Delete</th>--%><th>Chapter Id</th><th>Chapter Number</th><th>Chapter Title</th><th>Title Page</th><th>Number of Pages</th><th>Uploaded On</th>
                </tr>
                <c:forEach var="chapter" varStatus="status" items="${manga.chapters}">
                    <tr>
                        <%--<td><a href="delete_chapter.spr?manga_id=${manga.id}&chap_idx=${status.index}" onclick="return confirm('Really delete chapter \'${chapter.chapterTitle}\'? (Delete will be finalized after saving)');" >delete</a></td>--%>
                        <td><form:input disabled="true" path="chapters[${status.index}].id" /></td>
                        <td><form:input path="chapters[${status.index}].chapterNumber" /></td>
                        <td><form:input disabled="true" path="chapters[${status.index}].uploadDate" /></td>
                        <form:hidden path="chapters[${status.index}].id" />
                        <form:hidden path="chapters[${status.index}].uploadDate" />
                    </tr><%--
                    <c:if test="${newChapters[chapter.chapterId] != null}">
                        <tr><td colspan="8">
                        <center>
                            <c:forEach var="newPage" items="${newChapters[chapter.chapterId].pages}">
                                ${newPage}<br/>
                            </c:forEach>
                        </center>
                    </td></tr>
                </c:if>--%>
            </c:forEach>
    </table><br/>
    <input type="submit" name="Save"/> <a href="cancel_upload.spr">Cancel</a>
</form:form>
</body>
</html>
