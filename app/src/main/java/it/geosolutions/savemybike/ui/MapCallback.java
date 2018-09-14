package it.geosolutions.savemybike.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by Lorenzo Pini on 26/07/2018.
 */
public class MapCallback implements OnMapReadyCallback {
    private Context ctx;

    MapCallback(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(ctx, "Ready", Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            Log.v("MAP_CALLBACK", "setting my location");
        }else{
            Log.v("MAP_CALLBACK", "NOT setting my location");
        }

        googleMap.setOnMapClickListener(latLng -> Toast.makeText(ctx, "Cliccato: " + latLng.latitude + " , "+ latLng.longitude, Toast.LENGTH_LONG).show());
    }
}
