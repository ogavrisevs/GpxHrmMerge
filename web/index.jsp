<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
      <title>Simple jsp page</title>
      <link rel="stylesheet" href="main.css">
  </head>
  <body>
    <h3> Merge your workout data (heart rate and gpx track) from
         <a href="http://www.polar.fi/en/products/maximize_performance/running_multisport/RS800CX">polar</a>
        sport clock to import into
        <a href="http://www.endomondo.com">endomondo.com</a> site !!!
    </h3>
    <form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
        <label > Set *.gpx file : </label>
        <input type="file" name="gpxFile" class="gpxFile">
        <br>
        <label > Set *.hrm file : </label>
        <input type="file" name="hrmFile" class="hrmFile">
        <input type="submit" value="Merge Files">
    </form>

    <br>
    <h3> Toturial </h3>
    <h4> 1. Import your data from sport clock to Pc (use :
        <a href="http://www.polar.fi/en/products/training_software"> Polar ProTrainer5</a>
        )
    </h4>
    <img src="\img\Transfere.png" alt="some_text"/>

    <h4> 2. Find importetd files on PC (where "user name" is your Windows user)</h4>
    <img src="\img\FilesonPc.png" alt="some_text"/>

    <h4> 3. Go to
    <a href="http://polarmrg.appspot.com"> PolarMrg site </a>
        and upload gpx and hrm files ( there will be two hrm files ! chose file with smallest size ).
    </h4>
    <img src="\img\UploadF.png" alt="some_text"/>

    <h4> 4. Save mergeet file on PC </h4>
    <h4> 5. Goto  :  <a href="http://www.endomondo.com">endomondo.com</a>
        and login.
    </h4>
    <h4> 6. Chose : Workouts ->  Import Workout -> Import from file </h4>
    <img src="\img\EndoUpload.png" alt="some_text"/>

    <h4> 7. Choose and upload merget worout gpx file  </h4>



  </body>
</html>