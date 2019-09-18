package it.geosolutions.savemybike.model.competition;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CompetitionBaseData
{
	public long id;

	public String url;

	public String name;

	public String description;

	public @SerializedName("age_groups") ArrayList<String> ageGroups;

	public @SerializedName("start_date") String startDate;

	public @SerializedName("end_date") String endDate;

	public ArrayList<String> criteria;
}
