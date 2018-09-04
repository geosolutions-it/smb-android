package it.geosolutions.savemybike.ui.activity;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.model.DataPoint;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.ui.adapters.TrackDetailsViewPagerAdapter;
import it.geosolutions.savemybike.ui.callback.OnFragmentInteractionListener;
import it.geosolutions.savemybike.ui.fragment.TrackDetailsFragment;
import it.geosolutions.savemybike.ui.tasks.GetSessionTask;


/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Activity for track details view. Manages initialization of the map and various fragments for
 * the details of a track.
 *
 */
public class TrackDetailsActivity extends SMBBaseActivity implements OnMapReadyCallback, OnFragmentInteractionListener {

    private GoogleMap mMap;
    private Session session;
    public static final String TRACK_ID = "TRACK_ID";
    View bottomSheet;
    BottomSheetBehavior bottomSheetBehaviour;
    View tapActionLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_details);
        bindDependencies();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpBottomSheet();
        setupActionBar();

        setupViewPager();

    }

    private void setupViewPager() {
        ViewPager viewPager = findViewById(R.id.track_details_viewpager);
        TrackDetailsViewPagerAdapter adapter = new TrackDetailsViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new TrackDetailsFragment(), getBaseContext().getResources().getString(R.string.track_details));
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.track_details_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        findViewById(R.id.tracks_bar_layout).bringToFront();
        toolbar.bringToFront();
    }

    private void bindDependencies() {
        ButterKnife.bind(this);
        if(bottomSheet == null) {
            bottomSheet = findViewById(R.id.track_details_bottom_sheet);
        }
        if(tapActionLayout == null) {
            tapActionLayout = findViewById(R.id.tap_action_layout);
        }
        if(toolbar == null) {
            toolbar = findViewById(R.id.track_details_toolbar);
        }
    }
    private void setUpBottomSheet() {
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehaviour.setPeekHeight(300);
        bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        tapActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehaviour.getState()==BottomSheetBehavior.STATE_COLLAPSED)
                {
                    bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
    }
    private void loadData() {
        Long itemId = (Long) getIntent().getExtras().get(TRACK_ID);
        new GetSessionTask(getBaseContext(), new GetSessionTask.SessionCallback() {
            @Override
            public void showProgressView() {

            }

            @Override
            public void hideProgressView() {

            }

            @Override
            public void done(Session s) {
                session = s;
                if(mMap != null) {
                    displayData();
                }
            }
        }, itemId).execute();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        displayData();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void displayData() {
        // setup data in map
        // update item view
        if(session != null) {
            // TODO: share code with StatsFragment
            View view = findViewById(R.id.session_row);
            final TextView distanceTV = view.findViewById(R.id.dist_value);
            final TextView dataTV = view.findViewById(R.id.data_value);
            final TextView dateTV = view.findViewById(R.id.session_start_datetime);
            final TextView durationTV = view.findViewById(R.id.session_duration_text);

            if (this.getConfiguration().metric) {
                distanceTV.setText(String.format(Locale.US, "%.1f %s", (session.getDistance() / 1000f), Constants.UNIT_KM));
            } else {
                distanceTV.setText(String.format(Locale.US, "%.1f %s", (session.getDistance() / 1000f) * Constants.KM_TO_MILES, Constants.UNIT_MI));
            }
            dataTV.setText(String.format(Locale.US, "%d", session.getDataPoints().size()));
            dateTV.setText(DateTimeFormat.forPattern("dd MMM, 'ore' HH:mm").print(session.getStartingTime()));
            durationTV.setText(DateTimeFormat.forPattern("HH:mm:ss").print(session.getOverallTime()));
        }
        // update item info
        if(session != null && mMap != null) {
            ArrayList<DataPoint> points = session.getDataPoints();
            PolylineOptions pOpts = new PolylineOptions();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (DataPoint p : points) {
                LatLng latLng = new LatLng(p.latitude, p.longitude);
                pOpts.add(latLng);
                builder.include(latLng);
            }
            mMap.addPolyline(pOpts);
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 40));
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}