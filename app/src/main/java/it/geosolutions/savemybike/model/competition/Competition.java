package it.geosolutions.savemybike.model.competition;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

import it.geosolutions.savemybike.model.competition.Prize;

public class Competition
{
	public long id;

	public String url;

	public String name;

	public String description;

	public @SerializedName("age_groups") ArrayList<String> ageGroups;

	public @SerializedName("start_date") String startDate;

	public @SerializedName("end_date") String endDate;

	public ArrayList<String> criteria;

	public @SerializedName("winner_threshold") long winnerThreshold;

	public ArrayList<CompetitionLeader> leaderboard;

	public ArrayList<CompetitionPrize> prizes;

	public ArrayList<Sponsor> sponsors;
}
