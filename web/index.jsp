<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    String tutorialUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/Tutorial.jsp";
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polar merge</title>
    <link rel="stylesheet" href="main.css">
</head>
<body>

    <div class="center">

        <h3> Merge your
            <a href="http://www.polar.fi/en/products/maximize_performance/running_multisport/RS800CX">polar</a>
            sport clock workout data (heart rate and gpx track) and import into
            <a href="http://www.endomondo.com">endomondo.com</a>
            site ( read
            <a href="<%= tutorialUrl %>">tutorial</a>
            )
        </h3>

        <div>${userMessage}</div>
        <div style="color:red">${errorMessage}</div>

        <form action="/upload" method="post" enctype="multipart/form-data">
            <label> Set gpx file : </label>
            <input type="file" name="gpxFile" class="gpxFile" size="50" title="set gpx file">
            <br>
            <label> Set hrm file : </label>
            <input type="file" name="hrmFile" class="hrmFile" size="50" title="set hrm file">
            <input type="submit" value="Merge Files">
        </form>
    </div>
</body>
</html>