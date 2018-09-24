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
 * Created by Lorenzo Natali
 * Contains user
 *
 */

public class UserFragment extends Fragment{
    private int initialItem = R.id.navigation_user_profile;
    @BindView(R.id.navigation) BottomNavigationView navigation;
    @BindView(R.id.activities_content) ViewPager viewPager;
    /**
     * navigation listener to switch between fragments
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> setNavigation(item.getItemId());
    public void setInitialItem(int initialItem) {
        this.initialItem = initialItem;
    }

    public boolean setNavigation(int itemId) {

        switch(itemId){
            case R.id.navigation_user_profile:
                selectItem(0);
                return true;
            case R.id.navigation_badges:
                selectItem(1);
                return true;

        }
        return false;
    }
    public void selectItem(int index) {
        viewPager.setCurrentItem(index);
        alignMenu(index);
    }
    private void alignMenu(int index) {
        if(navigation.getMenu().getItem(index) != null) {
            navigation.getMenu().getItem(index).setChecked(true);
        }

    }

    /**
     * inflate and setup the view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_fragment, container,false);
        ButterKnife.bind(this, view);
        setupViewPager(view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        return view;
    }


    private void setupViewPager(View view) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        // Add Fragments to adapter one by one
        adapter.addFragment(new ProfileFragment(), getResources().getString(R.string.profile));
        adapter.addFragment(new BadgesFragment(), getResources().getString(R.string.badges));
        viewPager.setAdapter(adapter);
        setNavigation(this.initialItem);


    }

}
