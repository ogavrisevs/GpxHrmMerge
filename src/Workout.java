import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Workout {
    private static final String LINE_END = "\n";
    SimpleDateFormat formatter = new SimpleDateFormat(Main.TIME_STAMP_FORMAT);

    List<Coordinate> coordinateList = new ArrayList<Coordinate>();
    Map<Integer, Integer> hrData = new HashMap<Integer, Integer>();
    static Integer index = 1;


    public void sort(){
        Collections.sort(coordinateList);
    }

    public void print(){
        System.out.println("coordinateList("+ coordinateList.size()+ ")");
        System.out.println("hrData("+ hrData.size() +")");

        for (Coordinate coordinate : coordinateList)
            System.out.println(coordinate.toString());

        for (Integer key : hrData.keySet())
            System.out.println( key +" "+hrData.get(key));

    }

    public void normalize(){
        Random random = new Random();
        while (coordinateList.size() > hrData.size()){
            int keyCount = coordinateList.size();
            coordinateList.remove(random.nextInt(keyCount));
        }
        while (coordinateList.size() < hrData.size()){
            int keyCount = coordinateList.size();
            hrData.remove(random.nextInt(keyCount));
        }
    }

    public StringBuffer generateGpxFileWithHrm(){

        StringBuffer sb = new StringBuffer();
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

                sb.append("<ele>");
                sb.append(String.valueOf(coordinate.getElevation()));
                sb.append("</ele>");
                sb.append(LINE_END);

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

