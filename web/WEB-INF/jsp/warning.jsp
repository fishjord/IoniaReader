<%-- 
    Document   : warning
    Created on : Jan 1, 2013, 2:09:01 AM
    Author     : fishjord
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<t:wrapper title="Mature Content Warning">
    <h1>Warning! Warning!</h1>
    <p class="alert">Warning, this content may contain mature content including but not limited to: blood, gore, sexual content, adult situations.  By clicking 'I agree' you assert that you are old enough to legally view such content.</p>
    <a href="<c:url value="/mature_warning.spr?ok=true"/>">I agree</a> | <a href="<c:url value="/"/>">Get me out of here!</a>
</t:wrapper>
