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

import it.geosolutions.savemybike.model.competition.Sponsor;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;

/**
 * Adapter for Sponsors
 */
public class CompetitionSponsorAdapter extends ArrayAdapter<Sponsor> {

	protected int resource;

	static class ViewHolder
	{
		@BindView(R.id.sponsor_header) TextView header;
		@BindView(R.id.sponsor_description) TextView description;
		@BindView(R.id.sponsor_image) ImageView icon;
		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}


	public CompetitionSponsorAdapter(final Context context, int textViewResourceId, List<Sponsor> sponsors)
	{
		super(context, textViewResourceId, sponsors);

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

		Sponsor sponsor = getItem(position);
		// setup view

		if(sponsor != null)
		{
			final String url = sponsor.url;

			holder.header.setText(sponsor.name);
			holder.description.setText(sponsor.url);

			if(sponsor.logo != null) {
				GlideApp
						.with(getContext())
						.load(sponsor.logo)
						.into((ImageView) holder.icon);
			}

			holder.icon.setOnClickListener(view1 -> {
				SaveMyBikeActivity.instance().openURL(url);
			});
			holder.header.setOnClickListener(view2 -> {
				SaveMyBikeActivity.instance().openURL(url);
			});
			holder.description.setOnClickListener(view3 -> {
				SaveMyBikeActivity.instance().openURL(url);
			});

		}

		return view;
	}

}