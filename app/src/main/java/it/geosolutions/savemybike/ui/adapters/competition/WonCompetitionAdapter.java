package it.geosolutions.savemybike.ui.adapters.competition;

import android.content.Context;

import java.util.List;

import it.geosolutions.savemybike.model.competition.CompetitionParticipationInfo;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.fragment.competition.CompetitionFragment;

public class WonCompetitionAdapter extends ParticipatedCompetitionAdapter
{
    public WonCompetitionAdapter(Context context, int textViewResourceId, List<CompetitionParticipationInfo> competitions)
    {
	    super(context, textViewResourceId, competitions);
    }

	@Override
	public void onCompetitionSelected(CompetitionParticipationInfo pi)
	{
		SaveMyBikeActivity.instance().pushFragment(new CompetitionFragment(pi.competition,pi,true));
	}
}
