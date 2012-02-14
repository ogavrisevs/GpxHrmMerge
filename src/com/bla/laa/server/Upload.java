package com.bla.laa.server;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.ByteRange;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Upload extends HttpServlet {
    private static final Logger logger = Logger.getLogger(Upload.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        ByteRange byteRange = blobstoreService.getByteRange(req);

        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey gpxBlobKey = blobs.get("gpxFile");
        BlobKey hrmBlobKey = blobs.get("hrmFile");

        List<String> gpxFile = readFile(gpxBlobKey);
        List<String> hrmFile = readFile(hrmBlobKey);

        Workout workout = new Workout();
        workout.readGpxFile(gpxFile);
        workout.readHrmFile(hrmFile);
        workout.print();
        workout.normalize();
        workout.print();

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
            logger.severe(e.getMessage());
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
            logger.severe(e.getMessage());

        }

        for (String str : sb.toString().split("\r\n"))
            list.add(str);

        return list;
    }

}

