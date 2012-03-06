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

    WorkoutModel model = new WorkoutModel();

    private static Integer index = 1;

    public Workout() {
        Workout.index = 1;
    }

    public void sort(){
        Collections.sort(model.getCoordinateList());
    }

    public void normalize() throws CustomException {
        logger.info("normalize()");
        Random random = new Random();
        if ((model.getCoordinateList().size() == 0) || (model.getHrData().size() == 0))
            throw  new CustomException();

        Set<Integer> coorKeySet = model.getHrData().keySet();
        Object coorKeys[] = coorKeySet.toArray();

        Set<Integer> hrKeySet = model.getHrData().keySet();
        Object hrKeys[] = hrKeySet.toArray();

        if (coorKeys.length > hrKeys.length){
            int keyCount = model.getCoordinateList().size();

            while (coorKeys.length > hrKeys.length){
                int randInt = random.nextInt(keyCount);
                Object key = coorKeys[randInt];
                model.getCoordinateList().remove(key);
            }
        }

        if (coorKeys.length < hrKeys.length){
            int keyCount = model.getHrData().size();

            while (coorKeys.length < hrKeys.length){
                int randInt = random.nextInt(keyCount);
                Object key = hrKeys[randInt];
                model.getHrData().remove(key);
            }
        }
    }

    public void printSummary(){
        StringBuffer sb  = new StringBuffer();
        sb.append("coordinateList(" + model.getCoordinateList().size() + ") ,  ");
        sb.append("hrData("+ model.getHrData().size() +")");
        logger.info( sb.toString() );
    }

    public void print(){
        printSummary();
        for (Coordinate coordinate : model.getCoordinateList())
            logger.info(coordinate.toString());

        for (Integer key : model.getHrData().keySet())
            logger.info( key +" "+model.getHrData().get(key));
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
        if(model.getCoordinateList().isEmpty() || model.getHrData().isEmpty())
            return sb;

        Integer hrKeyKey = 0;
        addGpxHeader(sb);

        for(Coordinate  coordinate : model.getCoordinateList()){
             hrKeyKey++;

            while(!model.getHrData().containsKey(hrKeyKey))
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
                    sb.append(model.getHrData().get(hrKeyKey));
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
            sb.append(formatter.format(model.getStartTime()));
            sb.append("</time>");
        sb.append("</metadata>");

        sb.append("<trk>");
            sb.append("<name> workout("+ dateFormatter.format(model.getStartTime()) +") by "+ CREATOR +"</name>");
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

    public void parseHrmFile(List<String> hrmFile) throws CustomException {
        logger.info("parseHrmFile()");
        try {
            Boolean startOfHRdata = false;
            for(String readString : hrmFile){
                readString = readString.trim();

                if (startOfHRdata) {
                    String pulseStr = readString.split("\t")[0];
                    if (!pulseStr.isEmpty()){
                        Integer pulse = parsePulse(pulseStr);
                        addPulse(pulse);
                    }
                }

                if ((!startOfHRdata) && (readString.contains("[HRData]")))
                    startOfHRdata = Boolean.TRUE;
            }
            if (model.getHrData().size() == 0)
                throw new CustomException("Cannot parse Hrm file !");
        }catch (CustomException ce ){
            throw ce;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void parseGpxFile(List<String> gpxFile) throws CustomException {
        logger.info("parseGpxFile()");
        try {
            for (int idx = 0; idx < gpxFile.size(); idx++) {
                String readstr = gpxFile.get(idx).trim();
                if (readstr.startsWith("<trkpt")) {
                    Coordinate cordinate = readCoordinate(gpxFile, idx);
                    if (cordinate.isOk())
                        model.getCoordinateList().add(cordinate);
                }
                if (readstr.startsWith("<gpx")) {
                    model.setStartTime(readStartTime(gpxFile, idx));
                }
            }
            if (model.getCoordinateList().size() == 0)
                throw new CustomException("Cannot parse Gpx file !");
        } catch (CustomException ce ){
            // do nothing!
            throw ce;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Date readStartTime(List<String> gpxFile,int idx) throws ParseException {
        Date dt = null;
        String readString = "";
        readString = gpxFile.get(idx);
        while(true){
            if (readString.startsWith("<time")){
                String timestampStr = getTagVal(readString, "time");
                dt = parseDate(timestampStr);
            }
            if (readString.contains("<trkpt>"))
                break;
            if ((idx+1) >= gpxFile.size())
                break;
            if (dt != null)
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
                String readStringCopy = new String(readString);
                String latitude = getCleanVal(getAtribValue(readStringCopy, "lat"));
                cordinate.setLatitude(latitude);
                String longitude = getCleanVal(getAtribValue(readStringCopy, "lon"));
                cordinate.setLongitude(longitude);
            }
            if (readString.startsWith("<time") && cordinate.getTimeStamp() == null){
                String readStringCopy = new String(readString);
                String timestampStr = getTagVal(readString, "time");
                Date dt = parseDate(timestampStr);
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

    private Integer parsePulse(String pulseStr) {
        Integer  rez = 0;
        try{
           rez = Integer.valueOf(pulseStr);
        }catch (NumberFormatException nfe){
            logger.severe("Unable to pares : "+ pulseStr);
            logger.severe(nfe.getMessage());
        }
        return rez;
    }

    private Date parseDate(String dateStr) throws ParseException {
        Date dt = null;
        try{
           dt = df.parse(dateStr);
        }catch (ParseException pe ){
            logger.severe("Unable to pares : "+ dateStr);
            logger.severe(pe.getMessage());
            throw pe;
        }
        return dt;
    }

    public void addPulse(Integer pulse) {
        model.getHrData().put(index++, pulse);
    }

    public WorkoutModel getModel() {
        return model;
    }

    public void setModel(WorkoutModel model) {
        this.model = model;
    }
}

