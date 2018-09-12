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

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

/**
 * @Author Lorenzo Natali
 * adapter for sessions list.
 */
public abstract class SessionAdapter extends ArrayAdapter<Session> {

    private	int resource;
    private ViewBinderHelper binderHelper;
    public SessionAdapter(final Context context, int textViewResourceId, List<Session> sessions){
        super(context, textViewResourceId, sessions);

        resource = textViewResourceId;
        binderHelper = new ViewBinderHelper();
        binderHelper.setOpenOnlyOne(true);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        RelativeLayout view;

        final Session session = getItem(position);
        if(convertView == null){
            view = new RelativeLayout(getContext());
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            li.inflate(resource, view,true);
            setupSwipe(view, session);
        }else{
            view = (RelativeLayout) convertView;
        }


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
            long millis = Math.round(session.getOverallTime());
            Duration duration = new Duration(millis);
            DateTime date = new DateTime(0, 1, 1, 0, 0, 0, 0);
            durationTV.setText(DateTimeFormat.forPattern("HH:mm:ss").print(date.plus(duration)));
            view.setTag(session.getId());
            if(session.isUploaded()) {
                view.findViewById(R.id.uploaded).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.uploaded).setVisibility(View.GONE);
            }
        }

        return view;
    }

    /**
     * Setup the swipable item
     * @param convertView
     * @param item
     */
    void setupSwipe(View convertView, Session item) {
        String id = item.getId() + "";
        SwipeRevealLayout layout = (SwipeRevealLayout)convertView.findViewById(R.id.swipe_layout);
        binderHelper.bind(layout, id);

        if(item.isUploaded()) {
            binderHelper.lockSwipe(id);
        } else {
            binderHelper.unlockSwipe(id);
        }
        if (convertView != null && item != null) {
            View deleteView = convertView.findViewById(R.id.delete_layout);
            deleteView.setOnClickListener((view) -> {

                onDelete(item);

                SwipeRevealLayout l = (SwipeRevealLayout)convertView.findViewById(R.id.swipe_layout);

                l.close(false);
            });
        }
    }

    @Override
    public boolean isEnabled(int position) {
        // disable all the items
        return false;
    }

    public abstract void onDelete(Session s);
}