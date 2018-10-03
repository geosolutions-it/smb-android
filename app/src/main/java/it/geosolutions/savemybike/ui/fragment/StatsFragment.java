package it.geosolutions.savemybike.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.ui.adapters.ViewPagerAdapter;

/**
 * Created by Robert Oehler on 25.10.17.
 *
 * A fragment showing the stats of the session of the local database
 */

public class StatsFragment extends Fragment {
    private ViewPager viewPager;
    /**
     * inflate and setup the view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats, container,false);
        ButterKnife.bind(this, view);
        setupViewPager(view);

        return view;
    }

    private void setupViewPager(View view) {
        viewPager = view.findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new TracksFragment(), getResources().getString(R.string.tracks));
        adapter.addFragment(new SessionsFragment(), getResources().getString(R.string.sessions));
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    // TODO: improve this interface
    public void refreshSessions() {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        SessionsFragment f = (SessionsFragment) adapter.getItem(1);
        f.invalidateSessions();

    }
}
