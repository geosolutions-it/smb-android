package it.geosolutions.savemybike.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Lorenzo Natali, GeoSolutions s.a.s.
 * EmissionData object from SMB REST API
 */
public class EmissionData {
    private double co;
    private double co2;
    private double nox;
    private double so2;
    private double pm10;
    @SerializedName("co_saved")
    private double coSaved;

    @SerializedName("co2_saved")
    private double co2Saved;

    @SerializedName("nox_saved")
    private double noxSaved;

    @SerializedName("so2_saved")
    private double so2Saved;

    @SerializedName("pm10_saved")
    private double pm10Saved;

    public double getCo() {
        return co;
    }

    public void setCo(double co) {
        this.co = co;
    }

    public double getCo2() {
        return co2;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public double getNox() {
        return nox;
    }

    public void setNox(double nox) {
        this.nox = nox;
    }

    public double getSo2() {
        return so2;
    }

    public void setSo2(double so2) {
        this.so2 = so2;
    }

    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public double getCoSaved() {
        return coSaved;
    }

    public void setCoSaved(double coSaved) {
        this.coSaved = coSaved;
    }

    public double getCo2Saved() {
        return co2Saved;
    }

    public void setCo2Saved(double co2Saved) {
        this.co2Saved = co2Saved;
    }

    public double getNoxSaved() {
        return noxSaved;
    }

    public void setNoxSaved(double noxSaved) {
        this.noxSaved = noxSaved;
    }

    public double getSo2Saved() {
        return so2Saved;
    }

    public void setSo2Saved(double so2Saved) {
        this.so2Saved = so2Saved;
    }

    public double getPm10Saved() {
        return pm10Saved;
    }

    public void setPm10Saved(double pm10Saved) {
        this.pm10Saved = pm10Saved;
    }
}
