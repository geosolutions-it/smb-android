package it.geosolutions.savemybike.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class Track {

    private long id;

    private String owner;

    private Double duration;

    private ArrayList<Segment> segments;

    @SerializedName("created_at")
    private String cretaedAt;

    @SerializedName("vehicle_types")
    private ArrayList<String> vehicleTypes;

    private HashMap<String, EmissionData> emissions;

    private HashMap<String, Cost> costs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

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

    public ArrayList<String> getVehicleTypes() {
        return vehicleTypes;
    }

    public void setVehicleTypes(ArrayList<String> vehicleTypes) {
        this.vehicleTypes = vehicleTypes;
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

    private HashMap<String, HealthData> health;




}
