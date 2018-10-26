package it.geosolutions.savemybike.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.Configuration;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.CurrentStatus;
import it.geosolutions.savemybike.model.PaginatedResult;
import it.geosolutions.savemybike.ui.BikeAdapter;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.tasks.GetRemoteConfigTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Robert Oehler on 26.10.17.
 *
 * A fragment showing a list of bikes
 * Update by Lorenzo Pini on 09.07.2018
 */

public class BikeListFragment extends Fragment {
    public static final String TAG = "BIKELIST";
    @BindView(R.id.bikes_list) ListView listView;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout refreshLayout;
    BikeAdapter bikeAdapter;
    List<Bike> bikes;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_bikes, container, false);
        ButterKnife.bind(this, view);
        SaveMyBikeActivity activity = ((SaveMyBikeActivity)getActivity());
        getBikes();
        // setup adapter
        bikes = activity.getConfiguration().getBikes(activity);
        bikeAdapter = new BikeAdapter(activity, R.layout.item_bike, activity.getConfiguration().getBikes(activity)) {
            @Override
            public void updateStatus(Bike bike, String details) {
                updateBikeStatus(bike, details);
            }
        };
        refreshLayout.setOnRefreshListener(() -> getBikes());
        listView.setAdapter(bikeAdapter);
        // set up empty view
        View empty = view.findViewById(R.id.empty_bikes);

        if(empty != null && bikes != null && bikes.size() > 0) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }


        return view;
    }
    @OnClick(R.id.open_portal_button)
    public void openPortal() {
        Uri uriUrl = Uri.parse(Constants.PORTAL_ENDPOINT);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
    /**
     * Call the API to update a bike's status
     */
    public void updateBikeStatus(Bike bike, String details) {
        SaveMyBikeActivity activity = ((SaveMyBikeActivity)getActivity());
        if(details == null){
            details = "";
        }

        RetrofitClient rclient = RetrofitClient.getInstance(getActivity());
        SMBRemoteServices smbserv = rclient.getPortalServices();

        CurrentStatus newStatus = new CurrentStatus();
        newStatus.setBike(Constants.PORTAL_ENDPOINT + "api/my-bikes/"+bike.getShort_uuid()+"/");
        newStatus.setDetails(details);
        newStatus.setLost(!bike.getCurrentStatus().getLost());
        Call<Object> call = smbserv.sendNewBikeStatus(newStatus);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.i(TAG, "Response Message: "+ response.message());
                Log.i(TAG, "Response Body: "+ response.body());

                if(response.isSuccessful()) {
                    bike.getCurrentStatus().setLost(!bike.getCurrentStatus().getLost());
                    bikeAdapter.notifyDataSetInvalidated();
                } else {
                    Log.w(TAG, "Bike update UNSUCCESSFUL");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "ERROR: "+ t.getMessage());
            }
        });
    }
    public void getBikes() {
        Context context = getContext();
        showLoading(true);
        new GetRemoteConfigTask(context,  null, new RetrofitClient.GetBikesCallback() {
            @Override
            public void gotBikes(final PaginatedResult<Bike> bikesList) {
                if (bikesList != null){
                    showLoading(false);
                    if (bikesList.getResults() != null) {

                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "Number of downloaded bikes: " + bikesList.getResults().size());
                        }
                        //save the config
                        it.geosolutions.savemybike.model.Configuration.saveBikes(context, bikesList.getResults());
                        bikeAdapter.clear();
                        bikeAdapter.addAll(bikesList.getResults());
                        bikeAdapter.notifyDataSetChanged();


                    } else {
                        Log.w(TAG, "Wrong bikes response: server did not return a results array");

                    }

                } else {
                    Log.e(TAG, "Wrong bikes response: check for authentication or network errors");
                }
            }

            @Override
            public void error(String message) {
                Log.e(TAG, "error downloading bikes: " + message);
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG);
                showLoading(false);
            }
        }).execute();
    }
    @OnClick(R.id.add_bike_button)
    public void onClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PORTAL_ENDPOINT + "/bikes"));
        startActivity(browserIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(true);
        getBikes();
    }

    public void showLoading(boolean show) {
        refreshLayout.setRefreshing(show);
        // loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
