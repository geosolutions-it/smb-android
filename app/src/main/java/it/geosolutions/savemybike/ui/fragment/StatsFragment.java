package it.geosolutions.savemybike.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.Util;
import it.geosolutions.savemybike.data.db.SMBDatabase;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.activity.TrackDetailsActivity;

/**
 * Created by Robert Oehler on 25.10.17.
 *
 * A fragment showing the stats of the session of the local database
 */

public class StatsFragment extends Fragment {

    private final static String TAG = "StatsFragment";

    private SessionAdapter adapter;

    @BindView(R.id.distance_overall) TextView overallDistanceTV;
    @BindView(R.id.time_overall) TextView overallTimeTV;
    @BindView(R.id.elev_overall) TextView overallElevTV;

    @BindView(R.id.progress_layout) LinearLayout progress;
    @BindView(R.id.content_layout) LinearLayout content;

    @BindView(R.id.sessions_list) ListView listView;

    /**
     * inflate and setup the view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats, container,false);
        ButterKnife.bind(this, view);

        adapter = new SessionAdapter(getActivity(), R.layout.item_session, new ArrayList<>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, itemView, position, id) -> {


            Intent intent = new Intent(getActivity(), TrackDetailsActivity.class);

            intent.putExtra(TrackDetailsActivity.TRACK_ID, (Long) itemView.getTag());

            /* TODO: animation transition. Something like this...
            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // create the transition animation - the images in the layouts
                // of both activities are defined with android:transitionName="track-details-open"
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(this, androidRobotView, "track-details-open");
                getActivity().startActivity(intent, options.toBundle());
            } else {
                getActivity().startActivity(intent);
            }*/
            getActivity().startActivity(intent);


        }
        );
        invalidateSessions();

        return view;
    }

    /**
     * loads the locally available sessions from the database and invalidates the UI
     */
    private void invalidateSessions() {

        new InvalidateSessionsTask(getActivity(), new InvalidateSessionsCallback() {
            @Override
            public void showProgressView() {

                showProgress(true);
            }

            @Override
            public void hideProgressView() {

                showProgress(false);
            }

            @Override
            public void done(ArrayList<Session> sessions) {

                double dist = 0, elev = 0;
                long time = 0;

                for(Session session : sessions){
                    dist += session.getDistance();
                    time += session.getOverallTime();
                    elev += session.getOverallElevation();
                }

                if(((SaveMyBikeActivity)getActivity()).getConfiguration().metric) {
                    overallDistanceTV.setText(String.format(Locale.US, "%.1f %s", dist / 1000f, Constants.UNIT_KM));
                    overallElevTV.setText(String.format(Locale.US, "%.0f %s", elev, Constants.UNIT_M));
                }else{
                    overallDistanceTV.setText(String.format(Locale.US, "%.1f %s", dist / 1000f * Constants.KM_TO_MILES, Constants.UNIT_MI));
                    overallElevTV.setText(String.format(Locale.US, "%.0f %s", elev * Constants.METER_TO_FEET, Constants.UNIT_FT));
                }
                overallTimeTV.setText(Util.longToTimeString(time));

                adapter.addAll(sessions);
                adapter.notifyDataSetChanged();
            }
        }).execute();
    }

    /**
     * a task to load all sessions from the local database
     */
    static class InvalidateSessionsTask extends AsyncTask<Void,Void,ArrayList<Session>>{

        private WeakReference<Context> contextRef;
        private InvalidateSessionsCallback callback;

        public InvalidateSessionsTask(final Context context, final InvalidateSessionsCallback pCallback){

            this.contextRef = new WeakReference<>(context);
            this.callback = pCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(callback != null) {
                callback.showProgressView();
            }
        }

        @Override
        protected ArrayList<Session> doInBackground(Void... voids) {

            ArrayList<Session> sessions = null;
            final SMBDatabase database = new SMBDatabase(contextRef.get());
            try{

                database.open();
                sessions = database.getAllSessions();

            }finally {
                database.close();
            }

            return sessions;
        }

        @Override
        protected void onPostExecute(ArrayList<Session> sessions) {
            super.onPostExecute(sessions);

            if(callback != null) {
                callback.hideProgressView();
                callback.done(sessions);
            }
        }
    }

    interface InvalidateSessionsCallback
    {
        void showProgressView();
        void hideProgressView();
        void done(ArrayList<Session> sessions);
    }

    /**
     * Switches the UI of this screen to show either the progress UI or the content
     * @param show if true shows the progress UI and hides content, if false the other way around
     */
    private void showProgress(final boolean show) {

        if(isAdded()) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progress.setVisibility(View.VISIBLE);
            progress.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progress.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            content.setVisibility(View.VISIBLE);
            content.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            content.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });

        }

    }

    /**
     * adapter for sessions
     */
    private class SessionAdapter extends ArrayAdapter<Session> {

        private	int resource;

        SessionAdapter(final Context context, int textViewResourceId, List<Session> sessions){
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

                if (((SaveMyBikeActivity) getActivity()).getConfiguration().metric) {
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
}
