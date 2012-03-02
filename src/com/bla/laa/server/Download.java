package com.bla.laa.server;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

public class Download extends HttpServlet {
    private static final Logger logger = Logger.getLogger(Download.class.getName());
    PersistenceManager pm = Pm.get().getPersistenceManager();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Download.doGet()");

        Enumeration enumeration  = request.getParameterNames();
        while (enumeration.hasMoreElements()){
            Object obj = enumeration.nextElement();
            logger.info((String) obj);
        }

        String workoutKey = request.getParameter("key");
        if (workoutKey != null){
            WorkoutModel model = (WorkoutModel) pm.getObjectById(workoutKey);
            Workout workout = new Workout();
            workout.setModel(model);
            printRez(workout, response, request);
        }

    }

    public void printRez (Workout workout, HttpServletResponse res, HttpServletRequest req) throws IOException, ServletException {
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


}
