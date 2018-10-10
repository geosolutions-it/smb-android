package it.geosolutions.savemybike.ui.adapters.competition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.competition.Competition;
import it.geosolutions.savemybike.model.competition.Prize;

/**
 * Adapter that shows current available competitions
 */
public class CurrentCompetitionAdapter extends BaseCompetitionAdapter {
    public CurrentCompetitionAdapter(Context context, int textViewResourceId, List<Competition> competitions) {
        super(context, textViewResourceId, competitions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View contentView, @NonNull ViewGroup parent) {
        View view = super.getView(position, contentView, parent);
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.subtitle.setVisibility(View.VISIBLE);
        holder.header.setVisibility(View.GONE);

        Competition competition = getItem(position);
        if(competition != null) {
            // format validity
            if(competition.getStartDate() != null && competition.getEndDate() != null) {
                String startDate = DateTimeFormat.forPattern("dd MMMM").print(new DateTime((competition.getStartDate())));
                String endDate = DateTimeFormat.forPattern("dd MMMM").print(new DateTime((competition.getEndDate())));
                String subTitle = String.format(getContext().getResources().getString(R.string.validity), startDate, endDate);
                holder.subtitle.setText(subTitle);
            }
        }

        return view;
    }
}
