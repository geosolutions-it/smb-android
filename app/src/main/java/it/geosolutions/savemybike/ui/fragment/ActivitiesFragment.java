package it.geosolutions.savemybike.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;
import it.geosolutions.savemybike.ui.adapters.ViewPagerAdapter;
import it.geosolutions.savemybike.ui.callback.RecordingEventListener;

/**
 * Created by Robert Oehler on 25.10.17.
 *
 * A fragment showing the record image and the current tracks
 */

public class ActivitiesFragment extends Fragment implements RecordingEventListener {
    private int initialItem = R.id.navigation_record;
    @BindView(R.id.navigation) BottomNavigationView navigation;
    @BindView(R.id.activities_content) ViewPager viewPager;
    /**
     * navigation listener to switch between fragments
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> setNavigation(item.getItemId());

    public boolean setNavigation(int itemId) {

        switch(itemId){
            case R.id.navigation_record:
                viewPager.setCurrentItem(0);
                alignMenu(0);
                return true;
            case R.id.navigation_stats:
                viewPager.setCurrentItem(1);
                alignMenu(1);
                return true;
            case R.id.sessions_list:
                viewPager.setCurrentItem(1);
                alignMenu(1);
                switchToSubFragment(itemId);
                return true;
            case R.id.tracks_list:
                viewPager.setCurrentItem(1);
                alignMenu(1);
                switchToSubFragment(itemId);
                return true;

        }
        return false;
    }
    private void alignMenu(int index) {
        if(navigation.getMenu().getItem(index) != null) {
            navigation.getMenu().getItem(index).setChecked(true);
        }

    }

    public void setInitialItem(int initialItem) {
        this.initialItem = initialItem;
    }

    /**
     * inflate and setup the view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.record_tracks, container,false);
        ButterKnife.bind(this, view);
        setupViewPager(view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        return view;
    }


    private void setupViewPager(View view) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        // Add Fragments to adapter one by one
        adapter.addFragment(new RecordFragment(), getResources().getString(R.string.record));
        adapter.addFragment(new StatsFragment(), getResources().getString(R.string.title_stats));
        viewPager.setAdapter(adapter);
        setNavigation(this.initialItem);


    }

    // TODO: put in an upper class or refactor this interaction
    public void invalidateSessionStats(final Session session) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        Fragment f = adapter.getItem(viewPager.getCurrentItem());
        if(f instanceof RecordingEventListener) {
            ((RecordingEventListener) f).invalidateSessionStats(session);

        }
    };
    // TODO: put in an upper class or refactor this interaction
    public void selectVehicle(Vehicle vehicle) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        Fragment f = adapter.getItem(viewPager.getCurrentItem());
        if(f instanceof RecordingEventListener) {
            ((RecordingEventListener) f).selectVehicle(vehicle);

        }
    }
    // TODO: put in an upper class or refactor this interaction
    public void invalidateUI(Vehicle currentVehicle) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        Fragment f = adapter.getItem(viewPager.getCurrentItem());
        if(f instanceof RecordingEventListener) {
            ((RecordingEventListener) f).invalidateUI(currentVehicle);

        }
    }

    public void applySimulate(boolean simulate) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        Fragment f = adapter.getItem(viewPager.getCurrentItem());
        if(f instanceof RecordingEventListener) {
            ((RecordingEventListener) f).applySimulate(simulate);

        }
    }

    @Override
    public void applySessionState(Session.SessionState stopped) {
        // refresh sessions view due to a record ending
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        Fragment f = adapter.getItem(viewPager.getCurrentItem());
        if(f instanceof RecordingEventListener) {
            ((RecordingEventListener) f).applySessionState(stopped);
        }
    }

    @Override
    public void stopRecording() {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        if(adapter.getItem(1) instanceof StatsFragment) {
            StatsFragment frag = (StatsFragment) adapter.getItem(1);
            frag.refreshSessions();
        }

    }
    public void switchToSubFragment(int id) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        if(adapter.getItem(1) instanceof StatsFragment) {
            StatsFragment frag = (StatsFragment) adapter.getItem(1);
            frag.switchTo(id);
        }

    }
}
