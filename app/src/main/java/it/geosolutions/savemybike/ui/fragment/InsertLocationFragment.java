package it.geosolutions.savemybike.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.ui.adapters.AddressAdapter;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * This fragment helps to insert a location with GeoCoder, and map
 * When a position is selected, searching or tapping on the map, callback methods are called
 */
public class InsertLocationFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = InsertLocationFragment.class.getSimpleName();

    /**
     * Callbacks for events coming from this selection fragment
     */
    public interface Callbacks {
        /**
         * Called when an address is selected (using GeoCoder)
         * @param address
         */
        void onAddressSelected(Address address);

        /**
         * Called whet a point is selected (using the GeoCoder,
         * tap on the map or dragging the marker
         * @param point
         */
        void onPointSelected(LatLng point);
    }
    Callbacks callbacks;

    /**
     * set handlers for position or item selection
     * @param callbacks
     */
    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    @BindView(R.id.geocoderInputSearch) EditText geocoderSearchInput;
    @BindView(R.id.gecoderResults) ListView resultList;

    SupportMapFragment mapFragment;
    ArrayList<Address> addressList = new ArrayList<>();
    AddressAdapter adapter;
    private GoogleMap googleMap;
    private Marker marker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insert_location, container, false);

        ButterKnife.bind(this, view);

        // Inflate the map fragment programmatically.
        // This helps to get a not-null reference and, so, register callbacks and call getMapAsync.
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.insert_location_map_fragment);

        FragmentManager fm = getChildFragmentManager();

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.insert_location_map_fragment, mapFragment).commit();
            fm.executePendingTransactions();
        }

        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupMapLocation(googleMap);
        // set proper padding to give space for the GeoCoder
        googleMap.setPadding(0, 170, 0, 0);
        initGeocoder();

        // setup handlers for position picking
        googleMap.setOnMapClickListener(latLng -> {
            setMarkerPosition(latLng);
            if(callbacks != null) {
                callbacks.onPointSelected(latLng);
            }
        });
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {

            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker m) {
                Log.d("System out", "onMarkerDragEnd...");
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });
    }

    /**
     * Setup map position and location support.
     * Zoom to current location, enable location and so on
     * @param googleMap
     */
    private void setupMapLocation(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "setting my location");
            googleMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            crit.setPowerRequirement(Criteria.POWER_LOW);
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(crit, false));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

        }else{
            Log.v(TAG, "NOT setting my location");
        }
    }

    /**
     * Initialize GeoCoder input and result list
     * TODO: we could isolate this component
     */
    public void initGeocoder() {
        adapter = new AddressAdapter(getContext(), R.layout.geocoder_result, addressList) {
            @Override
            protected void useItemTextHandler(Address item) {
                geocoderSearchInput.setText(getAddressString(item));
                geocode();
            }

            @Override
            protected void itemClickHandler(Address item) {
                addressList.clear();
                adapter.notifyDataSetChanged();

                setMarkerPosition(new LatLng(item.getLatitude(), item.getLongitude()));

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                String currentAddress = getAddressString(item);
                geocoderSearchInput.setText(currentAddress);
                if(callbacks != null) {
                    callbacks.onAddressSelected(item);
                    callbacks.onPointSelected(new LatLng(item.getLatitude(), item.getLongitude()));
                }
            }
        };
       resultList.setAdapter(adapter);
       geocoderSearchInput.setOnEditorActionListener( (textView, actionId, keyEvent) -> {
           if(actionId == EditorInfo.IME_ACTION_SEARCH
                   || actionId == EditorInfo.IME_ACTION_DONE
                   || keyEvent.getAction() == keyEvent.ACTION_DOWN
                   || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {
               // search
               geocode();
               return true;
           }
           return false;
       });
   }

    /**
     * Initializes or updates the marker
     * @param latLng
     */
    private void setMarkerPosition(LatLng latLng) {
       if(marker == null) {
           googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
           marker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
       } else {
           marker.setPosition(latLng);
       }
   }

    /**
     * Retrieves results from the geocoding server and adds them to the result list
     */
   private void geocode() {
        Log.d(TAG, "geocode");
        String searchString = geocoderSearchInput.getText().toString();
        Geocoder geocoder = new Geocoder(getActivity());
        try {
            adapter.clear();
            adapter.addAll(geocoder.getFromLocationName(searchString, 3));
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            Log.e(TAG, "geocode: IOException" + e.getMessage());
        }
   }



}
