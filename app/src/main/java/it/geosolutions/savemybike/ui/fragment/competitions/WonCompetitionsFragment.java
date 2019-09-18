package it.geosolutions.savemybike.ui.fragment.competitions;

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
import it.geosolutions.savemybike.model.competition.CompetitionBaseData;
import it.geosolutions.savemybike.model.competition.CompetitionParticipationInfo;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.adapters.competition.AvailableCompetitionAdapter;
import it.geosolutions.savemybike.ui.adapters.competition.BaseCompetitionAdapter;
import it.geosolutions.savemybike.ui.adapters.competition.WonCompetitionAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WonCompetitionsFragment extends BaseCompetitionsFragment<CompetitionParticipationInfo>
{
	private static WonCompetitionsFragment m_oLastInstance = null;

	public static WonCompetitionsFragment lastInstance()
	{
		return m_oLastInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
	{
		m_oLastInstance = this;
		return super.onCreateView(inflater,container,savedInstanceState);
	}

	@Override
	public void onDestroyView()
	{
		m_oLastInstance = null;
		super.onDestroyView();
	}

	@Override
	protected BaseCompetitionAdapter<CompetitionParticipationInfo> createAdapter()
	{
		return new WonCompetitionAdapter(getActivity(), R.layout.item_competition,new ArrayList<CompetitionParticipationInfo>());
	}


	@Override
	protected int getEmptyTextResourceId()
	{
		return R.string.no_prize_title;
	}

	@Override
	protected int getEmptyDescriptionResourceId()
	{
		return R.string.no_prize_description;
	}

	@Override
	public void fetchItems()
	{
		RetrofitClient client = RetrofitClient.getInstance(this.getContext());
		SMBRemoteServices portalServices = client.getPortalServices();

		showProgress(true);

		client.performAuthenticatedCall(
				portalServices.getMyCompetitionsWon(),
				new Callback<PaginatedResult<CompetitionParticipationInfo>>()
				{
					@Override
					public void onResponse(Call<PaginatedResult<CompetitionParticipationInfo>> call, Response<PaginatedResult<CompetitionParticipationInfo>> response)
					{
						showProgress(false);
						PaginatedResult<CompetitionParticipationInfo> result = response.body();
						if(result != null && result.getResults() != null)
						{
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
					public void onFailure(Call<PaginatedResult<CompetitionParticipationInfo>> call, Throwable t)
					{
						showProgress(false);
						showEmpty(true, true);
						adapter.clear();
						adapter.notifyDataSetChanged();
					}
				}
			);
	}

}
