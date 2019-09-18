package it.geosolutions.savemybike.model.competition;

import com.google.gson.annotations.SerializedName;

public class CompetitionPrize
{
    public Prize prize;

	public @SerializedName("winner_description") String winnerDescription;
	public @SerializedName("user_rank") int userRank;
}
