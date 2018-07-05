package it.geosolutions.savemybike.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Robert Oehler on 26.10.17.
 *
 */

public class Bike implements Serializable {

    private final static String DEFAULT_BIKE_NAME = "My Bike";

    private long localId;
    private String name;

    @SerializedName("id")
    private String remoteId;
    @SerializedName("image")
    private String imagePath;
    @SerializedName("state")
    private int stolen;
    private int selected;

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    private List<String> pictures;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String nickname;

    @SerializedName("current_status")
    @Expose
    private CurrentStatus currentStatus;
    /**
     * static constructor for a default bike, selected, non stolen
     */
    public static Bike createDefaultBike() {

        return new Bike(
                -1,
                DEFAULT_BIKE_NAME,
                null,
                1,
                0);
    }

    public Bike(long id, String name) {
        this.localId = id;
        this.name = name;
    }

    public Bike(int id, String name, String uri, int selected, int stolen) {
        this.localId = id;
        this.name = name;
        this.imagePath = uri;
        this.selected = selected;
        this.stolen = stolen;
    }

    public String getName() {
        return name;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long id) {
        this.localId = id;
    }

    public boolean isStolen() {
        return stolen > 0;
    }

    public void setStolen(boolean stolen) {
        this.stolen = stolen ? 1 : 0;
    }

    public boolean isSelected() {
        return selected > 0;
    }

    public void setSelected(boolean selected) {
        this.selected = selected ? 1 : 0;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
}
