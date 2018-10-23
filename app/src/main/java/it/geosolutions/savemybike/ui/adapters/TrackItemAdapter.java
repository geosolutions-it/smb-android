package it.geosolutions.savemybike.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Segment;
import it.geosolutions.savemybike.model.Track;
import it.geosolutions.savemybike.model.TrackItem;
import it.geosolutions.savemybike.ui.activity.SMBBaseActivity;
import it.geosolutions.savemybike.ui.activity.TrackDetailsActivity;

/**
 * adapter for sessions
 */
public class TrackItemAdapter extends ArrayAdapter<TrackItem> {

    private	int resource;
    static class ViewHolder {
        @BindView(R.id.status) ImageView status;
        @BindView(R.id.km_view) TextView kmView;
        @BindView(R.id.dist_value) TextView distValue;
        @BindView(R.id.invalid_message) TextView invalidMessage;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public TrackItemAdapter(final Context context, int textViewResourceId, List<TrackItem> sessions){
        super(context, textViewResourceId, sessions);

        resource = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TrackItemAdapter.ViewHolder holder;
        ConstraintLayout view;
        if (convertView != null) {
            holder = (TrackItemAdapter.ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
            holder = new TrackItemAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if(convertView == null){
            view = new ConstraintLayout(getContext());

            LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            li.inflate(resource, view,true);
        }else{
            view = (ConstraintLayout) convertView;
        }

        final TrackItem track = getItem(position);
        if(track != null) {
            TrackDetailsActivity.inflateTrackDataToRecordView(track, view);
            if(track.isValid()) {
                holder.status.setVisibility(View.GONE);
                holder.kmView.setVisibility(View.VISIBLE);
                holder.distValue.setVisibility(View.VISIBLE);
                holder.invalidMessage.setVisibility(View.GONE);
            } else {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setImageResource(R.drawable.ic_error);
                holder.status.setColorFilter(getContext().getResources().getColor(R.color.red));
                holder.kmView.setVisibility(View.GONE);
                holder.distValue.setVisibility(View.GONE);
                holder.invalidMessage.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }
}