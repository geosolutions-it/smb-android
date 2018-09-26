package it.geosolutions.savemybike.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

import it.geosolutions.savemybike.GlideApp;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.fragment.BikeLostNotificationFragment;

/**
 * adapter for bikes
 */
public abstract class BikeAdapter extends ArrayAdapter<Bike> {

    private SaveMyBikeActivity smbActivity;
    private int resource;

    public BikeAdapter(@NonNull SaveMyBikeActivity smbActivity, @LayoutRes int resource, @NonNull List<Bike> objects) {
        super(smbActivity, resource, objects);
        this.smbActivity = smbActivity;

        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        RelativeLayout view;

        if (convertView == null) {
            view = new RelativeLayout(getContext());
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            li.inflate(resource, view, true);
        } else {
            view = (RelativeLayout) convertView;
        }

        final Bike bike = getItem(position);

        if (bike != null) {
            final TextView titleTv = view.findViewById(R.id.bike_title);
            final ImageView imageView = view.findViewById(R.id.bike_image);

            if (bike.getNickname() != null) {
                titleTv.setText(bike.getNickname());
            } else {
                titleTv.setText("");
            }

            if (bike.getPictures() != null && !bike.getPictures().isEmpty()) {
                GlideApp
                        .with(getContext())
                        .load(bike.getPictures().get(0))
                        .placeholder(R.drawable.footer_bikes_off)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.footer_bikes_off);
            }

            final FloatingActionButton alarmButton = view.findViewById(R.id.bike_alarm);
            alarmButton.setOnClickListener(view1 -> {
                if(bike.getCurrentStatus().getLost()) {
                    new AlertDialog.Builder(getContext())
                            .setMessage(R.string.confirm_bike_found)
                            .setPositiveButton(R.string.confirm, ( dialog,  id) -> {
                                updateStatus(bike, null);
                            })
                            .setNegativeButton(R.string.cancel, ( dialog,  id) -> {
                                // TODO: close
                            }).show();

                } else {
                    // show bike lost notification form
                    Fragment f = new BikeLostNotificationFragment();
                    Bundle b = new Bundle();
                    b.putSerializable(BikeLostNotificationFragment.BIKE_ARGUMENT, bike);
                    f.setArguments(b);
                    smbActivity.changeFragment(f);
                }



            });
            if (bike.getCurrentStatus() != null && bike.getCurrentStatus().getLost()) {
                alarmButton.setImageResource(R.drawable.ic_lock_open_red_24dp);
            } else {
                alarmButton.setImageResource(R.drawable.ic_lock_outline_green_24dp);
            }
        }
        return view;
    }
    public abstract void updateStatus(Bike bike, String details);
}
