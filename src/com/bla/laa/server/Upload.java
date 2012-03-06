package com.bla.laa.server;

import com.bla.laa.server.exception.CustomException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Upload extends HttpServlet {
    private static final Logger logger = Logger.getLogger(Upload.class.getName());
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    FileService fileService = FileServiceFactory.getFileService();
    PersistenceManager pm = PMF.get().getPersistenceManager();

    List<String> gpxFile = null;
    List<String> hrmFile = null;

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        logger.info("Upload.doPost()");

        try{
            getUploaded(req, res);
            Workout workout = new Workout();
            workout.parseGpxFile(gpxFile);
            workout.parseHrmFile(hrmFile);
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

        } catch (CustomException ce) {
            returnError(res, ce.getMessage());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            e.printStackTrace();
            returnError(res, e.getMessage());
        }
    }

    public void returnError(HttpServletResponse res, String msg) throws IOException {
        res.sendError( 404, msg);
    }

    public void returnError(HttpServletResponse res) throws IOException {
        res.sendError(404, " Error reading files ");
    }

    public void getUploaded(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, CustomException {
        try {
            List<String> list = new ArrayList<String>();

            ServletFileUpload upload = new ServletFileUpload();
            res.setContentType("text/plain");

            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream stream = item.openStream();

                if (item.isFormField()) {
                    logger.warning("Got a form field: " + item.getFieldName());
                } else {
                    logger.warning("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());
                    if (item.getFieldName().contentEquals("gpxFile"))
                        gpxFile = IOServlet.getInstance().readFile(stream);
                    else if (item.getFieldName().contentEquals("hrmFile"))
                        hrmFile = IOServlet.getInstance().readFile(stream);
                }
            }

            if ((gpxFile == null) || (gpxFile.size() == 0))
                throw  new CustomException("Wrong gpx file");

            if ((hrmFile == null) || (hrmFile.size() == 0))
                throw  new CustomException("Wrong hrm file");

        } catch (CustomException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

