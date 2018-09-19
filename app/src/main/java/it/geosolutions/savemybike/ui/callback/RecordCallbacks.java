package it.geosolutions.savemybike.ui.callback;


import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;

/**
 * Interface for callbacks called during a recording session
 */
public interface RecordCallbacks {
    void invalidateSessionStats(final Session session);
    void selectVehicle(Vehicle vehicle);
    void invalidateUI(Vehicle currentVehicle);
    void applySimulate(boolean simulate);
    void applySessionState(Session.SessionState stopped);
}
