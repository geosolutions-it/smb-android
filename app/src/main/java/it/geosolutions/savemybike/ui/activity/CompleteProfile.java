package it.geosolutions.savemybike.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Analytics;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.user.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteProfile extends SMBBaseActivity {
	private static final String TAG = "COMPLETEPROFILE";
	private FirebaseAnalytics mFirebaseAnalytics;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		setContentView(R.layout.activity_complete_profile);
	}


	@Override
	public void onRequestPermissionGrant(PermissionIntent permissionIntent) {

	}

	public void onComplete(User user) {
		RetrofitClient client = RetrofitClient.getInstance(getBaseContext());
		SMBRemoteServices service = client.getPortalServices();
		Context context = this;

		client.performAuthenticatedCall(
				service.updateUser(user),
				new Callback<ResponseBody>()
				{
					@Override
					public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
						mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.STATE, "profile_completed");
						Log.d("ANALYTICS", "registered user as a profile completed ");
						Intent intent = new Intent(context, SaveMyBikeActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent);
						finish();
					}

					@Override
					public void onFailure(Call<ResponseBody> call, Throwable t) {
						Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG);
					}
				}
			);
	}
}
