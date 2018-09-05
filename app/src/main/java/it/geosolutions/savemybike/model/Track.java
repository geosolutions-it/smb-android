package it.geosolutions.savemybike.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Lorenzo Natali, GeoSolutions s.a.s.
 * Track object from SMB REST API
 */
public class Track extends BaseTrack{

    private ArrayList<Segment> segments;

    private HashMap<String, EmissionData> emissions;

    private HashMap<String, Cost> costs;

    private HashMap<String, HealthData> health;

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    public String getCretaedAt() {
        return cretaedAt;
    }

    public void setCretaedAt(String cretaedAt) {
        this.cretaedAt = cretaedAt;
    }

    public HashMap<String, EmissionData> getEmissions() {
        return emissions;
    }

    public void setEmissions(HashMap<String, EmissionData> emissions) {
        this.emissions = emissions;
    }

    public HashMap<String, Cost> getCosts() {
        return costs;
    }

    public void setCosts(HashMap<String, Cost> costs) {
        this.costs = costs;
    }

    public HashMap<String, HealthData> getHealth() {
        return health;
    }

    public void setHealth(HashMap<String, HealthData> health) {
        this.health = health;
    }






}
