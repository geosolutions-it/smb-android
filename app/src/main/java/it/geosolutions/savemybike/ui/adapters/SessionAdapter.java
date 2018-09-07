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
            // final TextView dataTV = view.findViewById(R.id.data_value);
            final TextView dateTV = view.findViewById(R.id.session_start_datetime);
            final TextView durationTV = view.findViewById(R.id.session_duration_text);

            if (((SaveMyBikeActivity) getContext()).getConfiguration().metric) {
                distanceTV.setText(String.format(Locale.US, "%.1f %s", (session.getDistance() / 1000f), Constants.UNIT_KM));
            } else {
                distanceTV.setText(String.format(Locale.US, "%.1f %s", (session.getDistance() / 1000f) * Constants.KM_TO_MILES, Constants.UNIT_MI));
            }

            dateTV.setText(DateTimeFormat.forPattern("dd MMM, 'ore' HH:mm").print(session.getStartingTime()));
            long millis = Math.round(60000 * session.getOverallTime());
            Duration duration = new Duration(millis);
            DateTime date = new DateTime(0, 1, 1, 0, 0, 0, 0);
            durationTV.setText(DateTimeFormat.forPattern("HH:mm:ss").print(date.plus(duration)));
            view.setTag(session.getId());
        }

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        // disable all the items
        return false;
    }
}