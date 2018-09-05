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

import org.joda.time.format.DateTimeFormat;

import java.util.List;
import java.util.Locale;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;

/**
 * adapter for sessions
 */
public class SessionAdapter extends ArrayAdapter<Session> {

    private	int resource;

    public SessionAdapter(final Context context, int textViewResourceId, List<Session> sessions){
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

        final Session session = getItem(position);
        if(session != null) {

            final TextView distanceTV = view.findViewById(R.id.dist_value);
            final TextView dataTV = view.findViewById(R.id.data_value);
            final TextView dateTV = view.findViewById(R.id.session_start_datetime);
            final TextView durationTV = view.findViewById(R.id.session_duration_text);

            if (((SaveMyBikeActivity) getContext()).getConfiguration().metric) {
                distanceTV.setText(String.format(Locale.US, "%.1f %s", (session.getDistance() / 1000f), Constants.UNIT_KM));
            } else {
                distanceTV.setText(String.format(Locale.US, "%.1f %s", (session.getDistance() / 1000f) * Constants.KM_TO_MILES, Constants.UNIT_MI));
            }
            dataTV.setText(String.format(Locale.US, "%d", session.getDataPoints().size()));
            dateTV.setText(DateTimeFormat.forPattern("dd MMM, 'ore' HH:mm").print(session.getStartingTime()));
            durationTV.setText(DateTimeFormat.forPattern("HH:mm:ss").print(session.getOverallTime()));
            view.setTag(session.getId());
        }

        return view;
    }
}