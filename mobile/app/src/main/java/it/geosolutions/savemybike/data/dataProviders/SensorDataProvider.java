package it.geosolutions.savemybike.data.dataProviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.data.service.SaveMyBikeService;


/**
 * Created by Robert Oehler on 08.11.17.
 *
 * Class to read sensor data of the device
 *
 */

public class SensorDataProvider implements SensorEventListener, IDataProvider {

    private final static String TAG = "SensorDataProvider";

    private SaveMyBikeService service;
    private SensorManager sensorManager;

    private long lastPressureDataTime = 0;
    private long lastTemperatureDataTime = 0;
    private long lastAccelerationDataTime = 0;
    private long lastMagFieldDataTime = 0;
    private long lastHumidityDataTime = 0;
    private long lastProximityDataTime = 0;
    private long lastLightDataTime = 0;

    private boolean isRegistered = false;

    private float[] mGravity;
    private float[] mGeomagnetic;

    public SensorDataProvider(final SaveMyBikeService saveMyBikeService){

        this.service = saveMyBikeService;

        sensorManager = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {

        startSensorListening();
    }

    @Override
    public void stop() {

        stopSensorListening();
    }

    /**
     * starts sensor listening with frequency SensorManager.SENSOR_DELAY_NORMAL
     * which should be a frequency of 200000 ns or 200 ms
     * source : http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.0.3_r1/android/hardware/SensorManager.java
     */
    private void startSensorListening(){


        if(!isRegistered) {
            ArrayList<Boolean> sensorRegistrationResults = new ArrayList<>();

            //1.pressure
            sensorRegistrationResults.add(registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), "pressure"));

            //2.temperature
            boolean hasAmbientTemperature = registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), "ambient temperature");
            if(hasAmbientTemperature){
                sensorRegistrationResults.add(hasAmbientTemperature);
            }else{
                sensorRegistrationResults.add(registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE), "non ambient temperature"));
            }

            //3.accelerometer
            sensorRegistrationResults.add( registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), "accelerometer"));

            //4.humidity
            sensorRegistrationResults.add(registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY), "humidity"));

            //5.proximity
            sensorRegistrationResults.add(registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), "proximity"));

            //6.light
            sensorRegistrationResults.add(registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), "light"));

            //7.magnetic field
            sensorRegistrationResults.add(registerSensor(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), "magnetic field"));

            //if only one registration result was true remember that we have to unregister
            for(Boolean result : sensorRegistrationResults){
                if(result){
                    isRegistered = true;
                    break;
                }
            }
        }
    }

    /**
     * registers if available a sensor and returns if this was possible
     * @param sensor the sensor to register or null if not available on the device
     * @param name the name for logging
     * @return true if the sensor was registered, false otherwise
     */
    private boolean registerSensor(final Sensor sensor, String name){

        if(sensor != null){
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            if(BuildConfig.DEBUG) {
                Log.i(TAG, name + " registered");
            }
            return true;
        }else{
            if(BuildConfig.DEBUG) {
                Log.i(TAG, name + " not available");
            }
            return false;
        }
    }
    private void stopSensorListening(){

        if(isRegistered) {
            sensorManager.unregisterListener(this);
            isRegistered = false;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {

            case Sensor.TYPE_PRESSURE:

                if (System.currentTimeMillis() - lastPressureDataTime >= DATA_INTERVAL) {
                    final float pressure = event.values[0];

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Barometer sensor event new pressure " + pressure);
                    }


                    if (service != null && service.getSessionLogic() != null && service.getSessionLogic().getSession() != null) {
                        service.getSessionLogic().getSession().getCurrentDataPoint().pressure = pressure;
                    }
                    lastPressureDataTime = System.currentTimeMillis();
                }
            break;
            case  Sensor.TYPE_AMBIENT_TEMPERATURE:
            case  Sensor.TYPE_TEMPERATURE:

                if(System.currentTimeMillis() - lastTemperatureDataTime >= DATA_INTERVAL) {

                    //values[0]: ambient (room) temperature in degree Celsius.
                    float temperature = event.values[0];

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format(Locale.US, "Temperature changed to %.2f Celsius", temperature));
                    }

                    if (service != null && service.getSessionLogic() != null && service.getSessionLogic().getSession() != null) {
                        service.getSessionLogic().getSession().getCurrentDataPoint().temperature = temperature;
                    }

                    lastTemperatureDataTime = System.currentTimeMillis();
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                //https://developer.android.com/reference/android/hardware/Sensor.html#TYPE_ACCELEROMETER
                if (System.currentTimeMillis() - lastAccelerationDataTime >= DATA_INTERVAL) {

                    mGravity = event.values;

                    if (service != null && service.getSessionLogic() != null && service.getSessionLogic().getSession() != null) {
                        service.getSessionLogic().getSession().getCurrentDataPoint().accelerationX = mGravity[0];
                        service.getSessionLogic().getSession().getCurrentDataPoint().accelerationY = mGravity[1];
                        service.getSessionLogic().getSession().getCurrentDataPoint().accelerationZ = mGravity[2];
                    }

                    lastAccelerationDataTime = System.currentTimeMillis();

                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (System.currentTimeMillis() - lastMagFieldDataTime >= DATA_INTERVAL) {

                    mGeomagnetic = event.values;
                    if (mGravity != null && mGeomagnetic != null) {

                        float[] results = calculateOrientation();

                        float bearing =  results[0];
                        float pitch   =  results[1];
                        float roll    = -results[2];

                        if (service != null && service.getSessionLogic() != null && service.getSessionLogic().getSession() != null) {
                            service.getSessionLogic().getSession().getCurrentDataPoint().deviceBearing = bearing;
                            service.getSessionLogic().getSession().getCurrentDataPoint().deviceRoll    = roll;
                            service.getSessionLogic().getSession().getCurrentDataPoint().devicePitch   = pitch;
                        }
                    }
                }
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:

                if(System.currentTimeMillis() - lastHumidityDataTime >= DATA_INTERVAL) {

                    //values[0]: Relative ambient air humidity in percent
                    float humidity = event.values[0];

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format(Locale.US, "Humidity changed to %.2f percent", humidity));
                    }

                    if (service != null && service.getSessionLogic() != null && service.getSessionLogic().getSession() != null) {
                        service.getSessionLogic().getSession().getCurrentDataPoint().humidity = humidity;
                    }

                    lastHumidityDataTime = System.currentTimeMillis();
                }
                break;
            case Sensor.TYPE_PROXIMITY:

                if(System.currentTimeMillis() - lastProximityDataTime >= DATA_INTERVAL) {

                    //values[0]: Proximity sensor distance measured in centimeters
                    float proximity = event.values[0];

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format(Locale.US, "proximity changed to %.2f", proximity));
                    }

                    if (service != null && service.getSessionLogic() != null && service.getSessionLogic().getSession() != null) {
                        service.getSessionLogic().getSession().getCurrentDataPoint().proximity = proximity;
                    }

                    lastProximityDataTime = System.currentTimeMillis();
                }
                break;
            case Sensor.TYPE_LIGHT:

                if(System.currentTimeMillis() - lastLightDataTime >= DATA_INTERVAL) {

                    //values[0]: Ambient light level in SI lux units
                    float light = event.values[0];

                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, String.format(Locale.US, "light changed to %.2f lux", light));
                    }

                    if (service != null && service.getSessionLogic() != null && service.getSessionLogic().getSession() != null) {
                        service.getSessionLogic().getSession().getCurrentDataPoint().lumen = light;
                    }

                    lastLightDataTime = System.currentTimeMillis();
                }
                break;
        }
    }

    private float[] calculateOrientation(){

        float[] values = new float[3];
        float[] R = new float[9];
        float[] outR = new float[9];

        SensorManager.getRotationMatrix(R, null, mGravity, mGeomagnetic);

        SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);

        SensorManager.getOrientation(outR, values);

        //convert from radiants to degrees
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        float a = values[0] % 360;
        float b = values[1];
        float c = values[2];

        values[0] = a >= 0 ? a : a + 360;
        values[1] = b > 180f ? 180f : b;
        values[1] = b < -180f ? -180f : b;
        values[2] = c > 180f ? 180f : c;
        values[2] = c < -180f ? -180f : c;

        return values;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "accuracy changed to " + accuracy);
        }
    }

    @Override
    public String getName() {
        return TAG;
    }
}
