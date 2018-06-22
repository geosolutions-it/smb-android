package it.geosolutions.savemybike.data.dataProviders;

import android.location.Location;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Random;

import it.geosolutions.savemybike.data.service.SaveMyBikeService;
import it.geosolutions.savemybike.data.session.SessionLogic;
import it.geosolutions.savemybike.model.DataPoint;

/**
 * Created by Robert Oehler on 05.05.17.
 *
 * A class to simulate GPS updates
 */

public class GPSSimulator implements IDataProvider {

    private Handler handler;

    public enum SimulationMode{

       TEST_RIDE,
       LIMITLESS
    }

    private SimulationMode currentMode = SimulationMode.LIMITLESS;

    /**
     * speed
     */
    private static int SIMULATION_INTERVAL = 800;
    private static final int START_THRESHOLD = 2000;

    private static final float SIM_MOVE_SPEED_IN_MS = 6f;
    private static final float SIM_ACCURACY = 15f;

    private SessionLogic sessionLogic;
    private int currentSimulationIndex;
    private boolean cancelled = false;

    private ArrayList<DataPoint> locations;

    public GPSSimulator(final SessionLogic pSessionLogic){

        this.sessionLogic = pSessionLogic;
        this.sessionLogic.setSimulating(true);
        
    }

    /**
     * runnable which is executed during simulation - until all available locations are passed
     */
    private Runnable locSimulator = new Runnable() {

        public void run() {

            if(currentSimulationIndex < locations.size()){

                Location loc = new Location("");

                final DataPoint dataPoint = locations.get(currentSimulationIndex);

                loc.setLatitude(dataPoint.latitude);
                loc.setLongitude(dataPoint.longitude);
                loc.setAltitude(dataPoint.elevation);
                loc.setAccuracy(SIM_ACCURACY);
                loc.setSpeed(SIM_MOVE_SPEED_IN_MS);
                loc.setTime(System.currentTimeMillis());

                sessionLogic.evaluateNewLocation(loc);

                currentSimulationIndex++;

                if(!cancelled) {
                    getHandler().postDelayed(this, SIMULATION_INTERVAL);
                }
            }
        }
    };

    @Override
    public void start() {

        switch (currentMode) {
            case TEST_RIDE:
                locations = getTestLocations(sessionLogic);
                break;
            case LIMITLESS:

                locations = new ArrayList<>();
                Random rnd = new Random();

                double lat = 43.706763 + rnd.nextInt(2861)*0.00001;
                double lon = 10.388031 + rnd.nextInt(3915)*0.00001;
                int dx = ((rnd.nextInt(2) - 1)<0 ) ? -1 : 1;
                int dy = ((rnd.nextInt(2) - 1)<0 ) ? -1 : 1;

                double xoffset = (rnd.nextInt(5) + 1) * 0.0001d;
                double yoffset = (rnd.nextInt(5) + 1) * 0.0001d;

                xoffset *= dx;
                yoffset *= dy;

                for(int i = 0; i < 10000; i++){

                    lat += yoffset;
                    lon += xoffset;

                    locations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(), lat, lon));
                }

                break;
        }

        getHandler().postDelayed(locSimulator, GPSSimulator.START_THRESHOLD);
        
    }

    @Override
    public void stop() {

        cancelled = true;
    }
    
    public static ArrayList<DataPoint> getTestLocations(SessionLogic sessionLogic){

        ArrayList<DataPoint> debugLocations = new ArrayList<>();

        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.725398,10.397701));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.725862,10.397867));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.726251,10.398002));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.726440,10.398068));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.726871,10.398088));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.726855,10.397500));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.727026,10.396843));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.727274,10.394311));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.727414,10.393290));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.727514,10.392839));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.727561,10.392646));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.727685,10.392839));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.727584,10.392723));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.728034,10.393195));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.72929,10.3929160));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.731476,10.392573));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.734112,10.391822));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.738174,10.390728));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.743414,10.389354));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.748762,10.387874));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.750947,10.387402));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.755303,10.387895));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.756279,10.388281));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.757302,10.388432));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.757403,10.388592));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.757596,10.388619));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.757724,10.388474));
        debugLocations.add(new DataPoint(sessionLogic.getSession().getId(), System.currentTimeMillis(), sessionLogic.getVehicle().getType().ordinal(),43.758011,10.388442));
        return debugLocations;
    }

    public void setMode(SimulationMode currentMode) {
        this.currentMode = currentMode;
    }

    @Override
    public String getName() {
        return "GPSSimulator";
    }

    /**
     * This handler is used for repeating tasks
     * @return
     */
    Handler getHandler() {
        if(handler == null){
            handler = new Handler();
        }
        return handler;
    }
}
