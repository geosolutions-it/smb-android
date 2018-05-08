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

import java.util.ArrayList;
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

        final ArrayList<Bike> bikes = ((SaveMyBikeActivity) getActivity()).getConfiguration().bikes;

        final BikeAdapter bikeAdapter = new BikeAdapter(getActivity(), R.layout.item_bike, bikes);

        listView.setAdapter(bikeAdapter);

        return view;
    }


    @OnClick(R.id.add_bike_button)
    public void onClick(View view) {
        Toast.makeText(getActivity(), "Todo : add another bike", Toast.LENGTH_SHORT).show();
    }

    /**
     * adapter for bikes
     */
    private class BikeAdapter extends ArrayAdapter<Bike>{

        private	int resource;

        public BikeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Bike> objects) {
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

            final TextView titleTv = view.findViewById(R.id.bike_title);
            final ImageView imageView = view.findViewById(R.id.bike_image);

            if(bike.getName() != null){
                titleTv.setText(bike.getName());
            }

            final FloatingActionButton alarmButton = view.findViewById(R.id.bike_alarm);
            alarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getContext(),"Todo : ask if bike "+bike.getName()+" should be signaled as stolen", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }
}
