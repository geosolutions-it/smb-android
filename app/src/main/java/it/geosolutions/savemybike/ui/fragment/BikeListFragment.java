package it.geosolutions.savemybike.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.Configuration;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.CurrentStatus;
import it.geosolutions.savemybike.ui.BikeAdapter;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
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

    BikeAdapter bikeAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_bikes, container, false);
        ButterKnife.bind(this, view);
        SaveMyBikeActivity activity = ((SaveMyBikeActivity)getActivity());

        // setup adapter
        List<Bike> bikes = activity.getConfiguration().getBikes(activity);
        bikeAdapter = new BikeAdapter(activity, R.layout.item_bike, activity.getConfiguration().getBikes(activity)) {
            @Override
            public void updateStatus(Bike bike, String details) {
                updateBikeStatus(bike, details);
            }
        };
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


    @OnClick(R.id.add_bike_button)
    public void onClick() {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PORTAL_ENDPOINT + "/bikes"));
        startActivity(browserIntent);

        // Toast.makeText(getActivity(), "Todo : add another bike", Toast.LENGTH_SHORT).show();
    }

    /**
     * Call the API to update a bike's status
     */
    public void updateBikeStatus(Bike bike, String details) {

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

}
