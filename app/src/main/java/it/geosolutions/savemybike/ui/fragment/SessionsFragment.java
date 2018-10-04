package it.geosolutions.savemybike.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.ui.activity.SMBBaseActivity;
import it.geosolutions.savemybike.ui.activity.TrackDetailsActivity;
import it.geosolutions.savemybike.ui.adapters.SessionAdapter;
import it.geosolutions.savemybike.ui.tasks.DeleteSessionTask;
import it.geosolutions.savemybike.ui.tasks.InvalidateSessionsTask;
import it.geosolutions.savemybike.ui.tasks.UploadSessionTask;

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
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mySwipeRefreshLayout;

    @BindView(R.id.sessions_list) ListView listView;

    /**
     * inflate and setup the view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sessions, container,false);
        ButterKnife.bind(this, view);

        adapter = new SessionAdapter(getActivity(), R.layout.swipable_session_item, new ArrayList<>()) {
            @Override
            public void onDelete(Session s) {
                deleteSession(s);
            }
        };
        listView.setAdapter(adapter);
        view.findViewById(R.id.upload_button).setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View p0) {
                startUpload();
            }
        });
        mySwipeRefreshLayout.setOnRefreshListener(() -> invalidateSessions());


        // TODO: show also sessions, grayed out
        invalidateSessions();

        return view;
    }



    /**
     * loads the locally available sessions from the database and invalidates the UI
     */
    public void invalidateSessions() {

        new InvalidateSessionsTask(getActivity(), new InvalidateSessionsTask.InvalidateSessionsCallback() {
            @Override
            public void showProgressView() {
                showProgress(true);
                showEmpty(false);
                if(mySwipeRefreshLayout != null) {
                    mySwipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void hideProgressView() {
                showProgress(false);
                if(mySwipeRefreshLayout != null ) {
                    mySwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void done(ArrayList<Session> sessions) {
                adapter.clear();
                adapter.addAll(sessions);
                showEmpty(sessions.size() == 0);
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
    public void startUpload() {

        SMBBaseActivity activity = (SMBBaseActivity) getActivity();
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.permissionNecessary(Manifest.permission.WRITE_EXTERNAL_STORAGE, SMBBaseActivity.PermissionIntent.SD_CARD))) {

            new UploadSessionTask(getContext(), new UploadSessionTask.SessionCallback() {
                @Override
                public void showProgressView() {
                    showProgress(true);
                }

                @Override
                public void hideProgressView() {
                    showProgress(false);
                }

                @Override
                public void done(boolean success) {

                }
            }, false).execute();

        }

    }
    public void deleteSession(Session s) {
        new DeleteSessionTask(getContext(), new DeleteSessionTask.DeleteSessionCallback() {
            @Override
            public void showProgressView() {
                // TODO show progress delete
            }

            @Override
            public void hideProgressView() {
                // TODO hide progress delete
            }

            @Override
            public void done(Boolean res) {
                invalidateSessions();
            }
        }, s).execute();
    }
    private void showEmpty(boolean show) {
        if(getActivity() != null) {
            View v =  getActivity().findViewById(R.id.empty_sessions);
            if (v != null) {
                v.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }
}
