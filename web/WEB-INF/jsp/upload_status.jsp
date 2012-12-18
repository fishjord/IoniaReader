<%-- 
    Document   : upload_status
    Created on : Dec 6, 2012, 7:39:34 PM
    Author     : fishjord
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <c:if test="${refresh}">
            <meta http-equiv="refresh" content="15">
        </c:if>
        <title>Upload Status: ${uploadTask.status}</title>
    </head>
    <body>
        Processing upload: ${uploadTask.status}<br/>
        <c:forEach items="${uploadTask.messages}" var="message">
            ${message}<br/>
        </c:forEach>
            
        <c:if test="${refresh}">
            This page will refresh every 15 seconds until your upload is processed.
        </c:if>
    </body>
</html>
