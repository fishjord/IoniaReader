<%-- 
    Document   : upload
    Created on : Dec 5, 2012, 9:01:36 PM
    Author     : fishjord
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Upload</title>
    </head>
    <body>
        <c:if test="${error}">
            ${error}
        </c:if>
        <form method="POST" enctype="multipart/form-data" action="upload.spr">
            <input type="file" name="manga_archive" />
            <input type="submit" />
        </form>
    </body>
</html>
