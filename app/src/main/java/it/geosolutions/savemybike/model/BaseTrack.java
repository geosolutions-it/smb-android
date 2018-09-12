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

    @SerializedName("duration_minutes")
    protected Double duration;

    @SerializedName("created_at")
    protected String cretaedAt;

    @SerializedName("vehicle_types")
    protected ArrayList<String> vehicleTypes;

    @SerializedName("length_meters")
    protected Double length;

    @SerializedName("start_date")
    protected String startDate;

    @SerializedName("end_date")
    protected String endDate;

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

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getVehicleTypes() {
        return vehicleTypes;
    }

    public void setVehicleTypes(ArrayList<String> vehicleTypes) {
        this.vehicleTypes = vehicleTypes;
    }

}
