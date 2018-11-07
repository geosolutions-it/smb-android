package it.geosolutions.savemybike.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Observation;

public abstract class ObservationAdapter extends ArrayAdapter<Observation> {

    private int resource;
    public ObservationAdapter(@NonNull Context context, int viewId,
                              ArrayList<Observation> objects) {
        super(context, viewId, objects);
        resource = viewId;
    }
    static class ViewHolder {
        @BindView(R.id.observed_at) TextView observedAt;
        @BindView(R.id.observation_address) TextView observedAddress;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObservationAdapter.ViewHolder holder;
        ViewGroup view;
        if (convertView != null) {
            holder = (ObservationAdapter.ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
            holder = new ObservationAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if(convertView == null){
            view = new ConstraintLayout(getContext());
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            li.inflate(resource, view,true);
        }else{
            view = (ConstraintLayout) convertView;
        }
        Observation o = getItem(position);
        if(o != null) {
            holder.observedAt.setText(o.observedAt);
            holder.observedAddress.setText(o.address);
        }
        if(isSelected(o)) {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
        } else {
            view.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }
        String dateFormatted = DateTimeFormat.forPattern("dd MMM, 'ore' HH:mm").print(new DateTime((o.observedAt)));
        holder.observedAt.setText(dateFormatted);

        return convertView;
    }

    public abstract boolean isSelected(Observation o);
}