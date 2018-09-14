package it.geosolutions.savemybike.ui;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

/**
 * adapter for bikes
 */
public class BikeAdapter extends ArrayAdapter<Bike> {

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

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(R.string.lost_bike_report_title);
                if(bike.getCurrentStatus().getLost()) {
                    builder.setMessage(R.string.found_bike_message);
                    builder.setPositiveButton(R.string.send, (dialog, which) -> {
                        // smbActivity.updateBikeStatus(bike, "");
                        dialog.dismiss();
                    });
                }else {
                    LayoutInflater li = LayoutInflater.from(getContext());
                    View promptsView = li.inflate(R.layout.prompt, null);
                    builder.setView(promptsView);
                    final EditText userInput = promptsView.findViewById(R.id.editTextDialogUserInput);

                    SupportMapFragment mapFragment = (SupportMapFragment) smbActivity.getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(new MapCallback(smbActivity));


                    builder.setMessage(R.string.lost_bike_message);
                    builder.setPositiveButton(R.string.send, (dialog, which) -> {
                        // smbActivity.updateBikeStatus(bike, userInput.getText().toString());
                        dialog.dismiss();
                    });
                }

                builder.setNegativeButton(R.string.cancel, null);
                builder.show();

            });

            if (bike.getCurrentStatus() != null && bike.getCurrentStatus().getLost()) {
                alarmButton.setImageResource(R.drawable.ic_lock_open_red_24dp);
            } else {
                alarmButton.setImageResource(R.drawable.ic_lock_outline_green_24dp);
            }
        }
        return view;
    }
}
