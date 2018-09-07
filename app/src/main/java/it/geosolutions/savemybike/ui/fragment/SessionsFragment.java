package it.geosolutions.savemybike.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.ui.adapters.SessionAdapter;
import it.geosolutions.savemybike.ui.tasks.InvalidateSessionsTask;

/**
 * Created by Robert Oehler on 25.10.17.
 *
 * A fragment showing the stats of the session of the local database
 */

public class SessionsFragment extends Fragment {

    private final static String TAG = "SessionsFragment";

    private SessionAdapter adapter;

    @BindView(R.id.progress_layout) LinearLayout progress;
    @BindView(R.id.content_layout) LinearLayout content;

    @BindView(R.id.sessions_list) ListView listView;

    /**
     * inflate and setup the view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sessions, container,false);
        ButterKnife.bind(this, view);

        adapter = new SessionAdapter(getActivity(), R.layout.item_track, new ArrayList<>());
        listView.setAdapter(adapter);
        /* not clickable
        listView.setOnItemClickListener((parent, itemView, position, id) -> {


            Intent intent = new Intent(getActivity(), TrackDetailsActivity.class);

            intent.putExtra(TrackDetailsActivity.TRACK_ID, (Long) itemView.getTag());

            getActivity().startActivity(intent);
        });
        */
        // TODO: show also sessions, grayed out
        invalidateSessions();

        return view;
    }

    /**
     * loads the locally available sessions from the database and invalidates the UI
     */
    private void invalidateSessions() {

        new InvalidateSessionsTask(getActivity(), new InvalidateSessionsTask.InvalidateSessionsCallback() {
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
                adapter.addAll(sessions);
                adapter.notifyDataSetChanged();
            }
        }).execute();
    }



    /**
     * Switches the UI of this screen to show either the progress UI or the content
     * @param show if true shows the progress UI and hides content, if false the other way around
     */
    private void showProgress(final boolean show) {

        if(isAdded()) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            if(progress != null && content != null) {
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

    }
}
