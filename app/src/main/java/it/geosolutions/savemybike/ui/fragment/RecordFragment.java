package it.geosolutions.savemybike.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.Util;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.callback.RecordingEventListener;

/**
 * Created by Robert Oehler on 25.10.17.
 *
 * A fragment containing the UI to switch between vehicles, start/stop a session and to show some session stats
 */

public class RecordFragment extends Fragment implements RecordingEventListener {

    private final static String TAG = "RecordFragment";
    private static final int GPS_ENABLE_REQUEST = 789;
    LocationManager mLocationManager;

    @BindViews({
            R.id.mode_foot,
            R.id.mode_bike,
            R.id.mode_bus,
            R.id.mode_car,
            R.id.mode_moped,
            R.id.mode_train
            })
    List<View> modeViews;

    @BindView(R.id.record_button) ImageView recordButton;
    @BindView(R.id.simulate_tv) TextView simulateTV;
    @BindView(R.id.stats_dist) TextView distTV;
    @BindView(R.id.stats_time) TextView timeTV;
    @BindView(R.id.stats_group) Group statsRow;

    private boolean statsHidden = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
    }

    /**
     * inflates the view of this fragment and initializes it
     * @return the inflated view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_record, container,false);
        ButterKnife.bind(this, view);

        invalidateUI(((SaveMyBikeActivity)getActivity()).getCurrentVehicle());

        return view;
    }

    /**
     * invalidates the UI of this fragment by
     * 1. applying the current session state (active or stopped)
     * 2. selecting the current vehicle @param vehicle
     * 3. invalidating session stats if possible/necessary
     * @param vehicle the current vehicle
     */
    @Override
    public void invalidateUI(Vehicle vehicle){
        if(getActivity() != null) {
            Session session = ((SaveMyBikeActivity) getActivity()).getCurrentSession();
            if (session == null) {
                applySessionState(Session.SessionState.STOPPED);
            } else {
                applySessionState(session.getState());
            }
            selectVehicle(vehicle);
            invalidateSessionStats(session);
        }
    }

    /**
     * applies the session state by changing the icon of the record button according to @param state
     * @param state the current state
     */
    @Override
    public void applySessionState(final Session.SessionState state){

        switch (state){

            case ACTIVE:
                //switch to "Pause" UI
                recordButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_record_stop));
                break;
            case STOPPED:
                //switch to "Record" UI
                recordButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_record_play_pause));
                break;
        }
    }

    @Override
    public void stopRecording() {

    }

    /***
     * changes the icon of the vehicle @param vehicle to "selected" (white on black)
     * and the icons of all other vehicles to "unselected" (black on white)
     * @param vehicle the selected vehicle
     */
    public void selectVehicle(Vehicle vehicle){

        for(int i = 0; i < modeViews.size(); i++){

            if(vehicle.getType().ordinal() == i){

                //select
                Drawable mWrappedDrawable = DrawableCompat.wrap(((ImageView)modeViews.get(i)).getDrawable().mutate());
                //DrawableCompat.setTint(mWrappedDrawable, ContextCompat.getColor(getActivity(), android.R.color.white));
                DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);
                ((ImageView)modeViews.get(i)).setImageDrawable(mWrappedDrawable);

                modeViews.get(i).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.mode_selected));
            }else{

                //unselect
                Drawable mWrappedDrawable = DrawableCompat.wrap(((ImageView)modeViews.get(i)).getDrawable().mutate());
                //DrawableCompat.setTint(mWrappedDrawable, ContextCompat.getColor(getActivity(), android.R.color.black));
                DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);
                ((ImageView)modeViews.get(i)).setImageDrawable(mWrappedDrawable);

                modeViews.get(i).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.mode_bordered));
            }
        }
    }

    /**
     * invalidates the session stats UI of this fragment with @param session
     * @param session the session containing the data to invalidate
     */
    public void invalidateSessionStats(final Session session){

        if(session != null){

            if(statsRow != null){
                // this refrence could be null due to close the application
                statsRow.setVisibility(View.VISIBLE);
            }

            final double dist = session.getDistance();
            final long time = session.getOverallTime();
            Log.i(TAG, "DIST: "+ dist+ " TIME: "+time);

            if(((SaveMyBikeActivity)getActivity()).getConfiguration().metric){

                distTV.setText(String.format(Locale.US,"%.2f %s", dist / 1000f, Constants.UNIT_KM));
            }else{
                distTV.setText(String.format(Locale.US,"%.2f %s", dist / 1000f * Constants.KM_TO_MILES, Constants.UNIT_MI));
            }
            timeTV.setText(Util.longToTimeString(time));

        }

    }

    /**
     * click listener for vehicles and record button
     * a click on a vehicle changes the vehicle
     * a click on the record button starts/stops a session
     */
    @OnClick({
            R.id.mode_foot,
            R.id.mode_bike,
            R.id.mode_bus,
            R.id.mode_car,
            R.id.mode_moped,
            R.id.mode_train,
            R.id.record_button})
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.mode_foot:
                vehicleChange(Vehicle.VehicleType.FOOT);
                break;
            case R.id.mode_bike:
                vehicleChange(Vehicle.VehicleType.BIKE);
                break;
            case R.id.mode_bus:
                vehicleChange(Vehicle.VehicleType.BUS);
                break;
            case R.id.mode_car:
                vehicleChange(Vehicle.VehicleType.CAR);
                break;
            case R.id.mode_moped:
                vehicleChange(Vehicle.VehicleType.MOPED);
                break;
            case R.id.mode_train:
                vehicleChange(Vehicle.VehicleType.TRAIN);
                break;
            case R.id.record_button:
                recordButtonHandler();
                break;
        }
    }

    private void recordButtonHandler(){
        recordButtonHandler(null);
    }

    private void recordButtonHandler(Vehicle.VehicleType vtype) {

        vehicleChange(vtype);

        //detect if we are currently recording or not
        Session currentSession = null;

        if(((SaveMyBikeActivity)getActivity()).getCurrentSession() != null){
            currentSession = ((SaveMyBikeActivity)getActivity()).getCurrentSession();
        }

        if(currentSession != null && currentSession.getState() == Session.SessionState.ACTIVE){

            //stop service
            ((SaveMyBikeActivity)getActivity()).stopRecording();

            applySessionState(Session.SessionState.STOPPED);
        } else {
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                ((SaveMyBikeActivity)getActivity()).startRecording();

                applySessionState(Session.SessionState.ACTIVE);
            } else {
                showGPSDiabledDialog();
            }

        }
    }

    private void vehicleChange(Vehicle.VehicleType vtype) {
        if(vtype != null){
            ((SaveMyBikeActivity)getActivity()).changeVehicle(vtype, true);
        }
    }

    /**
     * shows or hides the simulation view
     * @param simulate
     */
    public void applySimulate(boolean simulate){

        simulateTV.setVisibility(simulate ? View.VISIBLE : View.INVISIBLE);
    }



    /**
     * If GPS is disabled, show this dialog
     */
    public void showGPSDiabledDialog() {
        if(getActivity() != null) { // this can be triggered when the user stopped gps. and the application is not present. In this case the activity doesn't exist anymore.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.gps_disabled_title);
            builder.setMessage(R.string.gps_disabled_description);
            builder.setPositiveButton(R.string.gps_enable, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ENABLE_REQUEST);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GPS_ENABLE_REQUEST)
        {
            if (mLocationManager == null)
            {
                mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
            }

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                showGPSDiabledDialog();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
