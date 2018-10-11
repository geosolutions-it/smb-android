package it.geosolutions.savemybike.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Fragment for home page
 */

public class HomeFragment extends Fragment {
    public static final String TAG = "BADGES_LIST";
    @BindView(R.id.link_badges) View badges;
    @BindView(R.id.link_bikes) View bikes;
    @BindView(R.id.link_prizes) View prizes;
    @BindView(R.id.link_tracks) View tracks;
    @BindView(R.id.link_record) View record;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        bikes.setOnClickListener( (view1) -> navigateTo(R.id.navigation_bikes));
        badges.setOnClickListener( (view1) -> navigateTo(R.id.navigation_badges));
        prizes.setOnClickListener( (view1) -> navigateTo(R.id.navigation_prizes));
        tracks.setOnClickListener( (view1) -> navigateTo(R.id.navigation_stats));
        record.setOnClickListener( (view1) -> navigateTo(R.id.navigation_record));
        return view;

    }

    private void navigateTo(int resource) {
        if(getActivity() instanceof SaveMyBikeActivity) {
            ((SaveMyBikeActivity) getActivity()).changeFragment(resource);
        }
    }



}
