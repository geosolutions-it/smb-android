package it.geosolutions.savemybike.model.competition;

import com.google.gson.annotations.SerializedName;

public class CompetitionParticipationInfo
{
	public long id;

	public String url;

	public @SerializedName("registration_status") String registrationStatus;

	public String score;

	public Competition competition;

}
