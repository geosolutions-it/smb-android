package it.geosolutions.savemybike.ui.adapters.competition;

import android.content.Context;

import java.util.List;

import it.geosolutions.savemybike.model.competition.Competition;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.fragment.competition.CompetitionFragment;

public class AvailableCompetitionAdapter extends BaseCompetitionAdapter<Competition>
{
	public AvailableCompetitionAdapter(Context context, int textViewResourceId, List<Competition> competitions)
	{
		super(context, textViewResourceId, competitions);
	}

	@Override
	public Competition getCompetitionData(Competition rc)
	{
		return rc;
	}

	@Override
	public void onCompetitionSelected(Competition bd)
	{
		SaveMyBikeActivity.instance().pushFragment(new CompetitionFragment(bd,null,false));
	}
}

