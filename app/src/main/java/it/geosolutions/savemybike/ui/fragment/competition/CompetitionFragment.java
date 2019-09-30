package it.geosolutions.savemybike.ui.fragment.competition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.competition.Competition;
import it.geosolutions.savemybike.model.competition.CompetitionParticipantRequest;
import it.geosolutions.savemybike.model.competition.CompetitionParticipationInfo;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.adapters.competition.CompetitionPrizeAdapter;
import it.geosolutions.savemybike.ui.adapters.competition.CompetitionSponsorAdapter;
import it.geosolutions.savemybike.ui.custom.WrappingGridView;
import it.geosolutions.savemybike.ui.fragment.competitions.AvailableCompetitionsFragment;
import it.geosolutions.savemybike.ui.fragment.competitions.CurrentCompetitionsFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class CompetitionFragment extends Fragment implements View.OnClickListener
{
	@BindView(R.id.image_view) ImageView m_oImageView;
	@BindView(R.id.title_text) TextView m_oTitleTextView;
	@BindView(R.id.description_text) TextView m_oDescriptionTextView;
	@BindView(R.id.action_button) Button m_oActionButton;
	@BindView(R.id.progress_layout) LinearLayout m_oProgressLayout;
	@BindView(R.id.prizes_grid) WrappingGridView m_oPrizesGrid;
	@BindView(R.id.sponsor_grid) WrappingGridView m_oSponsorsGrid;
	@BindView(R.id.prizes_title_text) TextView m_oPrizesHeader;
	@BindView(R.id.sponsors_title_text) TextView m_oSponsorsHeader;
	@BindView(R.id.congratulations_you_won_text) TextView m_oCongratulationsYouWonText;

	private Competition m_oCompetition;
	private CompetitionParticipationInfo m_oParticipationInfo;
	private boolean m_bIsWon;

	public CompetitionFragment(Competition bd, CompetitionParticipationInfo pi, boolean bIsWon)
	{
		super();
		m_oCompetition = bd;
		m_oParticipationInfo = pi;
		m_bIsWon = bIsWon;
	}

	protected void markBusy(boolean bBusy)
	{
		m_oProgressLayout.setVisibility(bBusy ? View.VISIBLE : View.GONE);
		m_oActionButton.setEnabled(!bBusy);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_competition, container,false);
		ButterKnife.bind(this, view);

		m_oProgressLayout.setVisibility(View.GONE);

		if(m_oParticipationInfo != null)
		{
			if(m_oParticipationInfo.registrationStatus.contains("pending"))
				m_oImageView.setImageResource(R.drawable.ic_competition_waiting);
			else
				m_oImageView.setImageResource(R.drawable.ic_competition_participating);
		} else {
			m_oImageView.setImageResource(R.drawable.ic_competition);
		}

		m_oTitleTextView.setText(m_oCompetition.name);
		m_oDescriptionTextView.setText(m_oCompetition.description);

		if(m_bIsWon)
		{
			m_oActionButton.setVisibility(View.GONE);
		} else {
			m_oActionButton.setText((m_oParticipationInfo != null) ? R.string.competition_cancel_button_text : R.string.competition_join_button_text);
			m_oActionButton.setOnClickListener(this);
		}

		if(m_bIsWon)
		{
			m_oCongratulationsYouWonText.setVisibility(View.VISIBLE);
			m_oCongratulationsYouWonText.setText(R.string.congratulations_you_won_this_competition);
		} else {
			m_oCongratulationsYouWonText.setVisibility(View.GONE);
			m_oCongratulationsYouWonText.setHeight(2);
		}


		if((m_oCompetition.prizes != null) && (m_oCompetition.prizes.size() > 0))
		{
			m_oPrizesHeader.setText(R.string.prizes_header);
			m_oPrizesGrid.setAdapter(new CompetitionPrizeAdapter(getContext(), R.layout.item_prize, m_oCompetition.prizes));
		} else {
			m_oPrizesHeader.setText("");
		}

		if((m_oCompetition.sponsors != null) && (m_oCompetition.sponsors.size() > 0))
		{
			m_oSponsorsHeader.setText(R.string.sponsors_header);
			m_oSponsorsGrid.setAdapter(new CompetitionSponsorAdapter(getContext(), R.layout.item_sponsor, m_oCompetition.sponsors));
		} else {
			m_oSponsorsHeader.setText("");
		}

		return view;
	}


	private void requestParticipation()
	{
		RetrofitClient client = RetrofitClient.getInstance(this.getContext());
		SMBRemoteServices portalServices = client.getPortalServices();

		CompetitionParticipantRequest rq = new CompetitionParticipantRequest();
		rq.competitionId = m_oCompetition.id;

		final Context ctx = getContext();

		markBusy(true);

		client.performAuthenticatedCall(
				portalServices.requestCompetitionParticipation(rq),
				new Callback<ResponseBody>()
				{
					@Override
					public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
					{
						markBusy(false);

						AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);

						dlg.setTitle(R.string.request_succeeded);
						dlg.setMessage(R.string.participation_requested);
						dlg.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialogInterface, int i)
							{
								dialogInterface.dismiss();
							}
						});
						dlg.create().show();

						if(AvailableCompetitionsFragment.lastInstance() != null)
							AvailableCompetitionsFragment.lastInstance().fetchItems();
						if(CurrentCompetitionsFragment.lastInstance() != null)
							CurrentCompetitionsFragment.lastInstance().fetchItems();

						SaveMyBikeActivity.instance().popFragment();
					}

					@Override
					public void onFailure(Call<ResponseBody> call, Throwable t)
					{
						markBusy(false);
						AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);

						dlg.setTitle(R.string.request_failed);
						dlg.setMessage(t.getLocalizedMessage());
						dlg.create().show();
					}
				}
			);
	}

	private void cancelParticipation()
	{
		RetrofitClient client = RetrofitClient.getInstance(this.getContext());
		SMBRemoteServices portalServices = client.getPortalServices();

		final Context ctx = getContext();

		markBusy(true);

		client.performAuthenticatedCall(
				portalServices.cancelCompetitionParticipation(m_oParticipationInfo.id),
				new Callback<ResponseBody>()
				{
					@Override
					public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
					{
						markBusy(false);

						AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);

						dlg.setTitle(R.string.request_succeeded);
						dlg.setMessage(R.string.participation_canceled);
						dlg.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialogInterface, int i)
							{
								dialogInterface.dismiss();
							}
						});
						dlg.create().show();

						SaveMyBikeActivity.instance().popFragment();
					}

					@Override
					public void onFailure(Call<ResponseBody> call, Throwable t)
					{
						markBusy(false);

						AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);

						dlg.setTitle(R.string.request_failed);
						dlg.setMessage(t.getLocalizedMessage());
						dlg.create().show();
					}
				}
			);
	}

	public void cancelParticipationAfterConfirmation()
	{
		AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());

		dlg.setTitle(R.string.confirm_participation_cancellation_title);
		dlg.setMessage(R.string.confirm_participation_cancellation_body);
		dlg.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				dialogInterface.dismiss();
				cancelParticipation();
			}
		});
		dlg.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				dialogInterface.dismiss();
			}
		});
		dlg.create().show();
	}


	@Override
	public void onClick(View view)
	{
		if(m_oParticipationInfo != null)
			cancelParticipationAfterConfirmation();
		else
			requestParticipation();
	}
}
