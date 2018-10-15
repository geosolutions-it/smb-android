package it.geosolutions.savemybike.data.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.dataProviders.BatteryInfo;
import it.geosolutions.savemybike.data.dataProviders.GPSProvider;
import it.geosolutions.savemybike.data.dataProviders.GPSSimulator;
import it.geosolutions.savemybike.data.dataProviders.IDataProvider;
import it.geosolutions.savemybike.data.dataProviders.SensorDataProvider;
import it.geosolutions.savemybike.data.db.SMBDatabase;
import it.geosolutions.savemybike.data.session.SessionLogic;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;

/**
 * Created by Robert Oehler on 28.10.17.
 *
 * A service which manages the recording of sessions
 * It is loosely bound to the UI - activity
 *
 * It starts and stop dataProviders - these provide data to the
 * @link SessionLogic which collects the data
 *
 * During a session a notification is shown to remind the user that an ongoing
 * GPS connection is active
 */

public class SaveMyBikeService extends Service {

    private static final String TAG = "SaveMyBikeService";

    public static final String PARAM_SIMULATE     = "service.param.simulate";
    public static final String PARAM_CONTINUE_ID  = "service.param.continue.id";
    public static final String PARAM_VEHICLE      = "service.param.vehicle";
    public static final String PARAM_CONFIG       = "service.param.config";

    private ArrayList<IDataProvider> dataProviders = new ArrayList<>();
    private SessionLogic sessionLogic;
    private Handler handler;
    private final IBinder mBinder = new SaveMyBikeBinder();
    private Configuration config;
    private NotificationManager notificationManager;

    private boolean didStop = false;

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate");
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(BuildConfig.DEBUG) {
            Log.d(TAG, "onStartCommand, startId "+ startId);
        }

        //1.parse params
        boolean simulate = false;
        long continueId = -1;
        Vehicle vehicle = null;

        if(intent != null){
            simulate   =  intent.getBooleanExtra(PARAM_SIMULATE, false);
            continueId = intent.getLongExtra(PARAM_CONTINUE_ID, -1);
            vehicle    = (Vehicle) intent.getSerializableExtra(PARAM_VEHICLE);
            config     = (Configuration) intent.getSerializableExtra(PARAM_CONFIG);
        }

        //2. create or continue a session
        Session session = null;
        if(continueId != -1){
            session = continueSession(continueId, vehicle);
            if(BuildConfig.DEBUG){
                Log.d(TAG, "continuing session "+ continueId);
            }
        }else{
            session = createSession(vehicle);
            if(BuildConfig.DEBUG){
                Log.d(TAG, "starting a new session "+ session.getId());
            }
        }

        //3.session logic
        sessionLogic = new SessionLogic(getBaseContext(), session, vehicle, config);
        sessionLogic.setSimulating(simulate);
        getDataProviders().add(sessionLogic);

        //create data providers:
        //4.a GPS
        if(simulate){

            //in a simulation use the simulator to create GPS locations
            final GPSSimulator gpsSimulator = new GPSSimulator( sessionLogic);
            getDataProviders().add(gpsSimulator);
        }else{

            //otherwise use the real GPS
            final GPSProvider gpsProvider = new GPSProvider(getBaseContext(), vehicle, sessionLogic);
            getDataProviders().add(gpsProvider);
        }

        //4.b Sensors
        getDataProviders().add(new SensorDataProvider(this));
        //4.c Battery
        getDataProviders().add(new BatteryInfo(this));

