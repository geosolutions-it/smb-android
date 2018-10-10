package it.geosolutions.savemybike;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import it.geosolutions.savemybike.data.dataProviders.GPSSimulator;
import it.geosolutions.savemybike.data.db.SMBDatabase;
import it.geosolutions.savemybike.data.session.SessionLogic;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.DataPoint;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Robert Oehler on 09.11.17.
 *
 * Tests the logic of adding dataPoints periodically
 * and persisting the session periodically
 *
 * this test may sometimes fail as it depends on the execution time of the device
 * we add a dataPoint every @link dataInterval and the logic will add it every @dataInterval
 * but this may not always be synchronized
 *
 * just retry in case of failure and it should pass
 */

@RunWith(AndroidJUnit4.class)
public class SessionLogicTest {

    private final static int dataInterval = 50;
    private final static int persistInterval = 1000;

    @Test
    public void testSessionLogic() throws InterruptedException {

        final Context context = InstrumentationRegistry.getTargetContext();
        assertNotNull(context);

        final CountDownLatch latch = new CountDownLatch(1);

        final Configuration configuration = Configuration.loadConfiguration(context);
        configuration.dataReadInterval = dataInterval;
        configuration.persistanceInterval = persistInterval;
        final Vehicle.VehicleType vehicleType = Vehicle.VehicleType.BIKE;
        final Vehicle vehicle = new Vehicle(vehicleType, 1000, 0);
        final Session testSession = new Session(vehicleType);

        final SessionLogic logic = new SessionLogic(context, testSession, vehicle, configuration);
        logic.setDatabaseName(DatabaseTest.TEST_DATABASE);
        logic.setTestHandler();
        final ArrayList<DataPoint> dataPoints = GPSSimulator.getTestLocations(logic);

        logic.start();

        //1. first three
        int from = 0;
        int to = 3;
        simulateTo(from, to, dataPoints, logic);

        assertNotNull(logic.getSession());
        while(logic.getSession().getDataPoints().size() != to){
            Log.v("SessionLogic Test", "waiting for add dataPoints task");
        }
        assertTrue("current dataPoints (1) size is " + logic.getSession().getDataPoints().size()+" should be "+to, logic.getSession().getDataPoints().size() == to);

        //next piece
        from = to;
        to = 10;

        simulateTo(from, to, dataPoints, logic);

        while(logic.getSession().getDataPoints().size() != to){
            Log.v("SessionLogic Test", "waiting for add dataPoints task");
        }
        assertTrue("current dataPoints (2) size is " + logic.getSession().getDataPoints().size()+" should be "+to, logic.getSession().getDataPoints().size() == to);

        //the rest
        from = to;
        to = dataPoints.size();

        simulateTo(from, to, dataPoints, logic);

        while(logic.getSession().getDataPoints().size() != to){
            Log.v("SessionLogic Test", "waiting for add dataPoints task");
        }
        assertTrue("current dataPoints (3) size is " + logic.getSession().getDataPoints().size()+" should be "+to,logic.getSession().getDataPoints().size() == to);

        //wait for persistance
        while(logic.getSession().getLastPersistedIndex() < to){
            Log.v("SessionLogic Test", "waiting for persistance task");
        }
        Log.i("SessionLogic Test", "did persist "+ logic.getSession().getLastPersistedIndex() +" dataPoints");
        logic.stop();
        latch.countDown();

        //this should execute in 3 seconds
        try{
            assertTrue("did not persist within expected time", latch.await(3, TimeUnit.SECONDS));
        }catch (Exception e){
            fail("exception awaiting latch");
        }
        //wait another moment for db sync
        Thread.sleep(50);
        assertTrue(!logic.isScheduledPersist());

        //check that db reflects that result

        final SMBDatabase database = new SMBDatabase(context, DatabaseTest.TEST_DATABASE);

        assertTrue(database.open());

        Session dbSession = database.getSession(logic.getSession().getId());
        assertNotNull(dbSession);

        assertTrue(dbSession.getDataPoints().size() >= dataPoints.size());
        assertTrue("did persist until "+dbSession.getLastPersistedIndex()+" expected "+dataPoints.size(),dbSession.getLastPersistedIndex() >= dataPoints.size() - 1);

        int i = 0;
        while (i < dataPoints.size() && dbSession.getDataPoints().get(i).timeStamp != dataPoints.get(i).timeStamp){
            i++;
        }

        if(i >= dataPoints.size()){
            fail("did not find initial index");
        }

        int count = i + dataPoints.size();
        for(;i < count; i++) {
            //pick some stats
            assertEquals(dbSession.getDataPoints().get(i).timeStamp, dataPoints.get(i).timeStamp);
            assertEquals(String.format(Locale.US, "%d : lat should be %.6f is %.6f",i, dataPoints.get(i).latitude, dbSession.getDataPoints().get(i).latitude), dbSession.getDataPoints().get(i).latitude, dataPoints.get(i).latitude, DatabaseTest.DOUBLE_DELTA);
            assertEquals(String.format(Locale.US, "%d : lon should be %.6f is %.6f",i, dataPoints.get(i).longitude, dbSession.getDataPoints().get(i).longitude), dbSession.getDataPoints().get(i).longitude, dataPoints.get(i).longitude, DatabaseTest.DOUBLE_DELTA);
            assertEquals(dbSession.getDataPoints().get(i).elevation, dataPoints.get(i).elevation, DatabaseTest.DOUBLE_DELTA);
            assertEquals(dbSession.getDataPoints().get(i).accuracy, dataPoints.get(i).accuracy, DatabaseTest.DOUBLE_DELTA);
            assertEquals(dbSession.getDataPoints().get(i).gps_bearing, dataPoints.get(i).gps_bearing, DatabaseTest.DOUBLE_DELTA);
            assertEquals(dbSession.getDataPoints().get(i).speed, dataPoints.get(i).speed, DatabaseTest.DOUBLE_DELTA);

        }
        //cleanup

        database.close();

        File file = context.getDatabasePath(DatabaseTest.TEST_DATABASE);

        if(file.exists()) {
            boolean deleted = file.delete();
            assertTrue(deleted);
        }
    }

    private void simulateTo(int from, int to, ArrayList<DataPoint> dataPoints, SessionLogic logic) throws InterruptedException{

        for(int i = from; i < to; i++){
            DataPoint da = dataPoints.get(i);
            Location loc = new Location("");

            loc.setLatitude(da.latitude);
            loc.setLongitude(da.longitude);
            loc.setAltitude(da.elevation);
            loc.setAccuracy(da.accuracy);
            loc.setBearing(da.gps_bearing);
            loc.setSpeed(da.speed);
            loc.setTime(da.timeStamp);

            logic.evaluateNewLocation(loc);
            //sleep a data interval
            Thread.sleep(dataInterval + 1);
        }
    }
}
