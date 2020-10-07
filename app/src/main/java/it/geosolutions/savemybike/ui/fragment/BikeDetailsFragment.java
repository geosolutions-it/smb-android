package it.geosolutions.savemybike.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.Observation;
import it.geosolutions.savemybike.ui.activity.BikeDetailsActivity;
import it.geosolutions.savemybike.ui.adapters.ObservationAdapter;
import it.geosolutions.savemybike.ui.callback.OnFragmentInteractionListener;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * TrackDetailsFragment Show track details in the releated fragment
 *
 */
public class BikeDetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    @BindView(R.id.observation_list) ListView observationList;
    ArrayList<Observation> observations = new ArrayList<>();
    ObservationAdapter observationAdapter;
    Bike bike;
    public EventCallbacks callbacks;
    private Observation selectedItem;

    public void setSelected(String selected) {
        for(int i = 0; i < observations.size(); i++) {
            Observation o = observations.get(i);
            if(o.getId() != null && o.getId().equals(selected)) {
                selectedItem = o;
                observationList.setItemChecked(i, true);
                observationAdapter.notifyDataSetChanged();
            }
        }

    }

    public interface EventCallbacks {
        public void onSelect(Observation o);
    };

    public BikeDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         observationAdapter = new ObservationAdapter(getContext(), R.layout.item_observation, observations) {
             public boolean isSelected(Observation o) {return o == selectedItem;}
         };
    }

    public void setEventsCallbacks(EventCallbacks callbacks) {
        this.callbacks = callbacks;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bike_details, container, false);
        ButterKnife.bind(this, view);
        bike = (Bike) getActivity().getIntent().getExtras().get(BikeDetailsActivity.BIKE);
        observationList.setAdapter(observationAdapter);
        observationList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        observationList.setOnItemClickListener(((parent, view1, position, id) -> {
            selectedItem = (Observation) parent.getItemAtPosition(position);
            if(callbacks != null) {
                callbacks.onSelect((Observation) parent.getItemAtPosition(position));
            }
        }));
        observationAdapter.clear();
        observationAdapter.addAll(observations);
        observationAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    public void setLoading(boolean loading) {
        if(getActivity() != null) {
            View v = getActivity().findViewById(R.id.loading_container);
            if(v != null) {
                v.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateListData(JSONObject geojson) {

        try{
        JSONArray jArray = geojson.getJSONArray("features");

        observations.clear();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){

                observations.add(new Observation(jArray.getJSONObject(i)));
            }
        }
        } catch (JSONException e) {
            Log.e("BikeDetailsFragment", "can not cast observations");
        }
        if(observationAdapter != null) {
            observationAdapter.notifyDataSetChanged();
        }


    }
}

