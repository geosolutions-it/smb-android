package it.geosolutions.savemybike.ui.adapters.competition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.GlideApp;
import it.geosolutions.savemybike.R;

import it.geosolutions.savemybike.model.competition.CompetitionPrize;
import it.geosolutions.savemybike.model.competition.Prize;
import it.geosolutions.savemybike.model.competition.Sponsor;

/**
 * Adapter for Prizes
 */
public class CompetitionPrizeAdapter extends ArrayAdapter<CompetitionPrize> {

    protected int resource;

    static class ViewHolder {
        @BindView(R.id.prize_header) TextView header;
        @BindView(R.id.prize_description) TextView description;
        @BindView(R.id.prize_subtitle) TextView subtitle;
        @BindView(R.id.prize_image) ImageView icon;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public CompetitionPrizeAdapter(final Context context, int textViewResourceId, List<CompetitionPrize> prizes){
        super(context, textViewResourceId, prizes);

        resource = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        CompetitionPrize cPrize = getItem(position);
        Prize prize = cPrize.getPrize();
        // setup view

        if(prize != null) {
            holder.header.setText(prize.getName());
            holder.description.setText(getDescription(cPrize));
            Sponsor s = prize.getSponsor();
            if(s != null && s.getName() != null) {
                holder.subtitle.setText(s.getName());
            }
            if(prize.getImage() != null) {
                GlideApp
                        .with(getContext())
                        .load(prize.getImage())
                        .into((ImageView) holder.icon);
            }
        }

        return view;
    }

    protected String getDescription(CompetitionPrize cPrize) {
        Prize p = cPrize.getPrize();
        if(p != null) {
            return p.getDescription();
        }
        return null;
    }


}