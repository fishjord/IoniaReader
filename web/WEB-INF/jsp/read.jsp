<%-- 
    Document   : read
    Created on : Dec 18, 2012, 9:50:34 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:url value="/manga/${manga.id}/${chapter.id}/" var="baseUrl" />

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<t:wrapper title="${manga.title}: ${chapter.chapterTitle}">
    <script>
        var target_url = '${baseUrl}';
        var page = 0;
        var last_page = ${fn:length(chapter.pages)};
        var pages = [<c:forEach items="${chapter.pages}" var="page" varStatus="status">"${page.id}" <c:if test="${!status.last}">,</c:if></c:forEach>]
            
        function keyPressed(e) {
            var key = ( window.event ) ? event.keyCode : e.keyCode;
                
            switch(key) {
                case 39:
                    next_page();
                    break;
                case 37:
                    prev_page();
                    break;
            }
        }
            
        function next_page() {
            if(page < last_page - 1) {
                page += 1;
                load_page();
            }                
        }
            
        function prev_page() {
            if(page > 0) {
                page -= 1;
                load_page();
            }
        }
            
        function load_page() {
            document.getElementById("manga_page").src = target_url + pages[page]; 
            window.scrollTo(0,0);
            document.getElementById('top_page_select').value = (page + 1);
            document.getElementById('bottom_page_select').value = (page + 1);
        }
        
        function chap_change(select_id) {
            selector = document.getElementById(select_id);
            chap = selector.value;
            window.location = "<c:url value="/manga/${manga.id}/" />" + chap;
        }
        
        function page_change(select_id) {
            page = document.getElementById(select_id).value - 1
            load_page()
        }
            
        document.onkeydown = keyPressed;
    </script>
    <select id="top_chap_select" onchange="javascript:chap_change('top_chap_select');">
        <c:forEach items="${manga.chapters}" var="c">
            <option value="${c.id}" <c:if test="${c.id == chapter.id}">selected="true"</c:if>>${c.chapterTitle}</option>
        </c:forEach>
    </select>&nbsp;|&nbsp;
    <select id="top_page_select" onchange="javascript:page_change('top_page_select');">
        <c:forEach begin="1" end="${fn:length(chapter.pages)}" var="page">
            <option value="${page}">${page}</option>
        </c:forEach>
    </select><br/>
    <a href="javascript:void(0);" onclick="next_page();"><img style="height:900px;margin: 0 auto;display: block;" id="manga_page" /></a><br/>
    
    <select id="bottom_chap_select" onchange="javascript:chap_change('bottom_chap_select');">
        <c:forEach items="${manga.chapters}" var="c">
            <option value="${c.id}" <c:if test="${c.id == chapter.id}">selected="true"</c:if>>${c.chapterTitle}</option>
        </c:forEach>
    </select>&nbsp;|&nbsp;
    <select id="bottom_page_select" onchange="javascript:page_change('bottom_page_select');">
        <c:forEach begin="1" end="${fn:length(chapter.pages)}" var="page">
            <option value="${page}">${page}</option>
        </c:forEach>
    </select>
    <script>load_page()</script>
    </t:wrapper>
