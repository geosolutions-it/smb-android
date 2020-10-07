package it.geosolutions.savemybike.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class Observation {
    private String id;
    private String observedAt;
    private String address;
    private String reporter_type;
    private String reporter_name;
    private String position;
    private String details;
    private String bike;
    private String reporter_id;

    public Observation(JSONObject obj) {
        try {
            id = obj.getString("id");
            JSONObject properties = obj.getJSONObject("properties");
            if (properties != null) {
                observedAt = properties.getString("observed_at");
                address = properties.getString("address");
            }
        } catch (JSONException e) {
            Log.e("Observation", "can not parse json object for observation");
        }
    }

    public Observation (String bikeUUID, String reporter_type, String details, String reporter_id){
        this.bike=bikeUUID;
        this.reporter_type=reporter_type;
        this.details=details;
        this.reporter_id=reporter_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(String observedAt) {
        this.observedAt = observedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReporter_type() {
        return reporter_type;
    }

    public void setReporter_type(String reporter_type) {
        this.reporter_type = reporter_type;
    }

    public String getReporter_name() {
        return reporter_name;
    }

    public void setReporter_name(String reporter_name) {
        this.reporter_name = reporter_name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBike() {
        return bike;
    }

    public void setBike(String bike) {
        this.bike = bike;
    }

    public String getReporter_id() {
        return reporter_id;
    }

    public void setReporter_id(String reporter_id) {
        this.reporter_id = reporter_id;
    }
}
