package it.geosolutions.savemybike.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.connectivity.DefaultConnectionBuilder;

import org.json.JSONException;

import java.util.ArrayList;

import it.geosolutions.savemybike.Configuration;

public class AuthenticationManager
{
	private static AuthenticationManager m_oInstance = null;

	private AuthState m_oAuthState = null;
	private AuthorizationService m_oAuthService = null;
	private static AuthorizationServiceConfiguration m_oAuthConfiguration = null;
	private Uri m_oDiscoveryUri = null;
	private Uri m_oRedirectUri = null;
	private String m_sClientId = null;
	private String m_sScope = null;
	private Context m_oContext = null;
	private AuthHandlerActivity m_oCurrentActivity = null;
	private ArrayList<PendingCall> m_lPendingCalls = null;
	private boolean m_bAuthInProgress = false;

	private static final String TAG = "AuthenticationManager";

	private static final String STORE_NAME = "AuthState";
	private static final String KEY_STATE = "state";

	private final SharedPreferences m_oPreferences;

	// This class is never deinitialized.

	public static AuthenticationManager instance()
	{
		return m_oInstance;
	}

	private AuthenticationManager(Context ctx)
	{
		m_lPendingCalls = new ArrayList<>();

		m_oPreferences = ctx.getSharedPreferences("AuthState",Context.MODE_PRIVATE);

		Configuration oConfig = Configuration.getInstance(ctx);

		m_oContext = ctx;
		m_oDiscoveryUri = oConfig.getDiscoveryUri();
		m_oRedirectUri = oConfig.getRedirectUri();
		m_sClientId = oConfig.getClientId();
		m_sScope = oConfig.getScope();

		m_oAuthService = new AuthorizationService(
				ctx,
				new AppAuthConfiguration.Builder()
						.setConnectionBuilder(DefaultConnectionBuilder.INSTANCE)
						.build());

		readState();
	}

	public AuthState currentAuthState()
	{
		return m_oAuthState;
	}

	public void clearState()
	{
		Log.d(TAG,"Clearing auth state");

		failAllPendingCalls("Authentication aborted");

		m_bAuthInProgress = false; // will abort any pending call!

		if(m_oAuthState == null)
			m_oAuthState = new AuthState();
		else
			m_oAuthState = new AuthState(m_oAuthState.getAuthorizationServiceConfiguration());

		writeState();
	}

	void setCurrentActivity(AuthHandlerActivity a)
	{
		m_oCurrentActivity = a;
	}

	public static void init(Context ctx)
	{
		if(m_oInstance == null)
			m_oInstance = new AuthenticationManager(ctx);
	}

	public void startAuthentication()
	{
		if(m_bAuthInProgress)
		{
			Log.d(TAG,"Authentication already in progress");
			return;
		}

		m_bAuthInProgress = true;

		Log.d(TAG,"Starting authentication");
		// FIXME: Handle state!
		if(m_oAuthConfiguration == null)
			startServiceDiscovery();
		else
			startAuthRequest();
	}

	private void startServiceDiscovery()
	{
		Log.d(TAG,"Starting service discovery");
		AuthorizationServiceConfiguration.fetchFromUrl(
				m_oDiscoveryUri,
				(AuthorizationServiceConfiguration.RetrieveConfigurationCallback) (serviceConfiguration, ex) -> {
					if(ex != null)
					{
						AuthenticationManager.instance().onServiceDiscroveryFailed(ex.getLocalizedMessage());
						return;
					}
					AuthenticationManager.instance().onServiceDiscoverySucceeded(serviceConfiguration);
				},
				DefaultConnectionBuilder.INSTANCE
		);
	}

	private void onServiceDiscroveryFailed(String sError)
	{
		Log.d(TAG,"ERROR: Service discovery failed: " + sError);
		onAuthenticationFailed(sError);
	}

	private void onServiceDiscoverySucceeded(AuthorizationServiceConfiguration cfg)
	{
		Log.d(TAG,"Service discovery succeeded");
		m_oAuthConfiguration = cfg;
		if(!m_bAuthInProgress)
		{
			Log.d(TAG,"Authentication process seems to have been aborted in the meantime");
			return;
		}
		startAuthRequest();
	}

	private void startAuthRequest()
	{
		if(m_oAuthConfiguration == null)
			throw new Error("Bad state");

		AuthorizationRequest.Builder bld = new AuthorizationRequest.Builder(
				m_oAuthConfiguration,
				m_sClientId,
				ResponseTypeValues.CODE,
				m_oRedirectUri
		);

		bld.setScope(m_sScope);

		if(m_oCurrentActivity == null)
			throw new Error("Bad activity state!");

		m_oCurrentActivity.startActivityForResult(
				m_oAuthService.getAuthorizationRequestIntent(bld.build()),
				AuthHandlerActivity.AUTHORIZATION_REQUEST_CODE
		);
	}

