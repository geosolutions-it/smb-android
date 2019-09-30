package it.geosolutions.savemybike.ui.fragment.competitions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.PaginatedResult;
import it.geosolutions.savemybike.model.competition.Competition;
import it.geosolutions.savemybike.ui.adapters.competition.AvailableCompetitionAdapter;
import it.geosolutions.savemybike.ui.adapters.competition.BaseCompetitionAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailableCompetitionsFragment extends BaseCompetitionsFragment<Competition>
{
	private static AvailableCompetitionsFragment m_oLastInstance = null;

	public static AvailableCompetitionsFragment lastInstance()
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
	protected BaseCompetitionAdapter<Competition> createAdapter()
	{
		return new AvailableCompetitionAdapter(getActivity(), R.layout.item_competition,new ArrayList<Competition>());
	}


	@Override
	protected int getEmptyTextResourceId()
	{
		return R.string.no_competition_currently_active_title;
	}

	@Override
	protected int getEmptyDescriptionResourceId()
	{
		return R.string.no_competition_currently_active_description;
	}

	@Override
	public void fetchItems()
	{
		RetrofitClient client = RetrofitClient.getInstance(this.getContext());
		SMBRemoteServices portalServices = client.getPortalServices();

		showProgress(true);

		client.performAuthenticatedCall(
				portalServices.getMyCompetitionsAvailable(),
				new Callback<PaginatedResult<Competition>>()
				{
					@Override
					public void onResponse(Call<PaginatedResult<Competition>> call, Response<PaginatedResult<Competition>> response)
					{
						showProgress(false);
						PaginatedResult<Competition> result = response.body();
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
					public void onFailure(Call<PaginatedResult<Competition>> call, Throwable t)
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
