package com.bla.laa.server;

import com.bla.laa.server.exception.CustomException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IOServlet {
    private static final Logger logger = Logger.getLogger(IOServlet.class.getName());

    FileService fileService = FileServiceFactory.getFileService();
    private static IOServlet instance = null;

    public static IOServlet getInstance(){
        if (instance == null)
            instance = new IOServlet();

        return instance;
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
        return blobKey;
    }


    List<String> readFile(BlobKey blobKey) throws IOException, CustomException {
        logger.info("readFile("+ blobKey +")");
        FileService fileService = FileServiceFactory.getFileService();
        List<String> list = new ArrayList<String>();
        AppEngineFile file = null;
        FileReadChannel ch = null;
        try {
            file = fileService.getBlobFile(blobKey);
            ch = fileService.openReadChannel(file, false);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new CustomException();
        }

        StringBuffer sb = new StringBuffer();
        BufferedReader reader = new BufferedReader(Channels.newReader(ch, "UTF8"));
        String line = "";
        while ( (line = reader.readLine()) != null){
            list.add(line);
        }
        ch.close();

        if (list.isEmpty()){
            throw  new CustomException("Canot read file !");
        }

        return list;
    }

    public static List<String> readFile(InputStream stream){
        List<String> list = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        try{
            int len;
            byte[] buffer = new byte[8192];
            while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
                String msg = new String(buffer);
                sb.append(msg);
            }

            for (String str : sb.toString().split("\r\n"))
                list.add(str);

        } catch(Exception e){
            logger.severe(e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


}

