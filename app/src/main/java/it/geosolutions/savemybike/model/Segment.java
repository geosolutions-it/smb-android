package it.geosolutions.savemybike.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Lorenzo Natali, GeoSolutions s.a.s.
 * Segment object from SMB REST API
 */
public class Segment {
    private int id;
    private String url;
    // geom	string

    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;

    @SerializedName("vehicle_type")
    private String veihicleType;


    private String geom;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getVeihicleType() {
        return veihicleType;
    }

    public void setVeihicleType(String veihicleType) {
        this.veihicleType = veihicleType;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    // ...url, track, vehicle_id, emissions, costs, health

}
