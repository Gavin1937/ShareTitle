<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>List Shared Text</title>
</head>

<style>
    table, th, td {
        border: 1px solid black;
    }
    p {
        font-weight: bold;
        font-size: large;
    }
</style>

<script type="text/javascript">
    
    function postSharetitle()
    {
        var data = document.getElementById("data").value;
        
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/api/sharetitle", true);
        xhr.setRequestHeader("Content-Type", "text/plain");
        xhr.send(data);
        
        window.reload();
    }
    
</script>

<body>
    
    <div>
        
        <p>Share new text</p>
        <form onsubmit="postSharetitle();">
            <input id="data" type="text" />
            <input type="submit" value="submit" />
        </form>
        
    </div>
    
    <div>
        <p>Current sharetitle</p>
        <table>
        <tbody>
            
            <tr>
                <th scope="col">Id</th>
                <th scope="col">Artist Name</th>
                <th scope="col">Links</th>
                <th scope="col">Time (UTC)</th>
            </tr>
            
            <c:forEach items="${websites}" var="website">
                <tr id="${website.id}">
                    <th scope="rowgroup">${website.id}</th>
                    <th>${website.title}</th>
                    <th><a href="${website.url}">${website.domain}</a></th>
                    <th>${website.time}</th>
                </tr>
            </c:forEach>
            
        </tbody>
        </table>
    </div>
    
</body>
</html>