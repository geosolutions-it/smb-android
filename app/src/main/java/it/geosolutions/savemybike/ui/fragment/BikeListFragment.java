package it.geosolutions.savemybike.ui.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;

/**
 * Created by Robert Oehler on 26.10.17.
 *
 * A fragment showing a list of bikes
 * Update by Lorenzo Pini on 09.07.2018
 */

public class BikeListFragment extends Fragment {

    @BindView(R.id.bikes_list) ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_bikes, container, false);
        ButterKnife.bind(this, view);

        listView.setAdapter(((SaveMyBikeActivity)getActivity()).getBikeAdapter());

        return view;
    }


    @OnClick(R.id.add_bike_button)
    public void onClick() {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PORTAL_ENDPOINT + "/bikes"));
        startActivity(browserIntent);

        // Toast.makeText(getActivity(), "Todo : add another bike", Toast.LENGTH_SHORT).show();
    }

}
