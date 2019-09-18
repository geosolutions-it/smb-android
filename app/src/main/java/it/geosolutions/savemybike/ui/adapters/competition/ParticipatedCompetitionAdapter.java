package it.geosolutions.savemybike.ui.adapters.competition;

import android.content.Context;

import java.util.List;

import it.geosolutions.savemybike.model.competition.CompetitionBaseData;
import it.geosolutions.savemybike.model.competition.CompetitionParticipationInfo;
import it.geosolutions.savemybike.model.competition.CompetitionPrize;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.fragment.competition.CompetitionFragment;

public class ParticipatedCompetitionAdapter extends BaseCompetitionAdapter<CompetitionParticipationInfo>
{
	public ParticipatedCompetitionAdapter(Context context, int textViewResourceId, List<CompetitionParticipationInfo> competitions)
	{
		super(context, textViewResourceId, competitions);
	}

	@Override
	public CompetitionBaseData getCompetitionData(CompetitionParticipationInfo rc)
	{
		if(rc == null)
			return null;
		return rc.competition;
	}

	@Override
	public List<CompetitionPrize> getPrizes(CompetitionParticipationInfo rc)
	{
		if(rc == null)
			return null;
		if(rc.competition == null)
			return null;
		return rc.competition.prizes;
	}

	@Override
	public void onCompetitionSelected(CompetitionParticipationInfo pi)
	{
		SaveMyBikeActivity.instance().pushFragment(new CompetitionFragment(pi.competition,pi));
	}
}
