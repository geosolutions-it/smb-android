package it.geosolutions.savemybike.ui.fragment.prizes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.PaginatedResult;
import it.geosolutions.savemybike.model.competition.Competition;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.adapters.competition.BaseCompetitionAdapter;
import it.geosolutions.savemybike.ui.adapters.competition.CurrentCompetitionAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Frabment for current competitions list
 */

public class CurrentCompetitionsFragment extends Fragment {
    public static final String TAG = "BADGES_LIST";
    @BindView(R.id.list) GridView listView;
    @BindView(R.id.content_layout) LinearLayout content;
    @BindView(R.id.list_header) TextView header;
    @BindView(R.id.progress_layout) LinearLayout progress;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mySwipeRefreshLayout;
    @BindView(R.id.empty_competition) View emptyView;
    @BindView(R.id.emptyNoNetwork) View emptyNoNetwork;

    BaseCompetitionAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_competition_list, container, false);
        ButterKnife.bind(this, view);
        SaveMyBikeActivity activity = ((SaveMyBikeActivity)getActivity());
        showEmpty(false, false);
        // setup adapter
        ArrayList prizes = new ArrayList<Competition>();
        header.setText(R.string.up_to_grab);
        adapter = new CurrentCompetitionAdapter(activity, R.layout.item_competition, prizes);
        listView.setAdapter(adapter);
        mySwipeRefreshLayout.setOnRefreshListener(() -> getPrizes());
        getPrizes();
        return view;
    }

    private void getPrizes() {
        RetrofitClient client = RetrofitClient.getInstance(this.getContext());
        SMBRemoteServices portalServices = client.getPortalServices();

        showProgress(true);

        portalServices.getMyCompetitions().enqueue(new Callback<PaginatedResult<Competition>>() {
            @Override
            public void onResponse(Call<PaginatedResult<Competition>> call, Response<PaginatedResult<Competition>> response) {
                showProgress(false);
                PaginatedResult<Competition> result = response.body();
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
            public void onFailure(Call<PaginatedResult<Competition>> call, Throwable t) {
                showProgress(false);
                showEmpty(true, true);
                adapter.clear();
                adapter.notifyDataSetChanged();
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
        View e = emptyView;
        View n = emptyNoNetwork;
        if (e != null) {
            boolean showEmpty = show && !noNetwork || show && n == null;
            e.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
            ((TextView) e.findViewById(R.id.empty_text)).setText(R.string.no_competition_currently_active_title);
            ((TextView) e.findViewById(R.id.empty_description)).setText(R.string.no_competition_currently_active_description);
        }
        if(n != null) {
            n.setVisibility(show && noNetwork ? View.VISIBLE : View.GONE);
        }
    }


}
