package it.geosolutions.savemybike.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.Cost;
import it.geosolutions.savemybike.model.EmissionData;
import it.geosolutions.savemybike.model.HealthData;
import it.geosolutions.savemybike.model.Track;
import it.geosolutions.savemybike.ui.adapters.EmissionAdapter;
import it.geosolutions.savemybike.ui.adapters.IconDataAdapter;
import it.geosolutions.savemybike.ui.callback.OnFragmentInteractionListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * TrackDetailsFragment Show track details in the releated fragment
 *
 */
public class TrackDetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public TrackDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_track_details, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLoading(true);
        RetrofitClient client = RetrofitClient.getInstance(this.getContext());
        SMBRemoteServices portalServices = client.getPortalServices();
        portalServices.getTrack(79).enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                setLoading(false);
                Track t = response.body();
                if (t == null) {
                    showNoData();
                    return;
                }
                if(t.getHealth() != null) {
                    showHealthData(t.getHealth().get("totals"));
                }
                if(t.getVehicleTypes() != null) {
                    showVehicleTypes(t.getVehicleTypes());
                }
                if(t.getEmissions() != null) {
                    showEmissions(t.getEmissions().get("totals"));
                }
                if(t.getCosts() != null) {
                    showCosts(t.getCosts().get("totals"));
                }

            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                setLoading(false);
                showNoData();
            }
        });
    }

    private void showEmissions(EmissionData data) {
        if(data != null) {
            View header = getActivity().findViewById(R.id.emissions_header);
            if(header != null) {
                ((TextView)header.findViewById(R.id.name)).setText(getResources().getString(R.string.emissions));
                ((TextView)header.findViewById(R.id.value)).setText(getResources().getString(R.string.value));
                ((TextView)header.findViewById(R.id.saved)).setText(getResources().getString(R.string.saved));
            }
            ListView v = getActivity().findViewById(R.id.emissions_list);
            ArrayList<EmissionAdapter.EmissionEntry> entries = new ArrayList<>();
            entries.add( new EmissionAdapter.EmissionEntry("SO2", "mg",data.getSo2(), data.getSo2Saved()));
            entries.add( new EmissionAdapter.EmissionEntry("NOx", "mg",data.getNox(), data.getNoxSaved()));
            entries.add( new EmissionAdapter.EmissionEntry("CO", "mg",data.getCo(), data.getCoSaved()));
            entries.add( new EmissionAdapter.EmissionEntry("CO2", "g",data.getC02(), data.getCo2Saved()));
            entries.add( new EmissionAdapter.EmissionEntry("PM10", "mg",data.getPm10(), data.getPm10Saved()));
            v.setAdapter(new EmissionAdapter(getActivity(), R.layout.emission_entry, entries));
        }

    }


    public void showNoData() {

    }

    public void showHealthData(HealthData data) {
        if(data != null) {
            ListView v = getActivity().findViewById(R.id.health_data_list);
            ArrayList<IconDataAdapter.IconEntry> entries = new ArrayList<>();
            entries.add(new IconDataAdapter.IconEntry(
                    R.drawable.ic_fire,
                    getResources().getString(R.string.calories_consumed),
                    getResources().getString(R.string.calories_uom),
                    data.getCaloriesConsumed()
            ));
            entries.add(new IconDataAdapter.IconEntry(
                    R.drawable.ic_heart_pulse,
                    getResources().getString(R.string.benefit_index),
                    "",
                    data.getBenefitIndex()
            ));
            v.setAdapter(new IconDataAdapter(getActivity(), R.layout.icon_list_entry, entries));


        }
    }
    public void showCosts(Cost data) {
        if(data != null) {
            ListView v = getActivity().findViewById(R.id.costs_list);
            ArrayList<IconDataAdapter.IconEntry> entries = new ArrayList<>();
            entries.add(new IconDataAdapter.IconEntry(
                    R.drawable.ic_fuel,
                    getResources().getString(R.string.fuel),
                    getResources().getString(R.string.costs_uom),
                    data.getFuelCost()
            ));
            entries.add(new IconDataAdapter.IconEntry(
                    R.drawable.ic_directions_car,
                    getResources().getString(R.string.c_depreciation),
                    getResources().getString(R.string.costs_uom),
                    data.getDepreciation_cost()
            ));
            entries.add(new IconDataAdapter.IconEntry(
                    R.drawable.ic_trending_top,
                    getResources().getString(R.string.c_operation),
                    getResources().getString(R.string.costs_uom),
                    data.getFuelCost()
            ));
            entries.add(new IconDataAdapter.IconEntry(
                    R.drawable.ic_euro,
                    getResources().getString(R.string.total),
                    getResources().getString(R.string.costs_uom),
                    data.getFuelCost()
            ));

            v.setAdapter(new IconDataAdapter(getActivity(), R.layout.icon_list_entry, entries));


        }
    }

    public void showVehicleTypes(ArrayList<String> vehicles) {
        for (String vehicle : vehicles) {
            switch (vehicle) {
                case "walk":
                    getActivity().findViewById(R.id.icon_walk).setVisibility(View.VISIBLE);
                    break;
                case "bike":
                    getActivity().findViewById(R.id.icon_bike).setVisibility(View.VISIBLE);
                    break;
                case "motocycle":
                    getActivity().findViewById(R.id.icon_motorcycle).setVisibility(View.VISIBLE);
                    break;
                case "car":
                    getActivity().findViewById(R.id.icon_car).setVisibility(View.VISIBLE);
                    break;
                case "bus":
                    getActivity().findViewById(R.id.icon_bus).setVisibility(View.VISIBLE);
                    break;
                case "train":
                    getActivity().findViewById(R.id.icon_train).setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
    public void setLoading(boolean loading) {
        View v = getActivity().findViewById(R.id.loading_container);
        if(v != null) {
            v.setVisibility(loading ? View.VISIBLE : View.GONE);
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
}

