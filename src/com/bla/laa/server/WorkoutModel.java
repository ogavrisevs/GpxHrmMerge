package com.bla.laa.server;

import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class WorkoutModel {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key key;

    @Persistent
    private List<Coordinate> coordinateList = new ArrayList<Coordinate>();

    @Persistent(serialized="true")
    private Map<Integer, Integer> hrData = new HashMap<Integer, Integer>();

    @Persistent
    private Date startTime  = null;


    public List<Coordinate> getCoordinateList() {
        return coordinateList;
    }

    public void setCoordinateList(List<Coordinate> coordinateList) {
        this.coordinateList = coordinateList;
    }

    public Map<Integer, Integer> getHrData() {
        return hrData;
    }

    public void setHrData(Map<Integer, Integer> hrData) {
        this.hrData = hrData;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("WorkoutModel");
        sb.append("{key=").append(key);
        sb.append(", coordinateList=").append(coordinateList);
        sb.append(", hrData=").append(hrData);
        sb.append(", startTime=").append(startTime);
        sb.append('}');
        return sb.toString();
    }
}

