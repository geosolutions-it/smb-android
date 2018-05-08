package it.geosolutions.savemybike.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Robert Oehler on 26.10.17.
 *
 */

public class Vehicle implements Serializable {

    public enum VehicleType
    {
        FOOT,
        BIKE,
        BUS,
        CAR,
        MOPED,
        TRAIN
    }

    @SerializedName("id")
    private int vehicleType;
    @SerializedName("gpsTime")
    private int minimumGPSTime;
    @SerializedName("gpsDist")
    private int minimumGPSDistance;

    private boolean selected;

    public Vehicle(VehicleType mType, int minimumGPSTime, int minimumGPSDistance) {
        this.vehicleType = mType.ordinal();
        this.minimumGPSTime = minimumGPSTime;
        this.minimumGPSDistance = minimumGPSDistance;
    }

    public VehicleType getType() {
        return VehicleType.values()[vehicleType];
    }

    public int getMinimumGPSTime() {
        return minimumGPSTime;
    }

    public int getMinimumGPSDistance() {
        return minimumGPSDistance;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
