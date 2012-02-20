package com.bla.laa.server;

import java.util.Date;

public class Coordinate implements  Comparable<Coordinate> {

    private String latitude;
    private String longitude;
    private Date timeStamp;

    public Boolean isOk (){
        if ((longitude == null) || (longitude.length() == 0))
            return Boolean.FALSE;

        if ((latitude == null) || (latitude.length() == 0))
            return Boolean.FALSE;

        if ((timeStamp == null))
            return Boolean.FALSE;

        return Boolean.TRUE;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }



    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Coordinate");
        sb.append("{latitude='").append(latitude).append('\'');
        sb.append(", longitude='").append(longitude).append('\'');
        sb.append(", timeStamp='").append(timeStamp).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public int compareTo(Coordinate o) {
        return (timeStamp.getTime() < o.getTimeStamp().getTime() ? -1 : 1);
    }
}

