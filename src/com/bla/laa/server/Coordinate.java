package com.bla.laa.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.Date;
import javax.jdo.annotations.IdentityType;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Coordinate implements  Comparable<Coordinate> {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key key;

    @Persistent
    private String latitude = "";

    @Persistent
    private String longitude = "";

    @Persistent
    private Date timeStamp = null;

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

