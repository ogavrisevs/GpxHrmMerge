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
import java.util.Map;
import java.util.logging.Logger;

public class Upload extends HttpServlet {

    private static final Logger logger = Logger.getLogger(Upload.class.getName());


    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        ByteRange byteRange = blobstoreService.getByteRange(req);

        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("myFile");


        FileService fileService = FileServiceFactory.getFileService();
        AppEngineFile file = null;
        try {
            file = fileService.getBlobFile(blobKey);
        } catch (FileNotFoundException e) {
            logger.severe(e.getMessage());
        }

        FileReadChannel ch = null;
        try {
            ch = fileService.openReadChannel(file, false);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }


        byte[] array = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(array);
        try {
            while (ch.read(buf) != -1) {
                buf.rewind();
                byte[] a = buf.array();

                System.out.println(new String(a));
                buf.clear();
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());

        }


    }

}
