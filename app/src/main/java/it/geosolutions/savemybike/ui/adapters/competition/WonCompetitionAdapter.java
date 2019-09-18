package it.geosolutions.savemybike.ui.adapters.competition;

import android.content.Context;

import java.util.List;

import it.geosolutions.savemybike.model.competition.CompetitionParticipationInfo;

public class WonCompetitionAdapter extends ParticipatedCompetitionAdapter
{
    public WonCompetitionAdapter(Context context, int textViewResourceId, List<CompetitionParticipationInfo> competitions)
    {
	    super(context, textViewResourceId, competitions);
    }
}
