<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2022/5/26
  Time: 14:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
        <jsp:forward page="first.jsp">
            <jsp:param name="name" value="gavin"/>
        </jsp:forward>
</body>
</html>
