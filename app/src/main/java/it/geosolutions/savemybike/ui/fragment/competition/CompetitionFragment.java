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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.competition.CompetitionBaseData;
import it.geosolutions.savemybike.model.competition.CompetitionParticipantRequest;
import it.geosolutions.savemybike.model.competition.CompetitionParticipationInfo;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
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

	private CompetitionBaseData m_oCompetition;
	private CompetitionParticipationInfo m_oParticipationInfo;

	public CompetitionFragment(CompetitionBaseData bd, CompetitionParticipationInfo pi)
	{
		super();
		m_oCompetition = bd;
		m_oParticipationInfo = pi;
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

		m_oActionButton.setText((m_oParticipationInfo != null) ? R.string.competition_cancel_button_text : R.string.competition_join_button_text);

		m_oActionButton.setOnClickListener(this);

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
