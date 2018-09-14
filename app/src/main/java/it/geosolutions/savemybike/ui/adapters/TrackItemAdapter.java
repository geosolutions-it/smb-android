package it.geosolutions.savemybike.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    public TrackItemAdapter(final Context context, int textViewResourceId, List<TrackItem> sessions){
        super(context, textViewResourceId, sessions);

        resource = textViewResourceId;
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

        final TrackItem track = getItem(position);
        if(track != null) {

            TrackDetailsActivity.inflateTrackDataToRecordView(track, view);
            view.setTag(track.getId());
        }

        return view;
    }
}