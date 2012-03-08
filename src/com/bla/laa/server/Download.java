package com.bla.laa.server;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

public class Download extends HttpServlet {
    private static final Logger logger = Logger.getLogger(Download.class.getName());
    public static final String TEXT_XML = "text/xml";
    PersistenceManager pm = PMF.get().getPersistenceManager();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Download.doPost()");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Download.doGet()");

        Object workoutObj = getServletContext().getAttribute(Upload.WORKOUT);
        if (workoutObj != null){
            getServletContext().removeAttribute(Upload.WORKOUT);
            Workout workout = (Workout) workoutObj;
            printRez(workout, response, request);
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            rd.forward(request, response);
            logger.info("Forward -> index.jsp");
        }

    }

    private void printBack(HttpServletResponse response, HttpServletRequest request) throws IOException {
        PrintWriter writer = response.getWriter();
        StringBuffer sb = new StringBuffer();
        addHeader(sb);
        String url = "http://"+  request.getServerName() +":"+ request.getServerPort() +"/index.jsp";
        sb.append("<H1> <a href=\""+ url +"\"> <-Back to main page</a> </H1>");
        addFooter(sb);
        writer.append(sb.toString());
        response.setContentType("text/html");
    }

    public void printRez (Workout workout, HttpServletResponse res, HttpServletRequest req) throws IOException, ServletException {
        logger.info("printRez()");
        List<String> list =  workout.generateGpxFileWithHrmToList();
        String dateStr =  workout.dateFormatter.format(workout.getModel().getStartTime());
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

    private void addHeader(StringBuffer sb){
        sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
        sb.append("\"http://www.w3.org/TR/html4/loose.dtd\">");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title></title>");
        sb.append("</head>");
        sb.append("<body>");
    }

    private void addFooter(StringBuffer sb){
        sb.append("</body>");
        sb.append("</html>");
    }


}