        //5.start all data providers
        for(IDataProvider provider : dataProviders){

            provider.start();
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "Starting data provider " + provider.getName());
            }
        }

        //6.finally, show a notification



        startForeground(1,  getNotificationManager().startNotification(getResources().getString(R.string.state_started), vehicle));
        return START_STICKY;
    }


    /**
     * updates the currently used vehicle :
     * 1. the GPS provider will restart with likely different parameters (if necessary)
     * 2. the session logic is updated to use the new vehicle
     * 3. the notification is updated
     * @param newVehicle the new vehicle
     */
    public void vehicleChanged(Vehicle newVehicle) {

        if(dataProviders != null){
            for(IDataProvider provider : dataProviders){
                if(provider instanceof GPSProvider){
                    ((GPSProvider) provider).switchToVehicle(newVehicle);
                }
            }
        }
        if(sessionLogic != null){
            sessionLogic.setVehicle(newVehicle);
        }

        getNotificationManager().updateNotification(getResources().getString(R.string.state_started), newVehicle);
    }

    public Vehicle vehicleFromType(int newVehicleType) {

        for (Vehicle vehicle : config.getVehicles()) {
            if (vehicle.getType().ordinal() == newVehicleType) {
                return vehicle;
            }
        }
        return null;
    }

    /**
     * stops this session
     * SessionLogic and providers are stopped
     * the state of this ride is set to FINISHED
     * and persisted
     */
    public void stopSession() {

        if(BuildConfig.DEBUG) {
            Log.d(TAG, "Stopping ride");
        }

        this.didStop = true;

        sessionLogic.stop();
        sessionLogic.getSession().setState(Session.SessionState.STOPPED);

        //synchronize ride
        sessionLogic.persistSession();

        for(IDataProvider provider : dataProviders){
            provider.stop();
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "Stopping data provider " + provider.getName());
            }
        }

        this.stopSelf();
    }
    /**
     * cleans up this service
     *
     * 1. the ride is persisted
     * 2. a notification that the ride is stopped shown - it autocancels after 1 sec
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        //if the service gets killed and we did not stop - try to persist
        if(!didStop && sessionLogic != null) {
            sessionLogic.persistSession();
        }

        stopForeground(true);

        getNotificationManager().stopNotification();
    }


    /**
     * continues a session if the provided @param sessionId is available in the database
     * otherwise a new session is created
     * @param sessionId the id of the session to continue
     * @param vehicle the current vehicle
     * @return the continued or the newly created session
     */
    private Session continueSession(long sessionId, final Vehicle vehicle) {

        Session session = null;
        //check database for id and reload it
        final SMBDatabase smbDatabase = new SMBDatabase(getBaseContext());
        try{
            smbDatabase.open();

            session = smbDatabase.getSession(sessionId);

            if(session == null){
                //this session was not found - create a new one
                session = createSession(vehicle);
            }

        } finally {
            smbDatabase.close();
        }

        return session;
    }

    /**
     * creates a new session and inserts it into the database
     * @param vehicle the current vehicle
     * @return the created session
     */
    private Session createSession(final Vehicle vehicle) {

        final Session session = new Session(vehicle.getType());
        //insert to database
        final SMBDatabase smbDatabase = new SMBDatabase(getBaseContext());
        try{
            smbDatabase.open();
            long id = smbDatabase.insertSession(session, false);
            session.setId(id);

        } finally {
            smbDatabase.close();
        }

        return session;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    public class SaveMyBikeBinder extends Binder {

        public SaveMyBikeService getService() {
            return SaveMyBikeService.this;
        }
    }

    public SessionLogic getSessionLogic() {
        return sessionLogic;
    }

    public Vehicle getCurrentVehicle(){

        return sessionLogic != null ? sessionLogic.getVehicle() : null;
    }

    public ArrayList<IDataProvider> getDataProviders() {

        if(dataProviders == null){
            dataProviders = new ArrayList<>();
        }

        return dataProviders;
    }

    public NotificationManager getNotificationManager() {

        if(notificationManager == null){
            notificationManager = new NotificationManager(this);
        }

        return notificationManager;
    }

    private void publishProgress() {
        Log.v(TAG, "reporting back from the Service Thread");
        String text = "PROVA";
        Bundle msgBundle = new Bundle();
        msgBundle.putString("result", text);
        Message msg = Message.obtain();
        msg.setData(msgBundle);
        handler.sendMessage(msg);
    }
}
