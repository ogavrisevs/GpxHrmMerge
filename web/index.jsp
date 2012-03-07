<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String tutorialUrl = "http://"+  request.getServerName() +":"+ request.getServerPort() +"/Tutorial.jsp";
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
      <title>Polar merge</title>
      <link rel="stylesheet" href="main.css">
  </head>
  <body>
    <h3> Merge your workout data (heart rate and gpx track) from
         <a href="http://www.polar.fi/en/products/maximize_performance/running_multisport/RS800CX">polar</a>
        sport clock to import into
        <a href="http://www.endomondo.com">endomondo.com</a>
        site ( read
        <a href="<%= tutorialUrl %>">tutorial</a>
        )
    </h3>

    <div>${userMessage}</div>
    <div style="color:red">${errorMessage}</div>

    <form action="/upload" method="post" enctype="multipart/form-data">
        <label > Set *.gpx file : </label>
        <input type="file" name="gpxFile" class="gpxFile">
        <br>
        <label > Set *.hrm file : </label>
        <input type="file" name="hrmFile" class="hrmFile">
        <input type="submit" value="Merge Files">
    </form>
  </body>
</html>