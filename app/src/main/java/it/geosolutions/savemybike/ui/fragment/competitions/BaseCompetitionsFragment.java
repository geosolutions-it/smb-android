package it.geosolutions.savemybike.ui.fragment.competitions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.adapters.competition.BaseCompetitionAdapter;

public abstract class BaseCompetitionsFragment<ResultClass> extends android.support.v4.app.Fragment
{
	@BindView(R.id.list)
	GridView listView;
	@BindView(R.id.content_layout)
	LinearLayout content;
	@BindView(R.id.progress_layout) LinearLayout progress;
	@BindView(R.id.swiperefresh)
	SwipeRefreshLayout mySwipeRefreshLayout;
	@BindView(R.id.empty_competition)
	View emptyView;
	@BindView(R.id.emptyNoNetwork) View emptyNoNetwork;

	BaseCompetitionAdapter<ResultClass> adapter;


	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.fragment_competition_list, container, false);

		ButterKnife.bind(this, view);

		SaveMyBikeActivity activity = ((SaveMyBikeActivity)getActivity());
		showEmpty(false, false);

		adapter = createAdapter();

		listView.setAdapter(adapter);
		mySwipeRefreshLayout.setOnRefreshListener(() -> fetchItems());
		fetchItems();
		return view;
	}

	protected abstract BaseCompetitionAdapter<ResultClass> createAdapter();

	protected abstract int getEmptyTextResourceId();
	protected abstract int getEmptyDescriptionResourceId();

	public abstract void fetchItems();

	/**
	 * Switches the UI of this screen to show either the progress UI or the content
	 * @param show if true shows the progress UI and hides content, if false the other way around
	 */
	protected void showProgress(final boolean show)
	{
		if(isAdded())
		{

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

		if(mySwipeRefreshLayout != null & !show)
			mySwipeRefreshLayout.setRefreshing(show);
	}

	protected void showEmpty(boolean show, boolean noNetwork)
	{
		View e = emptyView;
		View n = emptyNoNetwork;
		if (e != null)
		{
			boolean showEmpty = show && !noNetwork || show && n == null;
			e.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
			((TextView) e.findViewById(R.id.empty_text)).setText(getEmptyTextResourceId());
			((TextView) e.findViewById(R.id.empty_description)).setText(getEmptyDescriptionResourceId());
		}
		if(n != null)
			n.setVisibility(show && noNetwork ? View.VISIBLE : View.GONE);
	}

}
