package it.geosolutions.savemybike.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import it.geosolutions.savemybike.data.server.S3Manager;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;
import it.geosolutions.savemybike.ui.activity.SMBBaseActivity;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.adapters.SessionAdapter;
import it.geosolutions.savemybike.ui.callback.RecordingEventListener;
import it.geosolutions.savemybike.ui.tasks.DeleteSessionTask;
import it.geosolutions.savemybike.ui.tasks.InvalidateSessionsTask;
import it.geosolutions.savemybike.ui.tasks.UploadSessionTask;

/**
 * Created by Robert Oehler on 25.10.17.
 *
 * A fragment showing the stats of the session of the local database
 */

public class SessionsFragment extends Fragment implements RecordingEventListener {

    private final static String TAG = "SessionsFragment";

    private SessionAdapter adapter;

    @BindView(R.id.progress_layout) LinearLayout progress;
    @BindView(R.id.content_layout) LinearLayout content;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mySwipeRefreshLayout;
    @BindView(R.id.upload_button)  FloatingActionButton sendButton;
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
        updateListStatus(null);


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
                updateListStatus(null);
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
                    updateListStatus(null);
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

    /**
     * Hides the list and show a view when the items are uploading
     * or when there is a recording session active
     * @param state If present, it uses also this state to evaluate recording status
     *               This because the current status is not uploaded correctly
     *               TODO: improve this sistem to check the recording information once
     */
    private void updateListStatus(Session.SessionState state) {
        boolean showList = !S3Manager.isUploading() && !isRecording(state);
        // TODO: merge updateListStatus and showEmptyView
        boolean showRecording = isRecording(state) && adapter.getCount() > 0; // check empty view is not shown.


        boolean showUploading = S3Manager.isUploading() && !showRecording; // precedence to the recording view

        if(getActivity() != null) {
            View recordingView =  getActivity().findViewById(R.id.empty_sessions_recording);
            if (recordingView != null) {
                // show a page that explains this view is not available until recording session is ended
                recordingView.setVisibility(showRecording ?View.VISIBLE : View.GONE);
            }

            View uploadingView =  getActivity().findViewById(R.id.empty_sessions_uploading);
            if (uploadingView != null) {
                // show a notification that explains this view is not available until upload is ended
                uploadingView.setVisibility(showUploading ? View.VISIBLE : View.GONE);
            }
            // hide list and send button if needed
            listView.setVisibility(showList ? View.VISIBLE : View.GONE);
            // TODO: merge updateListStatus and showEmptyView
            sendButton.setVisibility(showList && adapter.getCount() > 0 ? View.VISIBLE : View.GONE);
        }

    }

    /**
     * Checks if the session is actually recording
     * @return
     */
    public boolean isRecording(Session.SessionState state) {
        Session s = ((SaveMyBikeActivity)getActivity()).getCurrentSession();
        return
                 s != null
                // state is not always available or updated.
                    && s.getState() == Session.SessionState.ACTIVE
                 // so if we have this value, it should be updated anyway
                || state != null
                    && state == Session.SessionState.ACTIVE;
    }

    @Override
    public void invalidateSessionStats(Session session) {

    }

    @Override
    public void selectVehicle(Vehicle vehicle) {

    }

    @Override
    public void invalidateUI(Vehicle currentVehicle) {
        updateListStatus(null);
    }

    @Override
    public void applySimulate(boolean simulate) {
    }

    @Override
    public void applySessionState(Session.SessionState state) {
        updateListStatus(state);
    }

    @Override
    public void stopRecording() {
        // need to show tracks when recording sessions is stopped
        updateListStatus(null);
        invalidateSessions();
    }
}
