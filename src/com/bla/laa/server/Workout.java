package com.bla.laa.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Workout {
    private static final Logger logger = Logger.getLogger(Workout.class.getName());

    public static final String LINE_END = "\n";
    SimpleDateFormat formatter = new SimpleDateFormat(TIME_STAMP_FORMAT);
    private static final char badSimb[] = {'>', '<', '\"'};
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private DateFormat df = new SimpleDateFormat(TIME_STAMP_FORMAT);

    private List<Coordinate> coordinateList = new ArrayList<Coordinate>();
    private Map<Integer, Integer> hrData = new HashMap<Integer, Integer>();
    private static Integer index = 1;


    public void sort(){
        Collections.sort(coordinateList);
    }

    public void normalize(){
        Random random = new Random();
        if ((coordinateList.size() == 0) || (hrData.size() == 0))
            return;

        while (coordinateList.size() > hrData.size()){
            int keyCount = coordinateList.size();
            coordinateList.remove(random.nextInt(keyCount));
        }
        while (coordinateList.size() < hrData.size()){
            int keyCount = coordinateList.size();
            hrData.remove(random.nextInt(keyCount));
        }
    }

    public void printSummary(){
        logger.info("coordinateList("+ coordinateList.size()+ ")");
        logger.info("hrData("+ hrData.size() +")");
    }

    public void print(){
        printSummary();
        for (Coordinate coordinate : coordinateList)
            logger.info(coordinate.toString());

        for (Integer key : hrData.keySet())
            logger.info( key +" "+hrData.get(key));
    }

    public List<String> generateGpxFileWithHrmToList(){
        List<String> list = new ArrayList<String>();
        StringBuffer mergedFile = generateGpxFileWithHrm();
        for (String line  : mergedFile.toString().split(Workout.LINE_END))
            list.add(line);
        return list;
    }

    public StringBuffer generateGpxFileWithHrm(){
        StringBuffer sb = new StringBuffer();
        if(coordinateList.isEmpty() || hrData.isEmpty())
            return sb;

        Integer hrKeyKey = 0;
        addGpxHeader(sb);

        for(Coordinate  coordinate : coordinateList){
             hrKeyKey++;

            while(!hrData.containsKey(hrKeyKey))
                hrKeyKey++;

            sb.append("<trkpt ");
            sb.append(" lat=\"");
            sb.append(coordinate.getLatitude());
            sb.append("\" ");
            sb.append(" lon=\"");
            sb.append(coordinate.getLongitude());
            sb.append("\">");
            sb.append(LINE_END);

            /*
                sb.append("<ele>");
                sb.append(String.valueOf(coordinate.getElevation()));
                sb.append("</ele>");
                sb.append(LINE_END);
             */
                sb.append("<time>");
                sb.append(formatter.format(coordinate.getTimeStamp()));
                sb.append("</time>");
                sb.append(LINE_END);

                sb.append("<extensions>");
                sb.append(LINE_END);

                    sb.append("<gpxtpx:TrackPointExtension>");
                    sb.append(LINE_END);

                    sb.append("<gpxtpx:hr>");
                    sb.append(hrData.get(hrKeyKey));
                    sb.append("</gpxtpx:hr>");
                    sb.append(LINE_END);

                    sb.append("</gpxtpx:TrackPointExtension>");
                    sb.append(LINE_END);

                sb.append("</extensions>");
                sb.append(LINE_END);

            sb.append("</trkpt>");
            sb.append(LINE_END);

        }

        addGpxFooter(sb);
        return  sb;
    }

    public void addGpxHeader(StringBuffer sb){

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?> ");
        sb.append("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" ");
        sb.append("xmlns:gpxx=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1\"");
        sb.append("xmlns:gpxtrx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\"");
        sb.append("xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\"");
        sb.append("creator=\"Oregon 550t\" version=\"1.1\"");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/WaypointExtension/v1 http://www8.garmin.com/xmlschemas/WaypointExtensionv1.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">");
        sb.append("<metadata>");
        sb.append("<link href=\"http://www.garmin.com\">");
        sb.append("<text>Garmin International</text>");
        sb.append("</link>");
        sb.append("<time>2012-01-01T19:08:38Z</time>");
        sb.append("</metadata>");
        sb.append("<trk>");
        sb.append("<name>01-JAN-12 20:08:34</name>");
        sb.append("<extensions>");
        sb.append("<gpxtrx:TrackExtension>");
        sb.append("<gpxtrx:DisplayColor>Black</gpxtrx:DisplayColor>");
        sb.append("</gpxtrx:TrackExtension>");
        sb.append("</extensions>");
        sb.append("<trkseg>");

    }

    public void addGpxFooter(StringBuffer sb){
        sb.append("</trkseg>");
        sb.append("</trk>");
        sb.append("</gpx>");
    }

    public void readHrmFile(List<String> hrmFile){
        try {
            Boolean startOfHRdata = false;
            for(String readString : hrmFile ) {
                readString = readString.trim();

                if (startOfHRdata) {
                    String pulseStr = readString.split("\t")[0];
                    addPulse(Integer.valueOf(pulseStr));
                }

                if ((!startOfHRdata) && (readString.contains("[HRData]")))
                    startOfHRdata = Boolean.TRUE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readGpxFile(List<String> gpxFile){
        try {
            String readString;

            for(int idx = 0; idx < gpxFile.size(); idx++){
                readString = gpxFile.get(idx).trim();
                if (readString.startsWith("<trkpt")){
                    Coordinate cordinate = new Coordinate();
                    cordinate.setLatitude(getCleanVal(getAtribValue(readString, "lat")));
                    cordinate.setLongitude(getCleanVal(getAtribValue(readString, "lon")));

                    readString = gpxFile.get(++idx).trim();
                    String timestampStr = getTagVal(readString, "time");
                    Date dt = df.parse(timestampStr);
                    cordinate.setTimeStamp(dt);

                    readString = gpxFile.get(++idx).trim();
                    if (readString.contains("ele")){
                        Double val = Double.valueOf(getTagVal(readString, "ele"));
                        cordinate.setElevation(val);
                    }

                    if (cordinate.isOk())
                        addCoordinate(cordinate);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTagVal(String orgStr, String readTagName){
        String rezStr = orgStr.trim();
        rezStr = rezStr.replaceAll("<"+ readTagName +">", "");
        rezStr = rezStr.replaceAll("</"+ readTagName +">", "");
        return rezStr.trim();
    }

    public String getAtribValue(String readStr, String key) {
        readStr = readStr.trim();
        for (String splitBySpace : readStr.trim().split(" ")){
            String splitByEqual[] =  splitBySpace.trim().split("=");
            if (splitByEqual.length == 2){
                if (splitByEqual[0].contentEquals(key))
                    return splitByEqual[1];
            }
        }
        return null;
    }
    public String getCleanVal(String val){
        Boolean containBadSimb = Boolean.FALSE;
        String endStr = "";
        for (char ch : val.toCharArray()){
            containBadSimb = Boolean.FALSE;
            for (char ch2 : badSimb)
                if (ch == ch2)
                    containBadSimb = Boolean.TRUE;

            if (!containBadSimb)
                endStr += ch;
        }
        return endStr;
    }




    public void addPulse(Integer pulse) {
        this.hrData.put(index++, pulse);
    }


    public void addCoordinate(Coordinate coordinate) {
        this.coordinateList.add(coordinate);
    }

    public List<Coordinate> getCoordinateList() {
        return coordinateList;
    }

    public void setCoordinateList(List<Coordinate> coordinateList) {
        this.coordinateList = coordinateList;
    }



}

