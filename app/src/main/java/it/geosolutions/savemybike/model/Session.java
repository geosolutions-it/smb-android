package it.geosolutions.savemybike.model;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import it.geosolutions.savemybike.data.Constants;

/**
 * Created by Robert Oehler on 26.10.17.
 *
 */

public class Session {

    public enum SessionState
    {
        ACTIVE,
        STOPPED
    }

    private SessionState state;
    private Bike bike;
    private Vehicle.VehicleType currentVehicleType;
    private DataPoint currentDataPoint;

    private long id;
    private long lastPersistedIndex;
    private boolean isUploaded;
    private String name;
    private String serverId;
    private String userId;

    private ArrayList<DataPoint> dataPoints;

    /**
     * reads the fields of this class which are of interest when exporting this session
     * //TODO this must be updated when the fields to upload change
     * @return the list of field names
     */
    public static ArrayList<String> getFieldNames(){

        ArrayList<String> fieldNames = new ArrayList<>();

        fieldNames.add("userId");
        fieldNames.add("name");

        return fieldNames;
    }

    /**
     * maps a fields value to a fields name
     * TODO this must be updated when other fields are added / renamed
     * @param field the name of the field
     * @param session the session containing the data
     * @return the values of the field as String
     */
    public static String getValueForFieldName(String field, Session session) {

        switch (field){
            case "userId":
                return session.userId;
            case "name":
                return session.name;
           default:
                return Integer.toString(0);
        }
    }

    public Session(Vehicle.VehicleType currentVehicleType){

        this.currentVehicleType = currentVehicleType;
        dataPoints = new ArrayList<>();
        state = SessionState.ACTIVE;
    }

    public Session(long id, Bike bike, String name, String userId, String sId, int state, boolean uploaded, int lastPersist) {
        this.id = id;
        this.bike = bike;
        this.name = name;
        this.userId = userId;
        this.serverId = sId;
        this.state = SessionState.values()[state];
        this.isUploaded = uploaded;
        this.lastPersistedIndex = lastPersist;
    }

    public DataPoint getCurrentDataPoint() {
        if(currentDataPoint == null){
            currentDataPoint = new DataPoint(this.id, System.currentTimeMillis(), this.currentVehicleType.ordinal());
        }
        return currentDataPoint;
    }
    public void deepCopyCurrentDataPoint(){

        DataPoint copy = new DataPoint(this.id, System.currentTimeMillis(), this.currentVehicleType.ordinal());
        copy.elevation = getCurrentDataPoint().elevation;
        copy.latitude = getCurrentDataPoint().latitude;
        copy.longitude = getCurrentDataPoint().longitude;
        copy.gps_bearing = getCurrentDataPoint().gps_bearing;
        copy.accuracy = getCurrentDataPoint().accuracy;
        copy.batConsumptionPerHour = getCurrentDataPoint().batConsumptionPerHour;
        copy.batteryLevel = getCurrentDataPoint().batteryLevel;
        copy.temperature = getCurrentDataPoint().temperature;
        copy.pressure = getCurrentDataPoint().pressure;
        copy.lumen = getCurrentDataPoint().lumen;
        copy.humidity = getCurrentDataPoint().humidity;
        copy.proximity = getCurrentDataPoint().proximity;
        copy.accelerationX = getCurrentDataPoint().accelerationX;
        copy.accelerationY = getCurrentDataPoint().accelerationY;
        copy.accelerationZ = getCurrentDataPoint().accelerationZ;
        copy.deviceBearing = getCurrentDataPoint().deviceBearing;
        copy.deviceRoll = getCurrentDataPoint().deviceRoll;
        copy.devicePitch = getCurrentDataPoint().devicePitch;

        this.currentDataPoint = copy;
    }

    /**
     * calculates the distance of this session
     * by summing up the single distances between the dataPoints
     * @return the distance
     */
    @NonNull
    public double getDistance() {

        double dist = 0;

        if(dataPoints != null && dataPoints.size() > 1){
            for(int i = 1; i < dataPoints.size(); i++){

                //are both locations valid ?
                boolean bothValid = isValidLat(dataPoints.get(i - 1).latitude) &&
                                    isValidLon(dataPoints.get(i - 1).longitude) &&
                                    isValidLat(dataPoints.get(i).latitude) &&
                                    isValidLon(dataPoints.get(i).longitude);
                if(!bothValid){
                    continue;
                }

                Location from = new Location("from");
                from.setLatitude(dataPoints.get(i - 1).latitude);
                from.setLongitude(dataPoints.get(i - 1).longitude);

                Location to   = new Location("to");
                to.setLatitude(dataPoints.get(i).latitude);
                to.setLongitude(dataPoints.get(i).longitude);

                dist += from.distanceTo(to);
            }
        }

        return dist;
    }

    private boolean isValidLat(double d){

        return  d != Double.POSITIVE_INFINITY &&
                d != Double.NEGATIVE_INFINITY &&
                d != Double.MAX_VALUE &&
                d != Double.MIN_VALUE &&
                d >= Constants.MIN_LATITUDE &&
                d <= Constants.MAX_LATITUDE &&
                d != Double.NaN;
    }

    private boolean isValidLon(double d){

        return  d != Double.POSITIVE_INFINITY &&
                d != Double.NEGATIVE_INFINITY &&
                d != Double.MAX_VALUE &&
                d != Double.MIN_VALUE &&
                d >= Constants.MIN_LONGITUDE &&
                d <= Constants.MAX_LONGITUDE &&
                d != Double.NaN;
    }

    /**
     * calculates the duration of this session
     *
     * when start and end time are valid the difference between these is returned
     * otherwise the diff between the first and the last dataPoint
     *
     * @return the overall time of this session
     */
    public long getOverallTime(){

        long time = 0;
        if (dataPoints != null && dataPoints.size() > 1) {

            long first = dataPoints.get(0).timeStamp;
            long last = dataPoints.get(dataPoints.size() - 1).timeStamp;
            long diff = last - first;
            if(diff > 0) {
                time += diff;
            }
        }
        return time;
    }

    /**
     * calculates the elevation of this session
     * @return the sum of all (positive) elevation changes
     */
    public double getOverallElevation() {

        double elev = 0;

        if(dataPoints != null && dataPoints.size() > 1){
            for(int i = 1; i < dataPoints.size(); i++){

               double from = dataPoints.get(i - 1).elevation;
               double to   = dataPoints.get(i).elevation;

                double diff = to - from;
                //only count positive elevation
                if(diff > 0) {
                    elev += diff;
                }
            }
        }

        return elev;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public ArrayList<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(ArrayList<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUserId() {
        return userId;
    }

    public long getLastPersistedIndex() {
        return lastPersistedIndex;
    }

    public void setLastPersistedIndex(long lastPersistedIndex) {
        this.lastPersistedIndex = lastPersistedIndex;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.isUploaded = uploaded;
    }

    public void setCurrentVehicleType(Vehicle.VehicleType currentVehicleType) {
        this.currentVehicleType = currentVehicleType;
    }

    /**
     * Return the first point timeStamp
     *
     * @return the overall time of this session
     */
    public long getStartingTime(){

        if (dataPoints != null && dataPoints.size() > 0) {
            return dataPoints.get(0).timeStamp;
        }
        return 0;
    }

}
