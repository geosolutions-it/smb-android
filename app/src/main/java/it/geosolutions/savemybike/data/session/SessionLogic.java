package it.geosolutions.savemybike.data.session;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.dataProviders.IDataProvider;
import it.geosolutions.savemybike.data.db.SMBDatabase;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.DataPoint;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;

/**
 * Created by Robert Oehler on 30.10.17.
 *
 * Class which collects the data during a session and
 * periodically {@link Constants#DEFAULT_DATA_READ_INTERVAL} adds
 * dataPoints of the current data situation to a list
 *
 * This list is periodically {@link Constants#DEFAULT_PERSISTENCE_INTERVAL}
 * persisted to the database
 */

public class SessionLogic implements IDataProvider {

    private final static String TAG = "SessionLogic";

    private Context context;
    private Vehicle vehicle;
    private Session session;
    private Handler handler;

    //configurable
    private int persistanceInterval;
    private int dataReadInterval;

    //temporay
    private boolean stopped = false;
    private boolean hasGPSFix = false;
    private boolean isSimulating;
    private long lastSessionPersistTime;
    private String databaseName;
    private boolean scheduledPersist;

    public SessionLogic(Context context, Session session, Vehicle vehicle, Configuration configuration) {

        this.context = context;
        this.session = session;
        this.vehicle = vehicle;

        if(configuration != null && configuration.dataReadInterval != 0 && configuration.persistanceInterval != 0){
            this.dataReadInterval = configuration.dataReadInterval;
            this.persistanceInterval = configuration.persistanceInterval;
        }else{
            this.dataReadInterval = Constants.DEFAULT_DATA_READ_INTERVAL;
            this.persistanceInterval = Constants.DEFAULT_PERSISTENCE_INTERVAL;
        }
    }

    public void start(){

        this.stopped = false;
        startTasks();
    }
    public void stop(){

        this.stopped = true;
        this.hasGPSFix = false;

        stopTasks();
    }

    /**
     * starts tasks
     */
    private void startTasks() {

        getHandler().removeCallbacks(getDataReadTask());
        getHandler().postDelayed(getDataReadTask(), dataReadInterval);

        getHandler().removeCallbacks(getPersistanceTask());
        getHandler().postDelayed(getPersistanceTask(), persistanceInterval);
    }

    /**
     * stops the tasks
     */
    private void stopTasks() {

        getHandler().removeCallbacks(getDataReadTask());
        getHandler().removeCallbacks(getPersistanceTask());
    }

    /**
     * evaluates a new location received from GPS:
     *
     * when not having a GPX fix yet register that the fix was acquired
     * the location is then used to update the current session data
     *
     * Currently there is NO filtering applied, all locations are registered
     *
     * @param newLocation the newly acquired location
     */
    public void evaluateNewLocation(Location newLocation){

        if(stopped){
            return;
        }

        if(session == null){
            Log.w(TAG, "session null");
            return;
        }

        if(!hasGPSFix){
            session.setState(Session.SessionState.ACTIVE);
            hasGPSFix = true;
        }

        //update session with the current data from the location

        session.getCurrentDataPoint().vehicleMode = vehicle.getType().ordinal();
        session.getCurrentDataPoint().timeStamp   = newLocation.getTime();
        session.getCurrentDataPoint().latitude    = newLocation.getLatitude();
        session.getCurrentDataPoint().longitude   = newLocation.getLongitude();
        session.getCurrentDataPoint().elevation   = newLocation.getAltitude();
        session.getCurrentDataPoint().accuracy    = newLocation.getAccuracy();
        session.getCurrentDataPoint().gps_bearing = newLocation.getBearing();
        session.getCurrentDataPoint().speed       = newLocation.getSpeed();

    }

    private Runnable persistanceTask;
    private Runnable dataReadTask;

