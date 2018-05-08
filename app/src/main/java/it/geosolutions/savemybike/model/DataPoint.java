package it.geosolutions.savemybike.model;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;

import it.geosolutions.savemybike.BuildConfig;

/**
 * Created by Robert Oehler on 26.10.17.
 *
 */

public class DataPoint {

    public long sessionId;
    public int vehicleMode;

    public long timeStamp;

    //GPS
    public double latitude = Double.NaN;
    public double longitude = Double.NaN;
    /**
     * height above the WGS84 ellipsoid in metres
     */
    public double elevation;
    public float accuracy;
    public float gps_bearing;
    public float speed;

    //sensors
    public int batteryLevel;
    public float batConsumptionPerHour;
    public float accelerationX;
    public float accelerationY;
    public float accelerationZ;
    public float humidity;
    public float proximity;
    public float lumen;
    public float deviceBearing;
    public float deviceRoll;
    public float devicePitch;

    /**
     * temperature in celsius
     */
    public float temperature;
    public float pressure;

    /**
     * reads the fields of this class via reflection
     * @return the list of field names of this class
     */
    public static ArrayList<String> getFieldNames(){

        ArrayList<String> fieldNames = new ArrayList<>();
        Field[] allFields = DataPoint.class.getDeclaredFields();

        for (Field field : allFields) {
            // Skip generated fields
            if (!field.isSynthetic()){
                if(BuildConfig.DEBUG) {
                    Log.i("DataPoint", "field " + field.getName());
                }
                fieldNames.add(field.getName());
            }
        }

        return fieldNames;
    }

    /**
     * maps a fields value to a fields name
     * TODO this must be updated when the fields are added / renamed
     * @param field the name of the field
     * @param dataPoint the dataPoint containing the data
     * @return the values of the field as String
     */
    public static String getValueForFieldName(String field, DataPoint dataPoint) {

        switch (field){
            case "accelerationX":
                return Float.toString(dataPoint.accelerationX);
            case "accelerationY":
                return Float.toString(dataPoint.accelerationY);
            case "accelerationZ":
                return Float.toString(dataPoint.accelerationZ);
            case "accuracy":
                return Float.toString(dataPoint.accuracy);
            case "batConsumptionPerHour":
                return Float.toString(dataPoint.batConsumptionPerHour);
            case "batteryLevel":
                return Float.toString(dataPoint.batteryLevel);
            case "deviceBearing":
                return Float.toString(dataPoint.deviceBearing);
            case "devicePitch":
                return Float.toString(dataPoint.devicePitch);
            case "deviceRoll":
                return Float.toString(dataPoint.deviceRoll);
            case "elevation":
                return Double.toString(dataPoint.elevation);
            case "gps_bearing":
                return Float.toString(dataPoint.gps_bearing);
            case "humidity":
                return Float.toString(dataPoint.humidity);
            case "latitude":
                return Double.toString(dataPoint.latitude);
            case "longitude":
                return Double.toString(dataPoint.longitude);
            case "lumen":
                return Float.toString(dataPoint.lumen);
            case "pressure":
                return Float.toString(dataPoint.pressure);
            case "proximity":
                return Float.toString(dataPoint.proximity);
            case "sessionId":
                return Long.toString(dataPoint.sessionId);
            case "speed":
                return Float.toString(dataPoint.speed);
            case "temperature":
                return Float.toString(dataPoint.temperature);
            case "timeStamp":
                return Long.toString(dataPoint.timeStamp);
            case "vehicleMode":
                return Integer.toString(dataPoint.vehicleMode);
            case "$change":
                return Integer.toString(0);
            case "serialVersionUID":
                return Integer.toString(0);
            default:
                return Integer.toString(0);
        }
    }

    public DataPoint(long sessionId, long time,  int vehicleMode) {

        this.sessionId = sessionId;
        this.timeStamp = time;
        this.vehicleMode = vehicleMode;
    }

    public DataPoint(long sessionId, long time, int vehicleMode, double lat, double lon){
        this(sessionId, time, vehicleMode);

        this.latitude  = lat;
        this.longitude = lon;
    }

    public DataPoint(long id,
                     int vehicle,
                     double lat,
                     double lon,
                     long time,
                     double elev,
                     float bear,
                     float accu,
                     float spd,
                     float press,
                     int bat_l,
                     float bat_c,
                     float accX,
                     float accY,
                     float accZ,
                     float hum,
                     float prx,
                     float lgt,
                     float deviceBearing,
                     float deviceRoll,
                     float devicePitch,
                     float temp) {

        this.sessionId = id;
        this.vehicleMode = vehicle;
        this.latitude = lat;
        this.longitude = lon;
        this.timeStamp = time;
        this.elevation = elev;
        this.gps_bearing = bear;
        this.accuracy = accu;
        this.speed = spd;
        this.pressure = press;
        this.batteryLevel = bat_l;
        this.batConsumptionPerHour = bat_c;
        this.accelerationX = accX;
        this.accelerationY = accY;
        this.accelerationZ = accZ;
        this.humidity = hum;
        this.proximity = prx;
        this.lumen = lgt;
        this.deviceBearing = deviceBearing;
        this.deviceRoll = deviceRoll;
        this.devicePitch = devicePitch;
        this.temperature = temp;

    }
}
