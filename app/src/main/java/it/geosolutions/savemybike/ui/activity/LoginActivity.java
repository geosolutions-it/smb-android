package it.geosolutions.savemybike.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.browser.AnyBrowserMatcher;
import net.openid.appauth.browser.BrowserMatcher;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.Configuration;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.auth.AuthHandlerActivity;
import it.geosolutions.savemybike.auth.AuthenticationManager;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.model.user.User;
import it.geosolutions.savemybike.model.user.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Based on The AppAuth for Android Authors. Licensed under the Apache License, Version 2.0
 *
 * Heavy changes in the login workflow to allow all the authentication happen in this activity
 */
public final class LoginActivity extends AuthHandlerActivity
{
	private FirebaseAnalytics mFirebaseAnalytics;

	private static final String TAG = "LoginActivity";
	public static final String KEY_USER_INFO = "userInfo";

	private Configuration mConfiguration;

	private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();

	@BindView(R.id.explanation) TextView failure_explanation;
	@BindView(R.id.authorized) View authorized;
	@BindView(R.id.not_authorized) View not_authorized;
	@BindView(R.id.loading_container) View loading_container;
	@BindView(R.id.refresh_token_info) TextView refreshTokenInfoView;
	@BindView(R.id.access_token_info) TextView accessTokenInfoView;
	@BindView(R.id.id_token_info) TextView idTokenInfoView;
	@BindView(R.id.refresh_token) Button refreshTokenButton;
	@BindView(R.id.retry) View retry;
	@BindView(R.id.view_profile) Button viewProfileButton;
	@BindView(R.id.userinfo_card) View userInfoCard;
	@BindView(R.id.error_container) View error_container;
	@BindView(R.id.auth_container) View auth_container;
	@BindView(R.id.error_description) TextView error_description;
	@BindView(R.id.userinfo_name) TextView userinfo_name;
	@BindView(R.id.userinfo_json) TextView userinfo_json;
	@BindView(R.id.loading_description) TextView loading_description;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_login);
		ButterKnife.bind(this);
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

		mConfiguration = Configuration.getInstance(this);

		AuthenticationManager.init(getApplicationContext());

		if (mConfiguration.hasConfigurationChanged()) {
			Toast.makeText(
					this,
					"Configuration change detected",
					Toast.LENGTH_SHORT)
					.show();
			AuthenticationManager.instance().clearState();
		}

		if (savedInstanceState != null) {
			Log.i(TAG, "I have instanceState");
			Log.i(TAG, savedInstanceState.getString(KEY_USER_INFO, "But I don't have the KEY_USER_INFO!!"));
/*            try {
				mUserInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));
			} catch (JSONException ex) {
				Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex);
			}*/
		}

		if (!mConfiguration.isValid()) {
			displayError(mConfiguration.getConfigurationError(), false);
			return;
		}

		//displayLoading("Initializing");
		displayAuthOptions();
	}

	@OnClick(R.id.retry)
	public void reinitializeAppAuth()
	{
		AuthenticationManager.instance().startAuthentication();
	}

	private void displayAuthorized()
	{

		// authorized.setVisibility(View.VISIBLE);
		not_authorized.setVisibility(View.GONE);
		loading_container.setVisibility(View.GONE);

		User localUser = it.geosolutions.savemybike.model.Configuration.getUserProfile(this);

		if(localUser == null // first login
			|| localUser.getAcceptedTermsOfService() == null // old users that didn't have the profile autocomplete but did login
			|| localUser.getAcceptedTermsOfService() == false // terms of service unchecked.
			//  TODO: find out check if different credentials are used
				)
		{

			displayLoading(/*getResources().getString(R.string.checking_user_data)*/ "Checking user data");
			//TODO: retrieve user info and prompt profile completion.
			// TODO: the following block have to be moved after async check to display CompleteProfile wizard
			final Context context = this;

			RetrofitClient client = RetrofitClient.getInstance(this);

			client.performAuthenticatedCall(
					client.getPortalServices().getUser(),
					new Callback<UserInfo>()
					{
						@Override
						public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
							User user = response.body();
							if(response.code() == 200 && (user == null // first login
									|| user.getAcceptedTermsOfService() == null // old users that didn't have the profile autocomplete but did login
									|| user.getAcceptedTermsOfService() == false) )
								{
									if(user != null) {
										mFirebaseAnalytics.setUserId(user.getUsername());
										Log.d("ANALYTICS", "username set as:" + user.getUsername());
									}
									Intent intent = new Intent(context, CompleteProfile.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									startActivity(intent);
									finish();
								} else if (response.code() >= 500 || response.code() == 404 ){
								displayError(getResources().getString(R.string.could_not_verify_user), true);
							} else {
									if(user != null) {
										mFirebaseAnalytics.setUserId(user.getUsername());
										Log.d("ANALYTICS", "username set as:" + user.getUsername());
									}
									Intent intent = new Intent(context, SaveMyBikeActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									startActivity(intent);
									finish();
								}

						}

						@Override
						public void onFailure(Call<UserInfo> call, Throwable t) {
							displayError(getResources().getString(R.string.could_not_verify_user), true);
						}
					}
				);

		} else {
			Intent intent = new Intent(this, SaveMyBikeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		}
	}

	@Override
	protected void onAuthenticationFailed(String sError)
	{
		displayNotAuthorized(sError);
	}

	@Override
	protected void onAuthenticationSucceeded()
	{
		displayAuthorized();
	}

	@OnClick({R.id.start_auth})
	public void startAuth()
	{
		displayLoading("Making authorization request");
		AuthenticationManager.instance().startAuthentication();
	}

	private void displayLoading(String loadingMessage)
	{
		loading_container.setVisibility(View.VISIBLE);
		auth_container.setVisibility(View.GONE);
		error_container.setVisibility(View.GONE);

		loading_description.setText(loadingMessage);
	}

	private void displayError(String error, boolean recoverable)
	{
		error_container.setVisibility(View.VISIBLE);
		loading_container.setVisibility(View.GONE);
		auth_container.setVisibility(View.GONE);

		error_description.setText(error);
		retry.setVisibility(recoverable ? View.VISIBLE : View.GONE);
	}

	private void displayErrorLater(final String error, final boolean recoverable)
	{
		runOnUiThread(() -> displayError(error, recoverable));
	}


	private void displayAuthOptions()
	{
		auth_container.setVisibility(View.VISIBLE);
		loading_container.setVisibility(View.GONE);
		error_container.setVisibility(View.GONE);
	}

	private void displayAuthCancelled()
	{
		Toast.makeText(
				this,
				"Authorization canceled",
				Toast.LENGTH_SHORT)
				.show();
	}

	@MainThread
	private void displayNotAuthorized(String explanation)
	{
		error_container.setVisibility(View.VISIBLE);
		not_authorized.setVisibility(View.VISIBLE);
		authorized.setVisibility(View.GONE);
		loading_container.setVisibility(View.GONE);

		retry.setVisibility(View.GONE);

		Log.e(TAG, explanation);

	}

	@MainThread
	@OnClick({R.id.sign_out})
	public void signOut()
	{
		Log.w(TAG, "Signing out");
		AuthenticationManager.instance().clearState();
		displayAuthOptions();
	}


	@Override
	protected void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);
		// user info is retained to survive activity restarts, such as when rotating the
		// device or switching apps. This isn't essential, but it helps provide a less
		// jarring UX when these events occur - data does not just disappear from the view.
		if (mUserInfoJson.get() != null) {
			state.putString(KEY_USER_INFO, mUserInfoJson.toString());
		}
	}
}
