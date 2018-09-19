package it.geosolutions.savemybike.ui.callback;


import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;

/**
 * Interface for callbacks called during a recording session.
 *
 * TODO: restructure methods names. They are not so meanful due to a previous implementation
 */
public interface RecordingEventListener {
    void invalidateSessionStats(final Session session);

    /**
     * Called on vehicle selection change
     * @param vehicle
     */
    void selectVehicle(Vehicle vehicle);
    void invalidateUI(Vehicle currentVehicle);
    void applySimulate(boolean simulate);

    /**
     * Called on session state change.
     * NOTE: this may be merged with stop recording,
     * but it may interfer with origineal RecordFragment callbacks.
     * TODO: refactor RecordFragment to have a clear callbacks interface
     * @param state
     */
    void applySessionState(Session.SessionState state);

    /**
     * Called when the recording session stops
     */
    void stopRecording();
}
