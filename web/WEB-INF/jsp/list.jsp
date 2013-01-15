<%-- 
    Document   : list
    Created on : Dec 22, 2012, 8:53:50 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<t:wrapper title="Manga Listing">
    <table>
        <tr></tr>
        <c:forEach var="manga" items="${mangaList}">
            <tr>
                <td class="manga-field"><a href="<c:url value="/manga/${manga.id}" />">${manga.title}</a><td/>
                <td>${manga.author}</td>
                <td>${manga.artist}</td>
                <td>${manga.publisher}</td>
                <td>${manga.circle}</td>
                <td>${manga.uploadedBy}</td>
                <td>${manga.description}</td>
                <td><c:if test="${manga.publishedDate != null}"><fmt:formatDate value="${manga.publishedDate.time}" /></c:if></td>
                <td><c:if test="${manga.uploadedDate != null}"><fmt:formatDate value="${manga.uploadedDate.time}" /></c:if></td>
                <td><c:if test="${manga.updatedDate != null}"><fmt:formatDate value="${manga.updatedDate.time}" /></c:if></td>
                <%--<td>${manga.completed}</td>--%>
                <td>${fn:length(manga.chapters)}</td>
            </tr>
        </c:forEach>
    </table>
</t:wrapper>
