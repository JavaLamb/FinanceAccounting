<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
</head>
<body>
<h1>Аккаунты: </h1>
<ul>
<c:forEach var="account" items="${requestScope.accounts}">
    <li>${account}</li>
</c:forEach>
</ul>
</body>
</html>