    /**
     * a task which adds the current dataPoint to the list
     * of dataPoints to persist
     * the current point is then deep copied to prepare
     * the next dataPoint with the current data as base
     */
    private Runnable getDataReadTask (){
        if(dataReadTask == null){
            dataReadTask = new Runnable() {
                @Override
                public void run() {

                    if(stopped){
                        return;
                    }

                    //add a new data point to the list of data-points

                    DataPoint newDataPoint = session.getCurrentDataPoint();

                    // Do not add datapoints without coordinates
                    if( !Double.isNaN(newDataPoint.latitude)){
                        session.getDataPoints().add(newDataPoint);
//                        if(BuildConfig.DEBUG){
//                            Log.i(TAG, String.format(Locale.US,"did add data point %d vehicle %d lat %.4f lon %.4f", session.getDataPoints().size(), newDataPoint.vehicleMode, newDataPoint.latitude, newDataPoint.longitude));
//                        }
                    }

                    session.deepCopyCurrentDataPoint();

                    getHandler().postDelayed(this, dataReadInterval);
                }
            };
        }
        return dataReadTask;
    }

    /**
     * a task which persists the current session
     */
    private Runnable getPersistanceTask() {

        if(persistanceTask == null){
            persistanceTask = new TimerTask() {
                @Override
                public void run() {

                    //15 sec interval for persistance event
                    if (System.currentTimeMillis() - lastSessionPersistTime >= persistanceInterval && session.getDataPoints().size() > 0) {

                        persistSession();

                    }else{

                        getHandler().postDelayed(this, persistanceInterval);
                    }
                }
            };
        }
        return persistanceTask;
    }

    /**
     * persist the current session to the local database
     */
    public void persistSession() {

        if(session == null){

            Log.w(TAG, "no session available, cannot persist to db");
            getHandler().postDelayed(persistanceTask, persistanceInterval);
            return;
        }

        new PersistTask(context, databaseName, new PersistTaskListener() {
            @Override
            public void done(boolean success) {

                if(success) {
                    lastSessionPersistTime = System.currentTimeMillis();
                }
                if(!stopped) {
                    scheduledPersist = true;
                    getHandler().postDelayed(persistanceTask, persistanceInterval);
                } else {
                    scheduledPersist =false;
                }
            }
        }).execute(session);
    }

    /**
     * task to persist the current session state to the database
     */
    private static class PersistTask extends AsyncTask<Session, Void, Boolean>{

        private WeakReference<Context> contextRef;
        private String databaseName;
        private PersistTaskListener listener;

        public PersistTask(Context context, String databaseName, PersistTaskListener listener) {
            this.contextRef = new WeakReference<>(context);
            this.databaseName = databaseName;
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Session... sessions) {

            final Session session = sessions[0];

            SMBDatabase database = databaseName == null ? new SMBDatabase(contextRef.get()) : new SMBDatabase(contextRef.get(), databaseName);

            if(database.open()){

                if (session.getId() <= 0) {
                    //this was never inserted
                    long id = database.insertSession(session, false);
                    session.setId(id);
                } else {
                    database.insertSession(session, true);
                }

                //persist dataPoints
                if (session.getDataPoints() != null) {

                    if(session.getDataPoints().size() > session.getLastPersistedIndex()) {
                        for (int i = (int) session.getLastPersistedIndex(); i < session.getDataPoints().size(); i++) {

                            final DataPoint dataPoint = session.getDataPoints().get(i);
                            if (dataPoint.sessionId <= 0) {
                                dataPoint.sessionId = session.getId();
                            }
                            long inserted_row_index = database.insertDataPoint(dataPoint);
                            Log.d(TAG, "Inserted row:" + inserted_row_index);
                        }
                        session.setLastPersistedIndex(session.getDataPoints().size());
                        database.insertSession(session, true);
                        if(BuildConfig.DEBUG) {
                            Log.d(TAG, "DB : persisted to " + session.getLastPersistedIndex());
                        }
                    }
                } else {
                    Log.i(TAG, "dataPoints null");
                }

                database.close();
                return true;
            }else{
                Log.w(TAG, "could not open db");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if(listener != null){
                listener.done(success);
            }
        }
    }
    interface PersistTaskListener
    {
        void done(boolean success);
    }

    public Handler getHandler() {
        if(handler == null){
            handler = new Handler();
        }
        return handler;
    }

    public Session getSession() {
        return session;
    }

    public void setSimulating(boolean simulating) {
        isSimulating = simulating;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.session.setCurrentVehicleType(vehicle.getType());
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setTestHandler(){

        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public String getName() {
        return "SessionLogic";
    }

    public boolean isScheduledPersist() {
        return scheduledPersist;
    }
}
