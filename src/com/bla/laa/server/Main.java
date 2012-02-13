
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Main {

    private static final char badSimb[] = {'>', '<', '\"'};
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private DateFormat df = new SimpleDateFormat(TIME_STAMP_FORMAT);

    public static void main(String... argv) {
        new Main();
    }

    public Main() {

        Workout workout = new Workout();
        readGpxFile(workout, "data\\13.02.2012\\12021301.gpx");
        readHrmFile(workout, "data\\13.02.2012\\12021301.hrm");
        workout.print();
        workout.normalize();

        StringBuffer sb = workout.generateGpxFileWithHrm();

        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("out.gpx"));
            writer.write(sb.toString());
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void readHrmFile(Workout workout, String fileName){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String readString;

            Boolean startOfHRdata = false;
            while ((readString = reader.readLine()) != null) {
                readString = readString.trim();

                if (startOfHRdata) {
                    String pulseStr = readString.split("\t")[0];
                    workout.addPulse(Integer.valueOf(pulseStr));
                }

                if ((!startOfHRdata) && (readString.contains("[HRData]")))
                    startOfHRdata = Boolean.TRUE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readGpxFile(Workout workout, String fileName){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String readString;

            while ((readString = reader.readLine()) != null) {
                readString = readString.trim();
                if (readString.startsWith("<trkpt")){
                    Coordinate cordinate = new Coordinate();
                    cordinate.setLatitude(getCleanVal(getAtribValue(readString, "lat")));
                    cordinate.setLongitude(getCleanVal(getAtribValue(readString, "lon")));

                    readString = reader.readLine().trim();
                    String timestampStr = getTagVal(readString, "time");
                    Date dt = df.parse(timestampStr);
                    cordinate.setTimeStamp(dt);

                    readString = reader.readLine().trim();
                    if (readString.contains("ele")){
                        Double val = Double.valueOf(getTagVal(readString, "ele"));
                        cordinate.setElevation(val);
                    }

                    if (cordinate.isOk())
                        workout.addCoordinate(cordinate);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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

    private void testRand() {
        Random random = new Random(100);

        Map<Integer, Integer> map = new TreeMap<Integer, Integer>();

        for (Integer i=0 ; i < 1500; i++){
            Integer key = random.nextInt(1000);
            Integer val = map.get( key);
            if (val == null)
                map.put( key, 1);
            else
                map.put( key, ++val);

        }

        for (Integer keyyy : map.keySet())
            System.out.println(keyyy +" "+ map.get(keyyy));
    }

}

