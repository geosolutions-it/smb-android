package it.geosolutions.savemybike.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.Observation;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.callback.IOnBackPressed;
import it.geosolutions.savemybike.ui.callback.OnFragmentInteractionListener;
import it.geosolutions.savemybike.ui.utils.UIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity.EXTRA_DATA;

public class BikeFoundNotificationFragment extends Fragment implements IOnBackPressed {

    private final static String TAG = BikeFoundNotificationFragment.class.getSimpleName();
    private LocationManager locationManager;
    private OnFragmentInteractionListener listener;

    @BindView(R.id.send_found_notification)
    FloatingActionButton send;
    @BindView(R.id.foundBikeMessage)
    EditText message;
    @BindView(R.id.bike_title)
    TextView bikeFoundName;
    @Nullable
    @BindView(R.id.loading_container)
    View loading;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_bike_found, container, false);
        ButterKnife.bind(this, view);
        Bike bike = new Gson().fromJson(getArguments().getString(EXTRA_DATA), Bike.class);
        bikeFoundName.setText(bike.getName()!=null?bike.getName():bike.getNickname());
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        send.setOnClickListener((View v) -> new AlertDialog.Builder(getActivity())
                .setMessage(R.string.confirm_lost_bike_found)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    updateBikeStatus(bike, message.getText().toString(), getCurrentLocation());
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dialog.cancel();
                }).show());
        return view;
    }


    public void updateBikeStatus(Bike bike, String details, Location location) {

        UIUtils.hideKeyboard(getActivity());
        Observation observation = new Observation(bike.getShort_uuid(), "user", details, "1");
        if (location != null) {
            observation.setAddress(getAddress(location));
            observation.setPosition("POINT (" + location.getLongitude() + " " + location.getLatitude() + ")");
        }else {
            observation.setPosition("POINT (0 0)");
        }
        SMBRemoteServices service = getSmbRemoteServices();
        showLoading(true);
        RetrofitClient.getInstance(getActivity()).performAuthenticatedCall(
                service.sendNewBikeObservations(observation),
                new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        showLoading(false);
                        Toast.makeText(getActivity(), R.string.notification_sent, Toast.LENGTH_LONG).show();
                        exit();

                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.e(TAG, "ERROR: " + t.getMessage());
                        showLoading(false);
                        Toast.makeText(getActivity(), R.string.error_sending_notification, Toast.LENGTH_LONG).show();
                        exit();
                    }
                }
        );
    }

    private void exit() {
        try {
            ((SaveMyBikeActivity) getActivity()).changeFragment(R.id.navigation_home);
        } catch (Exception e) {
            // and error happens when back button was pressed before save end.
            Log.e(TAG, "Error while exiting.", e);
        }
    }

    private SMBRemoteServices getSmbRemoteServices() {
        RetrofitClient client = RetrofitClient.getInstance(getActivity());
        return client.getPortalServices();
    }

    public void showLoading(boolean show) {
        loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onBackPressed() {
        // don't allow exit while loading
        if (loading.getVisibility() == View.GONE) {
            exit();
        }
        return true;

    }

    private Location getCurrentLocation() {
        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            crit.setPowerRequirement(Criteria.POWER_LOW);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            return bestLocation;
        } else {
            Log.v(TAG, "NOT getting current location");
            return null;
        }
    }

    private String getAddress (Location location){
        StringBuilder sb = new StringBuilder("");
        try {
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(" ");
                }
            } else {
                Log.e(TAG, "No address found for location with lat: " + location.getLatitude()
                        + " and long " + location.getLongitude());
            }
        }catch(IOException e){
            Log.e(TAG, "Error retrieving address from location.", e);
        }
        return sb.toString();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            this.listener = (OnFragmentInteractionListener) context;
        }
    }

}
