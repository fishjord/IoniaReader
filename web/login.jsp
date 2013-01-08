<%-- 
    Document   : login
    Created on : Dec 20, 2012, 10:56:06 PM
    Author     : fishjord
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <form method="POST" action="j_security_check">
            <input type="text" name="j_username" />
            <input type="password" name="j_password" />
            <input type="submit" />
        </form>
    </body>
</html>
