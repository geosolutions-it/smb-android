package it.geosolutions.savemybike.data.dataProviders;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.data.session.SessionLogic;
import it.geosolutions.savemybike.model.Vehicle;


/**
 * Created by Robert Oehler on 08.11.17.
 *
 * A class to receive GPS updates
 */

public class GPSProvider implements IDataProvider, LocationListener {

    private final static String TAG = "LocationManagerGPS";

    private LocationManager locationManager;

    private SessionLogic sessionLogic;
    private Vehicle vehicle;

    private boolean stopped;
    private String bestProvider;

    public GPSProvider(final Context context, final Vehicle vehicle, final SessionLogic sessionLogic) {

        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.sessionLogic = sessionLogic;
        this.vehicle = vehicle;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        bestProvider = locationManager.getBestProvider(criteria, true);
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void start() {

        this.stopped = false;

        if(bestProvider == null){
            bestProvider = LocationManager.GPS_PROVIDER;
        }

        this.locationManager.requestLocationUpdates(bestProvider, this.vehicle.getMinimumGPSTime(), this.vehicle.getMinimumGPSDistance(), this);
    }


    @Override
    public void stop() {

        this.stopped = true;
        this.locationManager.removeUpdates(this);
    }

    /**
     * switches the currently used vehicle
     * when @param newVehicle has different GPS params the locationManager is stopped and restarted
     * @param newVehicle the vehicle to apply
     */
    @SuppressWarnings( {"MissingPermission"})
    public void switchToVehicle(Vehicle newVehicle){

        //has the new vehicle different params ?
        if(newVehicle.getMinimumGPSDistance() != vehicle.getMinimumGPSDistance() || newVehicle.getMinimumGPSTime() != vehicle.getMinimumGPSTime()){

            //yes, a restart necessary
            this.locationManager.removeUpdates(this);
            //TODO can this be done immediately or delay call somewhat ?
            this.locationManager.requestLocationUpdates(bestProvider, newVehicle.getMinimumGPSTime(), newVehicle.getMinimumGPSDistance(), this);
        }


        this.vehicle = newVehicle;
    }

    @Override
    public void onLocationChanged(Location location) {

        if(stopped){
            Log.w(TAG, "Stopped");
            return;
        }

        if(BuildConfig.DEBUG){
            Log.i(TAG,"OnLocationChanged "+location.getProvider()+" "+location.getAccuracy() + " "+ location.getSpeed() + " m/s");
        }

        sessionLogic.evaluateNewLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) { }
    @Override
    public void onProviderEnabled(String provider) { }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public String getName() {
        return TAG;
    }
}
