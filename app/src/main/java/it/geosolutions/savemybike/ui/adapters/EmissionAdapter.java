package it.geosolutions.savemybike.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.utils.UOMUtils;

public class EmissionAdapter extends ArrayAdapter<EmissionAdapter.EmissionEntry> {
    public static class EmissionEntry {
        public String name;
        public String uom;
        public Double value;
        public Double saved;
        public EmissionEntry(String name, String uom, Double value, Double saved) {
            this.name = name;
            this.uom = uom;
            this.value = value;
            this.saved = saved;
        }
    }

    public EmissionAdapter(@NonNull Context context, int viewId,
                         ArrayList<EmissionAdapter.EmissionEntry> objects) {
        super(context, viewId, objects);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.emission_entry, null);
        TextView name = convertView.findViewById(R.id.name);
        TextView value = convertView.findViewById(R.id.value);
        TextView saved = convertView.findViewById(R.id.saved);
        EmissionAdapter.EmissionEntry e = getItem(position);
        name.setText(e.name);
        if(e.value != null) {
            DecimalFormat df2 = new DecimalFormat();
            value.setText(UOMUtils.format(e.value, "###") + e.uom);
        }
        if(e.saved != null) {

            saved.setText(UOMUtils.format(e.saved, "###") + e.uom);
        }

        return convertView;
    }

}