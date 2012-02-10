
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    private static final char badSimb[] = {'>', '<', '\"'};
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static void main(String... argv) {
        new Main();
    }

    public Main() {
        Workout workout = new Workout();
        readGpxFile(workout, "data\\onlyCord.gpx");
        readHrmFile(workout, "data\\2012\\12013002.hrm");
    }

    public void readHrmFile(Workout workout, String fileName){



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
                    Double val = Double.valueOf(getTagVal(readString, "ele"));
                    cordinate.setElevation(val);

                    readString = reader.readLine().trim();
                    String timestampStr = getTagVal(readString, "time");
                    Date dt = df.parse(timestampStr);
                    cordinate.setTimeStamp(dt);

                    if (cordinate.isOk())
                        workout.addCoordinate(cordinate);
                }
            }
            workout.sort();
            workout.print();

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
}

