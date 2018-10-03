package it.geosolutions.savemybike.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.PaginatedResult;
import it.geosolutions.savemybike.model.TrackItem;
import it.geosolutions.savemybike.ui.activity.TrackDetailsActivity;
import it.geosolutions.savemybike.ui.adapters.TrackItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Robert Oehler on 25.10.17.
 *
 * A fragment showing the stats of the session of the local database
 */

public class TracksFragment extends Fragment {

    private final static String TAG = "TracksFragment";

    private TrackItemAdapter adapter;

    @BindView(R.id.progress_layout) LinearLayout progress;
    @BindView(R.id.content_layout) LinearLayout content;

    @BindView(R.id.tracks_list) ListView listView;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mySwipeRefreshLayout;
    /**
     * inflate and setup the view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tracks, container,false);
        ButterKnife.bind(this, view);
        adapter = new TrackItemAdapter(getActivity(), R.layout.item_track, new ArrayList<>());
        listView.setAdapter(adapter);

        mySwipeRefreshLayout.setOnRefreshListener(() -> getTracks());
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
            getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        });
        // TODO: show also sessions, grayed out
        getTracks();

        return view;
    }

    /**
     * loads the locally available sessions from the database and invalidates the UI
     */
    private void getTracks() {
        RetrofitClient client = RetrofitClient.getInstance(this.getContext());
        SMBRemoteServices portalServices = client.getPortalServices();
        portalServices.getTracks().enqueue(new Callback<PaginatedResult<TrackItem>>() {
            @Override
            public void onResponse(Call<PaginatedResult<TrackItem>> call, Response<PaginatedResult<TrackItem>> response) {
                showProgress(false);
                PaginatedResult<TrackItem> result = response.body();
                if(result != null && result.getResults() != null) {
                    adapter.clear();
                    adapter.addAll(response.body().getResults());
                    showEmpty(response.body().getResults().size() == 0, false);
                } else {
                    adapter.clear();
                    adapter.addAll(new ArrayList<>());
                    showEmpty(true, false);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PaginatedResult<TrackItem>> call, Throwable t) {
                showProgress(false);
                showEmpty(true, true);
            }
        });
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
        if(mySwipeRefreshLayout != null & !show) {
            mySwipeRefreshLayout.setRefreshing(show);
        }
    }
    private void showEmpty(boolean show, boolean noNetwork) {
        if(getActivity() != null) {
            View e = getActivity().findViewById(R.id.emptyTracks);
            View n = getActivity().findViewById(R.id.emptyNoNetwork);
            if (e != null) {
                boolean showEmpty = show && !noNetwork || show && n == null;
                e.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
            }
            if(n != null) {
                n.setVisibility(show && noNetwork ? View.VISIBLE : View.GONE);
            }
        }
    }
}

