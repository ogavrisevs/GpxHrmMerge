<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Simple jsp page</title></head>
  <body>
    <form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
        <label > gpx file2</label>
        <input type="file" name="gpxFile" title="title" value="value" >
        <br>
        <label > hrm file2</label>
        <input type="file" name="hrmFile">
        <input type="submit" value="Submit">
    </form>
  </body>
</html>