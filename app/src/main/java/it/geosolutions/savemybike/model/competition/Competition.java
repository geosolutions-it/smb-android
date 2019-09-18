package it.geosolutions.savemybike.model.competition;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

import it.geosolutions.savemybike.model.competition.Prize;

public class Competition extends CompetitionBaseData
{
	public @SerializedName("winner_threshold") long winnerThreshold;

	public ArrayList<CompetitionLeader> leaderboard;

	public ArrayList<CompetitionPrize> prizes;

	public ArrayList<Sponsor> sponsors;
}
