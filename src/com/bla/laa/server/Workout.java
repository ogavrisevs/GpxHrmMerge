package com.bla.laa.server;

import com.bla.laa.server.exception.CustomException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Workout {
    private static final Logger logger = Logger.getLogger(Workout.class.getName());
    private static final String CREATOR = "polarmrg.appspot.com";

    public static final String LINE_END = "\n";
    private static final char badSimb[] = {'>', '<', '\"'};
    SimpleDateFormat formatter = new SimpleDateFormat(TIME_STAMP_FORMAT);
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private DateFormat df = new SimpleDateFormat(TIME_STAMP_FORMAT);

    private List<Coordinate> coordinateList = new ArrayList<Coordinate>();
    private Map<Integer, Integer> hrData = new HashMap<Integer, Integer>();
    private static Integer index = 1;
    private Date  startTime  = null;

    public void sort(){
        Collections.sort(coordinateList);
    }

    public void normalize() throws CustomException {
        logger.info("normalize()");
        Random random = new Random();
        if ((coordinateList.size() == 0) || (hrData.size() == 0))
            throw  new CustomException();

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
            list.add(line + Workout.LINE_END);
        return list;
    }

    public StringBuffer generateGpxFileWithHrm(){
        logger.info("generateGpxFileWithHrm()");
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
        sb.append("<gpx ");
            sb.append("xmlns:gpxx=\"http://www.garmin.com/xmlschemas/WaypointExtension/v1\" ");
            sb.append("xmlns:gpxtrx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" ");
            sb.append("xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" ");
            sb.append("creator=\""+ CREATOR +"\" ");
            sb.append("version=\"1.0\" ");
            sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
            sb.append("xmlns=\"http://www.topografix.com/GPX/1/0\" ");
            sb.append("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\" ");
        sb.append(">");

        sb.append("<metadata>");
            sb.append("<time>");
            sb.append(formatter.format(getStartTime()));
            sb.append("</time>");
        sb.append("</metadata>");

        sb.append("<trk>");
            sb.append("<name> workout("+ dateFormatter.format(getStartTime()) +") by "+ CREATOR +"</name>");
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

    public void readHrmFile(List<String> hrmFile) throws CustomException {
        try {
            Boolean startOfHRdata = false;
            for(String readString : hrmFile){
                readString = readString.trim();

                if (startOfHRdata) {
                    String pulseStr = readString.split("\t")[0];
                    if (!pulseStr.isEmpty())
                        addPulse(Integer.valueOf(pulseStr));
                }

                if ((!startOfHRdata) && (readString.contains("[HRData]")))
                    startOfHRdata = Boolean.TRUE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (hrmFile.size() == 0)
            throw new CustomException("Cannot parse Hrm file !");
    }

    public void readGpxFile(List<String> gpxFile) throws CustomException {
        try {
            for (int idx = 0; idx < gpxFile.size(); idx++) {
                String readstr = gpxFile.get(idx).trim();
                if (readstr.startsWith("<trkpt")) {
                    Coordinate cordinate = readCoordinate(gpxFile, idx);
                    if (cordinate.isOk())
                        addCoordinate(cordinate);
                }
                if (readstr.startsWith("<gpx")) {
                    setStartTime(readStartTime(gpxFile, idx));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gpxFile.size() == 0)
            throw new CustomException("Cannot parse Gpx file !");

    }

    public Date readStartTime(List<String> gpxFile,int idx) throws ParseException {
        Date dt = null;
        String readString = "";
        readString = gpxFile.get(idx);
        while(true){
            if (readString.startsWith("<time")){
                String timestampStr = getTagVal(readString, "time");
                dt = df.parse(timestampStr);
            }
            if (readString.contains("<trkpt>"))
                break;
            if ((idx+1) >= gpxFile.size())
                break;
            readString = gpxFile.get(++idx).trim();
            if ((readString == null) || (readString.isEmpty()))
                break;
        }
        return dt;
    }

    private Coordinate readCoordinate(List<String> gpxFile, int idx ) throws ParseException {
        Coordinate cordinate = new Coordinate();
        String readString = "";
        readString = gpxFile.get(idx);
        while(true){
            if (readString.startsWith("<trkpt") && cordinate.getLatitude().isEmpty() && cordinate.getLongitude().isEmpty()){
                cordinate.setLatitude(getCleanVal(getAtribValue(readString, "lat")));
                cordinate.setLongitude(getCleanVal(getAtribValue(readString, "lon")));
            }
            if (readString.startsWith("<time") && cordinate.getTimeStamp() == null){
                String timestampStr = getTagVal(readString, "time");
                Date dt = df.parse(timestampStr);
                cordinate.setTimeStamp(dt);
            }
            if (readString.contains("</trkpt>"))
                break;
            if ((idx+1) >= gpxFile.size())
                break;
            readString = gpxFile.get(++idx).trim();

            if ((readString == null) || (readString.isEmpty()))
                break;
        }
        return  cordinate;

    }

    public String getTagVal(String orgStr, String readTagName){
        String rezStr = orgStr.trim();
        rezStr = rezStr.replaceAll("<"+ readTagName +">", "");
        rezStr = rezStr.replaceAll("</"+ readTagName +">", "");
        return rezStr.trim();
    }

    private String getAtribValue(String readStr, String key) {
        readStr = readStr.trim();
        for (String splitBySpace : readStr.trim().split(" ")){
            String splitByEqual[] =  splitBySpace.trim().split("=");
            if (splitByEqual.length == 2){
                if (splitByEqual[0].contentEquals(key))
                    return splitByEqual[1];
            }
        }

        logger.severe("getAtribValue("+ readStr +", "+ key +") = fail ! ");
        return null;
    }
    public String getCleanVal(String val){
        if ((val == null) || (val.isEmpty())){
            logger.severe("getCleanVal() == unSsuccessful! ");
            return "";
        }

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

    public Map<Integer, Integer> getHrData() {
        return hrData;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }


}

