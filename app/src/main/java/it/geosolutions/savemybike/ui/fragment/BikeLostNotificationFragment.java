package it.geosolutions.savemybike.ui.fragment;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.CurrentStatus;
import it.geosolutions.savemybike.model.PaginatedResult;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.callback.IOnBackPressed;
import it.geosolutions.savemybike.ui.tasks.GetRemoteConfigTask;
import it.geosolutions.savemybike.ui.utils.UIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Fragment for Bike Lost/Found
 */

public class BikeLostNotificationFragment extends Fragment implements IOnBackPressed {
    public static final String TAG = BikeLostNotificationFragment.class.getSimpleName();
    public static final String BIKE_ARGUMENT = "BIKE";
    @BindView(R.id.send_lost_notification) FloatingActionButton send;
    @BindView(R.id.lostBikeMessage) EditText message;
    private LatLng location;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_bike_lost, container, false);
        ButterKnife.bind(this, view);
        InsertLocationFragment locationPicker = new InsertLocationFragment();
        locationPicker.setCallbacks(new InsertLocationFragment.Callbacks() {
            @Override
            public void onAddressSelected(Address address) {

            }

            @Override
            public void onPointSelected(LatLng point) {
                location = point;
                send.setEnabled(true);
                message.requestFocus();

            }
        });
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.insert_location, locationPicker);
        fragmentTransaction.commit();
        send.setOnClickListener((View v) -> {
            new AlertDialog.Builder(getActivity())
                .setMessage(R.string.confirm_bike_lost)
                .setPositiveButton(R.string.confirm, ( dialog,  id) -> {
                    updateBikeStatus((Bike)getArguments().getSerializable(BIKE_ARGUMENT), message.getText().toString());
                })
                .setNegativeButton(R.string.cancel, ( dialog,  id) -> {
                    // TODO: close
                }).show();
        });
        return view;
    }
    /**
     * Call the API to update a bike's status
     */
    public void updateBikeStatus(Bike bike, String details) {
        UIUtils.hideKeyboard(getActivity());
        if(details == null){
            details = "";
        }

        // prepare status to send
        CurrentStatus newStatus = new CurrentStatus();
        newStatus.setBike(Constants.PORTAL_ENDPOINT + "api/my-bikes/"+bike.getShort_uuid()+"/");
        newStatus.setDetails(details);
        if(location != null) {
            newStatus.setPosition("POINT (" + location.longitude + " " +location.latitude +")");
        }

        newStatus.setLost(!bike.getCurrentStatus().getLost());

        // send new status
        SMBRemoteServices service = getSmbRemoteServices();
        Call<Object> call = service.sendNewBikeStatus(newStatus);
        showLoading(true);
        Context context = getContext();
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                // Reload bikes, then show again the bike list
                new GetRemoteConfigTask(context,  null, new RetrofitClient.GetBikesCallback() {
                    @Override
                    public void gotBikes(final PaginatedResult<Bike> bikesList) {
                        if (bikesList != null){
                            if (bikesList.getResults() != null) {

                                if (BuildConfig.DEBUG) {
                                    Log.i(TAG, "Number of downloaded bikes: " + bikesList.getResults().size());
                                }

                                //save the config
                                Configuration.saveBikes(context, bikesList.getResults());


                            } else {
                                Log.w(TAG, "Wrong bikes response: server did not return a results array");
                                // Removing the bikes list
                                Configuration.saveBikes(context, new ArrayList<>());
                            }

                        } else {
                            Log.e(TAG, "Wrong bikes response: check for authentication or network errors");
                            // Removing the bikes list
                            Configuration.saveBikes(context, new ArrayList<>());
                        }
                        exit();
                    }

                    @Override
                    public void error(String message) {
                        Log.e(TAG, "error downloading bikes: " + message);
                        showLoading(false);
                    }
                }).execute();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "ERROR: "+ t.getMessage());
                Toast.makeText(getActivity(), R.string.error_saving_bike_status, Toast.LENGTH_LONG);
                showLoading(false);
            }
        });
    }

    private void exit() {
        try {
            ((SaveMyBikeActivity) getActivity()).changeFragment(R.id.navigation_bikes);
        } catch (Exception e) {
            // and error happens when back button was pressed before save end.
            Log.e(TAG, "Error while exiting.", e);
        }
    }

    private SMBRemoteServices getSmbRemoteServices() {
        RetrofitClient client = RetrofitClient.getInstance(getActivity());
        return client.getPortalServices();
    }

    @BindView(R.id.bike_status_loading) View loading;
    public void showLoading(boolean show) {
        loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onBackPressed() {
        // don't allow exit while loading
        if(loading.getVisibility() == View.GONE) {
            exit();
        }
        return true;

    }
}
