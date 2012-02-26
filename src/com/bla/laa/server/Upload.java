package com.bla.laa.server;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Upload extends HttpServlet {
    private static final Logger logger = Logger.getLogger(Upload.class.getName());
    FileService fileService = FileServiceFactory.getFileService();
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        logger.info("Upload.doPost()");

        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        BlobKey gpxBlobKey = blobs.get("gpxFile").get(0);
        BlobKey hrmBlobKey = blobs.get("hrmFile").get(0);

        List<String> gpxFile = readFile(gpxBlobKey);
        List<String> hrmFile = readFile(hrmBlobKey);

        if ((gpxFile.isEmpty()) || (hrmFile.isEmpty())){
            res.sendError( 404, " Error reading files ");
            return;
        }

        Workout workout = new Workout();
        workout.readGpxFile(gpxFile);
        workout.readHrmFile(hrmFile);
        workout.printSummary();
        workout.normalize();
        workout.printSummary();

        if ( workout.getCoordinateList().isEmpty() || workout.getHrData().isEmpty()){
            res.sendError( 404, " Error processing files ");
            return;
        } else {
            List<String> list =  workout.generateGpxFileWithHrmToList();
            String dateStr =  workout.dateFormatter.format(workout.getStartTime());
            res.setContentType("text/xml");
            res.setHeader("Content-Disposition", "attachment; fileName=workout_"+ dateStr +".gpx");
            int size = 0;
            ServletOutputStream out = res.getOutputStream();
            for (String str : list){
                out.write(str.getBytes());
                size += str.getBytes().length;
            }
            res.setContentLength(size);
        }

        //BlobKey blobKey = writeFile(list);
        //blobstoreService.serve(blobKey, res);
    }

    public BlobKey writeFile(List<String> list) throws IOException {
        AppEngineFile file = fileService.createNewBlobFile("text/plain");
        boolean lock = false;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

        PrintWriter printWriter = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        for(String str : list)
            printWriter.println(str);

        printWriter.close();
        writeChannel.close();

        BlobKey blobKey = null;

        // app enigine bug not returning blobkey
        while (blobKey == null)
            blobKey = fileService.getBlobKey(file);

        //logger.info(file.getFullPath());
        //logger.info(blobKey.toString());

        return blobKey;
    }


    private List<String> readFile(BlobKey blobKey) {
        FileService fileService = FileServiceFactory.getFileService();
        List<String> list = new ArrayList<String>();
        AppEngineFile file = null;
        FileReadChannel ch = null;
        try {
            file = fileService.getBlobFile(blobKey);
            ch = fileService.openReadChannel(file, false);
        } catch (Exception e) {
            //logger.severe(e.getMessage());
        }

        StringBuffer sb = new StringBuffer();
        byte[] array = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(array);
        try {
            while (ch.read(buf) != -1) {
                buf.rewind();
                byte[] a = buf.array();
                sb.append(new String(a));
                buf.clear();
            }
        } catch (IOException e) {
            //logger.severe(e.getMessage());

        }

        for (String str : sb.toString().split("\r\n"))
            list.add(str);

        return list;
    }

}

