package it.geosolutions.savemybike.ui.adapters.competition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.competition.Competition;
import it.geosolutions.savemybike.model.competition.CompetitionParticipationInfo;


public class CurrentCompetitionAdapter extends ParticipatedCompetitionAdapter
{
    public CurrentCompetitionAdapter(Context context, int textViewResourceId, List<CompetitionParticipationInfo> competitions)
    {
        super(context, textViewResourceId, competitions);
    }

	@NonNull
    @Override
    public View getView(int position, @Nullable View contentView, @NonNull ViewGroup parent)
    {
        View view = super.getView(position, contentView, parent);

        if(view == null)
        	return null;

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.subtitle.setVisibility(View.VISIBLE);

        CompetitionParticipationInfo participation = getItem(position);
        Competition competition = (participation != null) ? participation.competition : null;

        if(competition != null)
        {
            // format validity
            if(competition.startDate != null && competition.endDate != null)
            {
                String startDate = DateTimeFormat.forPattern("dd MMMM").print(new DateTime((competition.startDate)));
                String endDate = DateTimeFormat.forPattern("dd MMMM").print(new DateTime((competition.endDate)));
                String subTitle = String.format(getContext().getResources().getString(R.string.validity), startDate, endDate);
                holder.subtitle.setText(subTitle);
            }
        }

        return view;
    }
}
