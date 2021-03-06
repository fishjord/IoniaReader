<%-- 
    Document   : complete_upload
    Created on : Dec 5, 2012, 9:19:55 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<t:wrapper title="Edit Manga">
  <form:form action="edit_manga.spr" commandName="manga" method="POST" autocomplete="false">
    <form:hidden path="id" />
    <form:hidden path="uploadedDate" />
    <form:hidden path="updatedDate" />
    <form:hidden path="uploadedBy" />
    
    <form:label path="id" cssClass="label">Id</form:label><form:input path="id" disabled="true" cssClass="field" /><br/>
    <form:label path="title" cssClass="label">Title</form:label><form:input path="title" cssClass="field" /><br/>
    <form:label path="author" cssClass="label">Author</form:label><form:input path="author" cssClass="field" /><br/>
    <form:label path="artist" cssClass="label">Artist</form:label><form:input path="artist" cssClass="field" /><br/>
    <form:label path="publisher" cssClass="label">Publisher</form:label><form:input path="publisher" cssClass="field" /><br/>
    <form:label path="circle" cssClass="label">Circle</form:label><form:input path="circle" cssClass="field" /><br/>
    <form:label path="description" cssClass="label">Description</form:label><form:textarea path="description" cssClass="field" /><br/>
    <form:label path="publishedDate" cssClass="label">Published Date (yyyy-mm-dd)</form:label><form:input path="publishedDate" cssClass="field" /><br/>
    <form:label path="uploadedDate" cssClass="label">Uploaded On</form:label><form:input path="uploadedDate" disabled="true" cssClass="field" /><br/>
    <form:label path="updatedDate" cssClass="label">Last Updated On</form:label><form:input path="updatedDate" disabled="true" cssClass="field" /><br/>
    <form:label path="uploadedBy" cssClass="label">Uploaded By</form:label><form:input path="uploadedBy" disabled="true" cssClass="field" /><br/>
    <br/><br/>
    <c:forEach var="tag" items="${allTags}">
      <form:checkbox path="tags" value="${tag}" />${tag}<br/>
    </c:forEach>
    <br/>
    <br/>
    <br/>
    <table>
      <tr>
	<th>Delete</th><th>Chapter Id</th><th>Chapter Number</th><th>Chapter Title</th><th>Scan Group</th><th>Number of Pages</th><th>Uploaded On</th>
      </tr>
      <c:forEach var="chapter" varStatus="status" items="${manga.chapters}">
	<tr>
	  <td><a href="delete_chapter.spr?manga_id=${manga.id}&chap_id=${chapter.id}" onclick="return confirm('Really delete chapter \'${chapter.chapterTitle}\'? (All unsaved changes will be lost)');" >delete</a></td>
	  <td><form:input disabled="true" path="chapters[${status.index}].id" /></td>
	  <td><form:input path="chapters[${status.index}].chapterNumber" /></td>
	  <td><form:input path="chapters[${status.index}].chapterTitle" /></td>
	  <td><form:input path="chapters[${status.index}].scanGroup" /></td>
	  <td>${fn:length(chapter.pages)}</td>
	  <td><form:input path="chapters[${status.index}].uploadDate" disabled="true" cssClass="field" /></td>
	  <form:hidden path="chapters[${status.index}].id" />
	  <form:hidden path="chapters[${status.index}].uploadDate" />
	</tr>
      </c:forEach>
    </table><br/>
    <input type="submit" name="Save"/> <a href="cancel_upload.spr">Cancel</a>
    <br/><br/>
    <a href="delete_manga.spr?id=${manga.id}" onclick="return confirm('Really delete manga \'${manga.title}\'?');" >delete</a>
  </form:form>
</t:wrapper>