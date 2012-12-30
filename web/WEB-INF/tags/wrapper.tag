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
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1 class="banner" style="font-style: italic">Ionia Reader</h1>
            </div>
            <div class="content">
              <jsp:doBody/>                
            </div>
        </div>
    </body>
</html>
