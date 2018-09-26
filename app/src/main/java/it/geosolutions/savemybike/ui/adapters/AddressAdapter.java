package it.geosolutions.savemybike.ui.adapters;

import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Badge;

public abstract class AddressAdapter  extends ArrayAdapter<Address> {
    private final int resource;

    public AddressAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Address> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    static class ViewHolder {
        @BindView(R.id.location_title) TextView title;
        @BindView(R.id.address_text_view) View item;
        @BindView(R.id.location_icon) View icon;
        @BindView(R.id.location_description) TextView description;
        @BindView(R.id.use_text) View useTextButton;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        AddressAdapter.ViewHolder holder;
        if (view != null) {
            holder = (AddressAdapter.ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new AddressAdapter.ViewHolder(view);
            view.setTag(holder);
        }
        Address address = getItem(position);
        int max = address.getMaxAddressLineIndex();
        String currentAddress = "";
        if (max >= 0) {
            for (int i=0; i <= max ;i++) {
                currentAddress += address.getAddressLine(i) + " ";
            }
        }
        if(address.getPostalCode() != null) {
            String[] parts = currentAddress.split(address.getPostalCode());

            // TODO: improve how to show address
            if (parts != null && parts.length > 1 && parts[0].length() > 0) {
                holder.title.setText(parts[0]);
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(parts.length > 1 ? parts[1] : parts[0]);
            } else {
                holder.title.setText(currentAddress);
                holder.description.setVisibility(View.GONE);
            }
        } else {
            holder.title.setText(currentAddress);
            holder.description.setVisibility(View.GONE);
        }

        holder.useTextButton.setOnClickListener((View v) -> {
            useItemTextHandler(getItem(position));
        });
        holder.item.setOnClickListener((View v) -> {
            itemClickHandler(getItem(position));
        });
        holder.icon.setOnClickListener((View v) -> {
            itemClickHandler(getItem(position));
        });
        return view;
    }

    public String getAddressString(Address item) {
        int max = item.getMaxAddressLineIndex();
        String currentAddress = "";
        if (max!=-1) {
            for (int i=0; i<=max ; i++)
                currentAddress += item.getAddressLine(i) + " ";
        }
        return currentAddress;
    }

    protected abstract void useItemTextHandler(Address item);

    protected abstract void itemClickHandler(Address item);
}
