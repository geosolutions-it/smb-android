package it.geosolutions.savemybike.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.Badge;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.CurrentStatus;
import it.geosolutions.savemybike.model.PaginatedResult;
import it.geosolutions.savemybike.model.TrackItem;
import it.geosolutions.savemybike.ui.BikeAdapter;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.adapters.BadgeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Frabment for badges list
 */

public class BadgesFragment extends Fragment {
    public static final String TAG = "BADGES_LIST";
    @BindView(R.id.list) ListView listView;
    @BindView(R.id.content_layout) LinearLayout content;

    @BindView(R.id.progress_layout) LinearLayout progress;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mySwipeRefreshLayout;

    BadgeAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_badges, container, false);
        ButterKnife.bind(this, view);
        SaveMyBikeActivity activity = ((SaveMyBikeActivity)getActivity());
        showEmpty(false, false);
        // setup adapter
        ArrayList badges = new ArrayList<Badge>();

        adapter = new BadgeAdapter(activity, R.layout.item_badge, badges);
        listView.setAdapter(adapter);
        mySwipeRefreshLayout.setOnRefreshListener(() -> getBadges());
        getBadges();
        return view;
    }

    /**
     * loads the locally available sessions from the database and invalidates the UI
     */
    private void getBadges() {
        RetrofitClient client = RetrofitClient.getInstance(this.getContext());
        SMBRemoteServices portalServices = client.getPortalServices();

        showProgress(true);
        portalServices.getBadges().enqueue(new Callback<PaginatedResult<Badge>>() {
            @Override
            public void onResponse(Call<PaginatedResult<Badge>> call, Response<PaginatedResult<Badge>> response) {
                showProgress(false);
                PaginatedResult<Badge> result = response.body();
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
            public void onFailure(Call<PaginatedResult<Badge>> call, Throwable t) {
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
            View e = getActivity().findViewById(R.id.empty_badges);
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
