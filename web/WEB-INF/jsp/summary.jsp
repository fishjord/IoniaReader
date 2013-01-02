<%-- 
    Document   : complete_upload
    Created on : Dec 5, 2012, 9:19:55 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<t:wrapper title="${manga.title}">
    <span class="label">Title</span><span class="field">${manga.title}</span><br/>
    <span class="label">Author</span><span class="field">${manga.author}</span><br/>
    <span class="label">Artist</span><span class="field">${manga.artist}</span><br/>
    <span class="label">Publisher</span><span class="field">${manga.publisher}</span><br/>
    <span class="label">Circle</span><span class="field">${manga.circle}</span><br/>
    <span class="label">Scanlation Group</span><span class="field">${manga.scanGroup}</span><br/>
    <span class="label">Description</span><span class="field">${manga.description}</span><br/>
    <span class="label">Published Date (yyyy-mm-dd)</span><span class="field"><c:if test="${manga.publishedDate != null}"><fmt:formatDate value="${manga.publishedDate.time}" /></c:if></span><br/>
    <span class="label">Uploaded On</span><span class="field"><c:if test="${manga.uploadedDate != null}"><fmt:formatDate value="${manga.uploadedDate.time}" /></c:if></span><br/>
    <span class="label">Last Updated On</span><span class="field"><c:if test="${manga.updatedDate != null}"><fmt:formatDate value="${manga.updatedDate.time}" /></c:if></span><br/>
    <span class="label">Complete</span><span class="field">${manga.complete}</span><br/>
    <span class="label">Mature</span><span class="field">${manga.mature}</span><br/>
    <br/><br/>
    <span class="label">Tags</span><c:forEach var="tag" items="${manga.tags}" varStatus="status">${tag}<c:if test="${not status.last}">, </c:if></c:forEach>
            <table>
                <tr>
                    <th>Chapter</th><th>Pages</th><th>Uploaded</th>
                </tr>
        <c:forEach var="chapter" varStatus="status" items="${manga.chapters}">
            <tr>
                <td><a href="<c:url value="/manga/${manga.id}/${chapter.id}/" />">${chapter.chapterTitle}</a></td>
                <td>${chapter.numPages}</td>
                <td><c:if test="${chapter.uploadDate != null}"><fmt:formatDate value="${chapter.uploadDate.time}" /></c:if></td>
            </tr>
        </c:forEach>
    </table>
</t:wrapper>
