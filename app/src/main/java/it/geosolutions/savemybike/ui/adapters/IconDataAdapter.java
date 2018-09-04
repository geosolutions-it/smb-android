package it.geosolutions.savemybike.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.geosolutions.savemybike.R;

public class IconDataAdapter extends ArrayAdapter<IconDataAdapter.IconEntry> {
    public static class IconEntry {
        public int icon;
        public String name;
        public String uom;
        public Double value;
        public IconEntry(int icon, String name, String uom, Double value) {
            this.icon = icon;
            this.name = name;
            this.uom = uom;
            this.value = value;
        }
    }

    public IconDataAdapter(@NonNull Context context, int viewId,
                           ArrayList<IconEntry> objects) {
        super(context, viewId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.icon_list_entry, null);
        ImageView icon = convertView.findViewById(R.id.inner_icon);
        TextView name = convertView.findViewById(R.id.name);
        TextView value = convertView.findViewById(R.id.value);
        TextView uom = convertView.findViewById(R.id.uom);
        IconEntry e = getItem(position);
        icon.setImageResource(e.icon);
        name.setText(e.name);
        if(e.value != null) {
            DecimalFormat df2 = new DecimalFormat();
            value.setText(df2.format(e.value));
        }
        uom.setText(e.uom);
        return convertView;
    }
}