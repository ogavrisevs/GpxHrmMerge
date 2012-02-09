/*
 * @(#)Main.java
 *
 * Copyright Swiss Reinsurance Company, Mythenquai 50/60, CH 8022 Zurich. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

   private static final char badSimb[] = {'>', '<', '\"'};

    public static void main(String... argv) {
        new Main();
    }


    /*
           <trkpt lat="26.89257" lon="-82.244476">
               <ele>3.99897599392</ele>
               <time>2011-11-04T15:19:46Z</time>
           </trkpt>

    */
    Main() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data\\onlyCord.gpx"));
            String readString;
            while ((readString = reader.readLine()) != null) {
                readString = readString.trim();
                if (readString.startsWith("<trkpt")){
                    System.out.println( getCleanVal(getValue(readString, "lat")));
                    System.out.println( getCleanVal(getValue(readString, "lon")));

                    readString = readString.trim();

                }


            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getValue(String readStr, String key) {
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

