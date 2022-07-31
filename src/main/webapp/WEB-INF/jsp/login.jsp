<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>

<style>
    .title {
        font-weight: bolder;
        font-size: large;
    }
    .subtitle {
        font-weight: bold;
        font-size: medium;
    }
    .form-row {
        float:left;
        clear:both;
        margin-top:5px;
        margin-bottom:5px;
    }
</style>

<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.9-1/core.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.9-1/md5.js"></script>
<script src="js/login.js"></script>

<body>
    
    <p class="title">Login Page</p>
    <p class="subtitle">Please enter username in lowercase.</p>
    <p class="subtitle">Username must be unique</p>
    <c:if test='${auth_fail == true}'>
        <p id="auth_fail_text" class="title" style="color:red;">
            Fail to Login, username or password may be invalid.
        </p>
    </c:if>
    
    <div>
        
        <form id="login" action="/login" method="post" onsubmit="doLogin();">
            
            <div class="form-row">
                <label for="username">username</label>
                <input id="username" name="username" type="text" onkeyup="return forceLower(this);" />
            </div>
            
            <div class="form-row">
                <label for="password">password</label>
                <input id="password" name="password" type="password" />
            </div>
            
            <div class="form-row">
                <label for="use_cookie">use cookie</label>
                <input id="use_cookie" name="use_cookie" type="checkbox" />
            </div>
            
            <input id="auth_hash" type="hidden" name="auth_hash" value="" />
            
            <input class="form-row" id="submit" type="submit" onsubmit="doLogin();" />
            
        </form>
        
    </div>
    
</body>
</html>