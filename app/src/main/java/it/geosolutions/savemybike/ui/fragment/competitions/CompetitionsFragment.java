package it.geosolutions.savemybike.ui.fragment.competitions;

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
import it.geosolutions.savemybike.ui.adapters.ViewPagerAdapter;

/**
 * Created by Lorenzo Natali
 * Base container for Prizes views. Allow to navigate user's won and current available competitions
 *
 */

public class CompetitionsFragment extends Fragment
{
    private int initialItem = R.id.navigation_competitions_available;

    @BindView(R.id.navigation) BottomNavigationView navigation;
    @BindView(R.id.activities_content) ViewPager viewPager;

    /**
     * navigation listener to switch between fragments
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> setNavigation(item.getItemId());

    public void setInitialItem(int initialItem)
    {
        this.initialItem = initialItem;
    }

    public boolean setNavigation(int itemId) {

        switch(itemId){

            case R.id.navigation_competitions_available:
                selectItem(0);
                return true;
	        case R.id.navigation_competitions_participating:
	        	selectItem(1);
	        	return true;
	        case R.id.navigation_my_prizes:
		        selectItem(2);
		        return true;
        }
        return false;
    }
    public void selectItem(int index)
    {
        viewPager.setCurrentItem(index);
        alignMenu(index);
    }

    private void alignMenu(int index)
    {
        if(navigation.getMenu().getItem(index) != null) {
            navigation.getMenu().getItem(index).setChecked(true);
        }
    }

    /**
     * inflate and setup the view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_competitions, container,false);
        ButterKnife.bind(this, view);
        setupViewPager(view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        return view;
    }


    private void setupViewPager(View view)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

	    adapter.addFragment(new AvailableCompetitionsFragment(), getResources().getString(R.string.competitions_available));
        adapter.addFragment(new CurrentCompetitionsFragment(), getResources().getString(R.string.competitions_participating));
        adapter.addFragment(new WonCompetitionsFragment(), getResources().getString(R.string.my_prizes));

        viewPager.setAdapter(adapter);

        setNavigation(this.initialItem);
    }

}
