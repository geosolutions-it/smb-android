package it.geosolutions.savemybike.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Lorenzo Natali, GeoSolutions s.a.s.
 * BaseTrack common data object from SMB REST API
 */
public class BaseTrack {

    protected long id;

    protected String owner;

    protected Double duration;

    @SerializedName("created_at")
    protected String cretaedAt;

    @SerializedName("vehicle_types")
    protected ArrayList<String> vehicleTypes;

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

}
