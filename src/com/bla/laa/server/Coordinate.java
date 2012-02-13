import java.util.Date;

public class Coordinate implements  Comparable<Coordinate> {

    private String latitude;
    private String longitude;
    private Double elevation;
    private Date timeStamp;

    public Boolean isOk (){
        if ((longitude == null) || (longitude.length() == 0))
            return Boolean.FALSE;

        if ((latitude == null) || (latitude.length() == 0))
            return Boolean.FALSE;

        //if ((elevation == null) || (elevation.doubleValue() == 0d))
         //    return Boolean.FALSE;

        //if ((timeStamp == null) || (timeStamp.length() == 0))
        //    return Boolean.FALSE;

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

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
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
        sb.append(", elevation=").append(elevation);
        sb.append(", timeStamp='").append(timeStamp).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public int compareTo(Coordinate o) {
        return (timeStamp.getTime() < o.getTimeStamp().getTime() ? -1 : 1);
    }
}

