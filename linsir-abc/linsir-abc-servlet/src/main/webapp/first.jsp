<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2022/5/26
  Time: 9:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" errorPage="erro.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="static sun.misc.MessageUtils.out" %>
<%@include file="erro.jsp"%>
<jsp:include page="erro.jsp" />
<html>
<head>

    <title>Title</title>
</head>
<body>
    now：<%= new java.util.Date()%>
    <h1><%= new Date()%></h1>
    <%System.out.println("10");
    out.println("10");%>
    <br/>
    <%!
        int b = 20;
    %>
    <%="今天天气好啊 "%>
    <%
        String name = request.getParameter("name");
    %>

   <!--  10/0 -- >

</body>
</html>
<%@include file="index.jsp"%>>
