package it.geosolutions.savemybike.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Lorenzo Pini on 05/07/2018.
 */
public class CurrentStatus {

    @SerializedName("lost")
    @Expose
    private Boolean lost;
    @SerializedName("url")
    @Expose
    private String url;

    public Boolean getLost() {
        return lost;
    }

    public void setLost(Boolean lost) {
        this.lost = lost;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBike() {
        return bike;
    }

    public void setBike(String bike) {
        this.bike = bike;
    }

    /**
     * This is the bike url, needed to upload a new bike status
     */
    @SerializedName("bike")
    @Expose
    private String bike;

}