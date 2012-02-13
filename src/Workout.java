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
        return  sb;
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

