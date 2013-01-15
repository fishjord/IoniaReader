<%-- 
    Document   : login
    Created on : Dec 20, 2012, 10:56:06 PM
    Author     : fishjord
--%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<t:wrapper title="Login">
  <p class="alert">Incorrect username or password</p>
  <form method="POST" action="j_security_check">
    <label for="j_username">Username: </label><input type="text" name="j_username" /><br/>
    <label for="j_password">Password: </label><input type="password" name="j_password" /><br/>
    <input type="submit" />
  </form>
</t:wrapper>
