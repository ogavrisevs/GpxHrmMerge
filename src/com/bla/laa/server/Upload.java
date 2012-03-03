package com.bla.laa.server;

import com.bla.laa.server.exception.CustomException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Upload extends HttpServlet {
    private static final Logger logger = Logger.getLogger(Upload.class.getName());
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    FileService fileService = FileServiceFactory.getFileService();
    PersistenceManager pm = PMF.get().getPersistenceManager();

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        logger.info("Upload.doPost()");

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

                String url = "http://"+  req.getServerName() +":"+ req.getServerPort() +"/download?id="+ model.getKey().getId();
                //RequestDispatcher rd = req.getRequestDispatcher(url);
                //rd.forward(req, res);
                res.sendRedirect(url);
                logger.info(model.getKey().toString());

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
           // pm.close();
        }

    }

    public void returnError(HttpServletResponse res, String msg) throws IOException {
        res.sendError( 404, msg);
    }


    public void returnError(HttpServletResponse res) throws IOException {
        res.sendError(404, " Error reading files ");
    }

}

