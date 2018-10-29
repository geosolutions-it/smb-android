package it.geosolutions.savemybike.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;
import it.geosolutions.savemybike.ui.adapters.ViewPagerAdapter;
import it.geosolutions.savemybike.ui.callback.RecordingEventListener;

/**
 * Created by Robert Oehler on 25.10.17.
 *
 * A fragment showing the stats of the session of the local database
 */

public class StatsFragment extends Fragment implements RecordingEventListener {
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
   
    public void switchTo(int id) {
        switch (id) {
            case R.id.tracks_list:
                viewPager.setCurrentItem(0);
                break;
            case R.id.sessions_list:
                viewPager.setCurrentItem(1);
                break;
        }
    }
    // TODO: put in an upper class or refactor this interaction
    public void invalidateSessionStats(final Session session) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        for(Fragment f : getAllFragments(adapter)) {
            if (f instanceof RecordingEventListener) {
                ((RecordingEventListener) f).invalidateSessionStats(session);
            }
        }
    };
    // TODO: put in an upper class or refactor this interaction
    public void selectVehicle(Vehicle vehicle) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        for(Fragment f : getAllFragments(adapter)) {
            if (f instanceof RecordingEventListener) {
                ((RecordingEventListener) f).selectVehicle(vehicle);

            }
        }
    }

    // TODO: put in an upper class or refactor this interaction
    public void invalidateUI(Vehicle currentVehicle) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        for(Fragment f : getAllFragments(adapter)) {
            if (f instanceof RecordingEventListener) {
                ((RecordingEventListener) f).invalidateUI(currentVehicle);
            }
        }
    }

    public void applySimulate(boolean simulate) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        for(Fragment f : getAllFragments(adapter)) {
            if (f instanceof RecordingEventListener) {
                ((RecordingEventListener) f).applySimulate(simulate);

            }
        }
    }

    @Override
    public void applySessionState(Session.SessionState stopped) {
        // refresh sessions view due to a record ending
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        for(Fragment f : getAllFragments(adapter)) {
            if (f instanceof RecordingEventListener) {
                ((RecordingEventListener) f).applySessionState(stopped);
            }
        }
    }

    @Override
    public void stopRecording() {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        for(Fragment f : getAllFragments(adapter)) {
            if( f instanceof RecordingEventListener) {
                ((RecordingEventListener) f).stopRecording();
            }
        }
    }
    private List<Fragment> getAllFragments(ViewPagerAdapter adapter) {
        List<Fragment> allFragments = new LinkedList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            Fragment f = adapter.getItem(i);
            allFragments.add(f);
        }
        return allFragments;
    }
}
