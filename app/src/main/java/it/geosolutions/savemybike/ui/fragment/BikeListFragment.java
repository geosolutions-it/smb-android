package it.geosolutions.savemybike.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;

/**
 * Created by Robert Oehler on 26.10.17.
 *
 * A fragment showing a list of bikes
 */

public class BikeListFragment extends Fragment {

    @BindView(R.id.bikes_list) ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_bikes, container, false);
        ButterKnife.bind(this, view);

        final List<Bike> bikes = ((SaveMyBikeActivity) getActivity()).getBikes();

        final BikeAdapter bikeAdapter = new BikeAdapter(getActivity(), R.layout.item_bike, bikes);

        listView.setAdapter(bikeAdapter);

        return view;
    }


    @OnClick(R.id.add_bike_button)
    public void onClick() {
        Toast.makeText(getActivity(), "Todo : add another bike", Toast.LENGTH_SHORT).show();
    }

    /**
     * adapter for bikes
     */
    private class BikeAdapter extends ArrayAdapter<Bike>{

        private	int resource;

        private BikeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Bike> objects) {
            super(context, resource, objects);

            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            RelativeLayout view;

            if(convertView == null){
                view = new RelativeLayout(getContext());
                LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                li.inflate(resource, view,true);
            }else{
                view = (RelativeLayout) convertView;
            }

            final Bike bike = getItem(position);

            if(bike != null) {
                final TextView titleTv = view.findViewById(R.id.bike_title);
                final ImageView imageView = view.findViewById(R.id.bike_image);

                if (bike.getNickname() != null) {
                    titleTv.setText(bike.getNickname());
                }else{
                    titleTv.setText("");
                }

                if(bike.getPictures() != null && !bike.getPictures().isEmpty()){
                    Glide.with(getContext()).load(bike.getPictures().get(0)).into(imageView);
                }else{
                    imageView.setImageResource(R.drawable.footer_bikes_off);
                }

                final FloatingActionButton alarmButton = view.findViewById(R.id.bike_alarm);
                alarmButton.setOnClickListener(view1 -> {
                    Toast.makeText(getContext(), "Changing " + bike.getNickname() + "  status", Toast.LENGTH_LONG).show();
                    ((SaveMyBikeActivity)getActivity()).updateBikeStatus(bike);
                });

                if(bike.getCurrentStatus() != null && bike.getCurrentStatus().getLost()){
                    alarmButton.setImageResource(R.drawable.ic_lock_open_red_24dp);
                }else{
                    alarmButton.setImageResource(R.drawable.ic_lock_outline_green_24dp);
                }
            }
            return view;
        }
    }
}
