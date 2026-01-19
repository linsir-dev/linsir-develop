<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2022/5/26
  Time: 14:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2022/5/26
  Time: 14:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>useBean</title>
</head>

    <jsp:useBean id="user" class="com.linsir.abc.User"/>
    <jsp:setProperty name="user" property="username" value="tom"/>
    <jsp:setProperty name="user" property="password" value="123456789"/>
    <jsp:getProperty name="user" property="username"/>
    <jsp:getProperty name="user" property="password"/>


</body>
</html>
