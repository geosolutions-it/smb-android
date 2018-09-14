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

    private EmissionData emissions;

    private Cost costs;

    private HealthData health;

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

    public EmissionData getEmissions() {
        return emissions;
    }

    public void setEmissions(EmissionData emissions) {
        this.emissions = emissions;
    }

    public Cost getCosts() {
        return costs;
    }

    public void setCosts(Cost costs) {
        this.costs = costs;
    }

    public HealthData getHealth() {
        return health;
    }

    public void setHealth(HealthData health) {
        this.health = health;
    }






}
