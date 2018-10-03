package it.geosolutions.savemybike.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pixelcan.inkpageindicator.InkPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.ui.activity.SMBBaseActivity;
import it.geosolutions.savemybike.ui.adapters.ViewPagerAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public abstract class WizardFragment extends Fragment {

    private boolean mConsumePageSelectedEvent;
    /**
     * The flag asks if the user can exit the wizard at the first step
     */
    private boolean canExit = true;
    @BindView(R.id.viewpager) public ViewPager viewPager;
    @BindView(R.id.indicator) public InkPageIndicator indicator;
    @BindView(R.id.prev_button) public Button mPrevButton;
    @BindView(R.id.next_button) public Button mNextButton;

    /**
     * Add to the adapter the fragments for the wizard
     * @param adapter
     */
    public abstract void setupSteps(ViewPagerAdapter adapter);

    public abstract void onComplete();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wizard, container, false);
        ButterKnife.bind(this, view);
        setupViewPager(view);
        return view;
    }
    private void setupViewPager(View view) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        setupSteps(adapter);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }
                updateBottomBar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // disable swipe if not valid

        viewPager.setOnTouchListener((View v, MotionEvent event) ->
                !isValid(viewPager.getCurrentItem())
                    // To avoid deny swipe back at the last page
                    // TODO: improve this check to allow swipe back in middle pages, if needed
                    && viewPager.getCurrentItem() != viewPager.getAdapter().getCount() - 1
        );
        updateBottomBar();

    }
    @OnClick(R.id.next_button)
    public void next() {
        int curr = viewPager.getCurrentItem(); // TODO: check valid
        if(curr < viewPager.getAdapter().getCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        } else if (curr == viewPager.getAdapter().getCount() - 1) {
            onComplete();
        }
    }
    @OnClick(R.id.prev_button)
    public void prev() {
        int curr = viewPager.getCurrentItem();
        if(curr > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        } else if (curr == 0 && canExit && getActivity() instanceof SMBBaseActivity) {
            SMBBaseActivity activity = (SMBBaseActivity) getActivity();
            activity.logout();
        }
    }
    public void updateBottomBar() {
        int position = viewPager.getCurrentItem();
        mNextButton.setEnabled(isValid(position));
        if (position == viewPager.getAdapter().getCount() - 1) {
            mNextButton.setText(R.string.finish);
        } else {
            mNextButton.setText(R.string.next);

        }
        if(canExit && position <= 0) {
            mPrevButton.setText(R.string.cancel);
        }
        mPrevButton.setVisibility(position <= 0 && !canExit ? View.INVISIBLE : View.VISIBLE);
    }

    public boolean isValid(int position) {
        return true;
    }


}
