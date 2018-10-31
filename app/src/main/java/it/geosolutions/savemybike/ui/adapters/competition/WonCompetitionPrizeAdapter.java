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
public class WonCompetitionPrizeAdapter extends CompetitionPrizeAdapter {


    public WonCompetitionPrizeAdapter(final Context context, int textViewResourceId, List<CompetitionPrize> prizes) {
        super(context, textViewResourceId, prizes);
    }

    protected String getDescription(CompetitionPrize cPrize) {
        return cPrize.getWinnerDescription();
    }


}