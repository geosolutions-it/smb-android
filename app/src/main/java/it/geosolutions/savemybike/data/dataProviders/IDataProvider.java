package it.geosolutions.savemybike.data.dataProviders;

/**
 * Created by Robert Oehler on 28.10.17.
 *
 * Interface for data providers
 */

public interface IDataProvider {

    /**
     * data time interval
     */
    int DATA_INTERVAL = 1000;

    void start();

    void stop();

    String getName();
}