	void onAuthRequestCompleted(AuthHandlerActivity oActivity,int resultCode, Intent data)
	{
		Log.d(TAG,"Auth request completed: " + String.valueOf(resultCode));

		if(m_oCurrentActivity == null)
			throw new Error("Bad activity state!");
		if(m_oCurrentActivity != oActivity)
			throw new Error("Bad activity state!");

		AuthorizationResponse res = AuthorizationResponse.fromIntent(data);
		if(res == null)
		{
			AuthorizationException exc = AuthorizationException.fromIntent(data);
			String sError = (exc == null) ? "Unknown error" : exc.getLocalizedMessage();
			Log.d(TAG,"ERROR: Auth request seems to be failed: " + sError);
			oActivity.onAuthenticationFailed(sError);
			return;
		}

		Log.d(TAG,"Auth request succeeded. Performing token exchange");

		if(!m_bAuthInProgress)
		{
			Log.d(TAG,"Authentication process seems to have been aborted in the meantime");
			return;
		}

		// bleargh...
		m_oAuthService.performTokenRequest(
				res.createTokenExchangeRequest(),
				(response, ex) -> {
					if(response == null)
					{
						AuthenticationManager.instance().onTokenExchangeFailed((ex == null) ? "Failed to exchange tokens" : ex.getLocalizedMessage());
						return;
					}
					AuthenticationManager.instance().onTokenExchangeSucceeded(response);
				}
		);
	}

	private void onTokenExchangeSucceeded(TokenResponse resp)
	{
		Log.d(TAG,"Token exchange succeeded");
		m_oAuthState.update(resp,null);
		writeState();
		onAuthenticationSucceeded();
	}

	private void onTokenExchangeFailed(String sError)
	{
		Log.d(TAG,"ERROR: Token exchange failed: " + sError);
		onAuthenticationFailed(sError);
	}


	private void onAuthenticationFailed(String sError)
	{
		Log.d(TAG,"ERROR: Authentication failed: " + sError);
		if(m_oCurrentActivity == null)
			throw new Error("Bad activity state!");

		m_bAuthInProgress = false;

		failAllPendingCalls(sError);

		m_oCurrentActivity.onAuthenticationFailed(sError);
	}

	private void onAuthenticationSucceeded()
	{
		Log.d(TAG,"Authentication succeeded!");
		if(m_oCurrentActivity == null)
			throw new Error("Bad activity state!");

		writeState();

		m_bAuthInProgress = false;

		restartAllPendingCalls();

		m_oCurrentActivity.onAuthenticationSucceeded();
	}


	private void readState()
	{
		String currentState = m_oPreferences.getString(KEY_STATE, null);
		if (currentState == null)
		{
			m_oAuthState = new AuthState();
			return;
		}

		try {
			m_oAuthState = AuthState.jsonDeserialize(currentState);
		} catch (JSONException ex) {
			Log.w(TAG, "Failed to deserialize stored auth state - discarding");
			m_oAuthState = new AuthState();
		}
	}

	private void writeState()
	{
		SharedPreferences.Editor editor = m_oPreferences.edit();
		editor.putString(KEY_STATE, m_oAuthState.jsonSerializeString());
		if (!editor.commit()) {
			throw new IllegalStateException("Failed to write state to shared prefs");
		}
	}

	public interface PendingCall
	{
		void onAuthenticationSucceeded();
		void onAuthenticationFailed(String sError);
	}

	public void onAuthenticatedCallFailed(PendingCall oCall)
	{
		Log.d(TAG,"Authenticated call failed");
		m_lPendingCalls.add(oCall);

		if(m_bAuthInProgress)
		{
			Log.d(TAG,"An authentication call is already in progress");
			return;
		}

		m_bAuthInProgress = true;

		TokenRequest.Builder bld = new TokenRequest.Builder(m_oAuthConfiguration,m_sClientId);
		bld.setRefreshToken(m_oAuthState.getRefreshToken());

		m_oAuthService.performTokenRequest(
				bld.build(),
				(response, ex) -> {
					if(response == null)
					{
						AuthenticationManager.instance().onTokenExchangeFailed((ex == null) ? "Failed to exchange tokens" : ex.getLocalizedMessage());
						return;
					}
					AuthenticationManager.instance().onTokenExchangeSucceeded(response);
				}
		);

	}

	private void failAllPendingCalls(String sError)
	{
		Log.d(TAG,"Failing all pending calls with error " + sError);
		while(m_lPendingCalls.size() > 0)
		{
			PendingCall pc = m_lPendingCalls.get(0);
			m_lPendingCalls.remove(0);
			pc.onAuthenticationFailed(sError);
		}
	}

	private void restartAllPendingCalls()
	{
		Log.d(TAG,"Restarting all pending calls");
		while(m_lPendingCalls.size() > 0)
		{
			PendingCall pc = m_lPendingCalls.get(0);
			m_lPendingCalls.remove(0);
			pc.onAuthenticationSucceeded();
		}
	}

}
