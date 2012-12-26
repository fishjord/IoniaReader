<%-- 
    Document   : read
    Created on : Dec 18, 2012, 9:50:34 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${manga.title}: ${chapter.chapterTitle}</title>
        <script>
            var target_url = "page.spr?id=${manga.id}&chap=${chapter.chapterId}&page=";
            var page = 0;
            var last_page = ${chapter.numPages};
            
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
                if(page < last_page) {
                    page += 1;
                    document.getElementById("manga_page").src = target_url + page;
                }
                
            }
            
            function prev_page() {
                if(page > 0) {
                    page -= 1;
                    document.getElementById("manga_page").src = target_url + page;
                }
            }
            document.onkeydown = keyPressed;
        </script>
    </head>
    <body>
        <a href="javascript:void(0);" onclick="next_page();"><img style="height:900px;text-align:center;" id="manga_page" src="page.spr?id=${manga.id}&chap=${chapter.chapterId}&page=0" /></a>
    </body>
</html>
