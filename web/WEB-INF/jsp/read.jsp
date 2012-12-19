<%-- 
    Document   : read
    Created on : Dec 18, 2012, 9:50:34 PM
    Author     : fishjord
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${manga.title}: ${chapter.chapterTitle}</title>
    </head>
    <body>
        <img src="page.spr?id=${manga.id}&chap=${chapter.chapterId}&page=0" />
    </body>
</html>
