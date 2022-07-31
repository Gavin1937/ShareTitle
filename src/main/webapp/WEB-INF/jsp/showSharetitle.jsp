<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Show Shared Titles</title>
</head>

<link href="css/showSharetitle.css" rel="stylesheet">
<script src="js/showSharetitle.js"></script>

<body>
    
    <div>
        
        <p>Share new text</p>
        <form onsubmit="postSharetitle();">
            <input id="data" type="text" />
            <input type="submit" value="submit" />
        </form>
        
    </div>
    
    <div>
        
        <p id="table_title">Current Share Titles (Count: #)</p>
        <pre style="display:inline-block; font-size:large; font-weight:bold;">filter</pre>
        <select style="display:inline-block;" id="fileter" onchange="filterWebsites()">
            <option value="-1">Show All</option>
            <option value="0" selected>Show Unvisited</option>
            <option value="1">Show Visited</option>
        </select>
        
        <table>
        <tbody>
            
            <tr>
                <th scope="col">Id</th>
                <th scope="col">Title</th>
                <th scope="col">link</th>
                <th scope="col">visitStatus</th>
                <th scope="col">Time (UTC)</th>
            </tr>
            
            <c:forEach items="${websites}" var="website">
                <tr id="${website.id}">
                    <th class="id" scope="rowgroup">${website.id}</th>
                    <th class="title">${website.title}</th>
                    <th class="link" domain="${website.domain}" parentChild="${website.parentChild}">
                        <a href="${website.url}"></a>
                    </th>
                    <th class="visitStatus" visited="${website.getIsVisited()}">
                        <a onclick="toggleVisited('${website.id}');"></a>
                    </th>
                    <th class="time" time="${website.time}"></th>
                    <script>beautifyRow("${website.id}");</script>
                </tr>
            </c:forEach>
            
        </tbody>
        </table>
    </div>
    
    <script>
        collectWebsites();
        autoFilterWebsites();
    </script>
    
</body>
</html>
