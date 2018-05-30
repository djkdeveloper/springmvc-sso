<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录</title>

</head>

<body>
用户名：<input id="username" type="text"/> <br/>
密&nbsp;&nbsp;&nbsp;码：<input id="password" type="password"/> <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<button onclick="login()">登录</button>

</body>

</html>

<script src="<%=basePath %>/js/jquery.js"></script>

<script>

    /**
     * 登录
     */
    function login() {
        $.ajax({
            url: '<%=basePath%>/login',
            method: 'post',
            data: {'userName': $("#username").val(), 'password': $("#password").val()},
            success: function (data) {
                if (data != 0) {
                    alert('登录失败');
                } else {
                    // 登录成功后跳转至客户端去
                    window.location.href = '<%=basePath%>/loginsuccess';
                }
            }
        });
    }
</script>