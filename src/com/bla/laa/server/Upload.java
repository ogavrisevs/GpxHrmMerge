package com.bla.laa.server;

import com.bla.laa.server.exception.CustomException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.*;

import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Upload extends HttpServlet {
    private static final Logger logger = Logger.getLogger(Upload.class.getName());
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    FileService fileService = FileServiceFactory.getFileService();
    PersistenceManager pm = Pm.get().getPersistenceManager();

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        logger.info("Upload.doPost()");

        Enumeration enumeration  = req.getParameterNames();
        while (enumeration.hasMoreElements()){
            Object obj = enumeration.nextElement();
            logger.info((String) obj);
        }


        BlobKey gpxBlobKey = null;
        BlobKey hrmBlobKey = null;
        try{
            Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
            gpxBlobKey = blobs.get("gpxFile").get(0);
            hrmBlobKey = blobs.get("hrmFile").get(0);

            List<String> gpxFile = IOServlet.getInstance().readFile(gpxBlobKey);
            List<String> hrmFile = IOServlet.getInstance().readFile(hrmBlobKey);

            Workout workout = new Workout();
            workout.readGpxFile(gpxFile);
            workout.readHrmFile(hrmFile);
            workout.printSummary();
            workout.normalize();
            workout.printSummary();

            if ( !workout.getModel().getCoordinateList().isEmpty() && !workout.getModel().getHrData().isEmpty()){
                WorkoutModel model = workout.getModel();
                pm.makePersistent(model);
                RequestDispatcher rd = req.getRequestDispatcher("/Download?key="+ model.getKey().getId());
                rd.forward(req, res);
            }

        } catch (CustomException e) {
            returnError(res, e.getMessage());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();

        } finally {
            if (gpxBlobKey != null){
                logger.info("delete.gpxBlob "+ gpxBlobKey.toString() );
                blobstoreService.delete(gpxBlobKey);
            }
            if (hrmBlobKey != null){
                logger.info("delete.hrmBlob "+ hrmBlobKey.toString() );
                blobstoreService.delete(hrmBlobKey);
            }
        }

    }

    public void returnError(HttpServletResponse res, String msg) throws IOException {
        res.sendError( 404, msg);
    }


    public void returnError(HttpServletResponse res) throws IOException {
        res.sendError( 404, " Error reading files ");
    }

}

