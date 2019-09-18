package it.geosolutions.savemybike.ui.adapters.competition;

import android.content.Context;

import java.util.List;

import it.geosolutions.savemybike.model.competition.CompetitionBaseData;
import it.geosolutions.savemybike.model.competition.CompetitionPrize;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.fragment.competition.CompetitionFragment;

public class AvailableCompetitionAdapter extends BaseCompetitionAdapter<CompetitionBaseData>
{
	public AvailableCompetitionAdapter(Context context, int textViewResourceId, List<CompetitionBaseData> competitions)
	{
		super(context, textViewResourceId, competitions);
	}

	@Override
	public CompetitionBaseData getCompetitionData(CompetitionBaseData rc)
	{
		return rc;
	}

	@Override
	public List<CompetitionPrize> getPrizes(CompetitionBaseData rc)
	{
		return null;
	}

	@Override
	public void onCompetitionSelected(CompetitionBaseData bd)
	{
		SaveMyBikeActivity.instance().pushFragment(new CompetitionFragment(bd,null));
	}
}

