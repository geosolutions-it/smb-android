
package it.geosolutions.savemybike.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import it.geosolutions.savemybike.R;

/**
 * Adapter for map popup to allow multiline text
 */
public class PopupAdapter implements InfoWindowAdapter {
    private View popup = null;
    private LayoutInflater inflater = null;

    public PopupAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
       return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = inflater.inflate(R.layout.popup, null);
        TextView title = (TextView) v.findViewById(R.id.popup_title);
        TextView subtitle = (TextView) v.findViewById(R.id.popup_snipplet);
        title.setText(marker.getTitle());
        subtitle.setText(marker.getSnippet());
        return v;
    }
}