package it.geosolutions.savemybike;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.Vehicle;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by Robert Oehler on 13.11.17.
 *
 * Test the correct parsing of the default config
 *
 */

@RunWith(AndroidJUnit4.class)
public class ConfigTest {

    @Test
    public void testConfigParsing(){

        Context context = InstrumentationRegistry.getTargetContext();
        assertNotNull(context);

        final Configuration  configuration = Configuration.loadConfiguration(context);

        assertNotNull(configuration);

        assertNotNull(configuration.bikes);
        assertNotNull(configuration.vehicles);

        assertEquals(configuration.bikes.size(), 2);
        assertEquals(configuration.vehicles.size(), 6);

        assertEquals(configuration.dataReadInterval, 1000);
        assertEquals(configuration.persistanceInterval, 15000);
        assertEquals(configuration.metric, true);
        assertEquals(configuration.id, "saveMyBike");

        assertEquals(configuration.vehicles.get(0).getType(), Vehicle.VehicleType.FOOT);
        assertEquals(configuration.vehicles.get(1).getType(), Vehicle.VehicleType.BIKE);
        assertEquals(configuration.vehicles.get(2).getType(), Vehicle.VehicleType.BUS);
        assertEquals(configuration.vehicles.get(3).getType(), Vehicle.VehicleType.CAR);

        assertEquals(configuration.vehicles.get(0).getMinimumGPSDistance(), 0);
        assertEquals(configuration.vehicles.get(1).getMinimumGPSDistance(), 10);
        assertEquals(configuration.vehicles.get(2).getMinimumGPSDistance(), 20);
        assertEquals(configuration.vehicles.get(3).getMinimumGPSDistance(), 50);

        assertEquals(configuration.vehicles.get(0).getMinimumGPSTime(), 1000);
        assertEquals(configuration.vehicles.get(1).getMinimumGPSTime(), 1000);
        assertEquals(configuration.vehicles.get(2).getMinimumGPSTime(), 5000);
        assertEquals(configuration.vehicles.get(3).getMinimumGPSTime(), 10000);

        assertEquals(configuration.vehicles.get(0).isSelected(), false);
        assertEquals(configuration.vehicles.get(1).isSelected(), true);
        assertEquals(configuration.vehicles.get(2).isSelected(), false);
        assertEquals(configuration.vehicles.get(3).isSelected(), false);

        assertEquals(configuration.bikes.get(0).isSelected(), true);
        assertEquals(configuration.bikes.get(1).isSelected(), false);

        assertEquals(configuration.bikes.get(0).isStolen(), false);
        assertEquals(configuration.bikes.get(1).isStolen(), true);

        assertEquals(configuration.bikes.get(0).getName(), "Bianchi");
        assertEquals(configuration.bikes.get(1).getName(), "De Rosa");

        assertEquals(configuration.bikes.get(0).getRemoteId(), "123");
        assertEquals(configuration.bikes.get(1).getRemoteId(), "124");
    }
}
