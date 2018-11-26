package it.geosolutions.savemybike.ui.activity;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.ColorRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoIdToken;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.RegistrationRequest;
import net.openid.appauth.RegistrationResponse;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.browser.AnyBrowserMatcher;
import net.openid.appauth.browser.BrowserMatcher;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.AuthStateManager;
import it.geosolutions.savemybike.Configuration;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.model.user.User;
import it.geosolutions.savemybike.model.user.UserInfo;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Based on The AppAuth for Android Authors. Licensed under the Apache License, Version 2.0
 *
 * Heavy changes in the login workflow to allow all the authentication happen in this activity
 */
public final class LoginActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "LoginActivity";
    private static final String EXTRA_FAILED = "failed";
    private static final int RC_AUTH = 100;
    public static final String KEY_USER_INFO = "userInfo";

    private AuthorizationService mAuthService;
    private AuthStateManager mAuthStateManager;
    private Configuration mConfiguration;

    private final AtomicReference<String> mClientId = new AtomicReference<>();
    private final AtomicReference<AuthorizationRequest> mAuthRequest = new AtomicReference<>();
    private final AtomicReference<CustomTabsIntent> mAuthIntent = new AtomicReference<>();
    private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();

    private CountDownLatch mAuthIntentLatch = new CountDownLatch(1);
    private ExecutorService mExecutor;

    private boolean mUsePendingIntents;

    @NonNull
    private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_login);
        ButterKnife.bind(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mExecutor = Executors.newSingleThreadExecutor();
        mAuthStateManager = AuthStateManager.getInstance(this);
        mConfiguration = Configuration.getInstance(this);

        if (mConfiguration.hasConfigurationChanged()) {
            Toast.makeText(
                    this,
                    "Configuration change detected",
                    Toast.LENGTH_SHORT)
                    .show();
            clearAuthState();
        }

        mAuthService = new AuthorizationService(
                this,
                new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(mConfiguration.getConnectionBuilder())
                        .build());

        if (savedInstanceState != null) {
            Log.i(TAG, "I have instanceState");
            Log.i(TAG, savedInstanceState.getString(KEY_USER_INFO, "But I don't have the KEY_USER_INFO!!"));
/*            try {
                mUserInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex);
            }*/
        }

        if (mAuthStateManager.getCurrent().isAuthorized()
                && !mConfiguration.hasConfigurationChanged()) {
            Log.i(TAG, "User is already authenticated, proceeding to main activity");
            displayAuthorized();

            return;
        }

        if (!mConfiguration.isValid()) {
            displayError(mConfiguration.getConfigurationError(), false);
            return;
        }

        if (mConfiguration.hasConfigurationChanged()) {
            // discard any existing authorization state due to the change of configuration
            Log.i(TAG, "Configuration change detected, discarding old state");
            mAuthStateManager.replace(new AuthState());
            mConfiguration.acceptConfiguration();
        }

        if (getIntent().getBooleanExtra(EXTRA_FAILED, false)) {
            displayAuthCancelled();
        }

        displayLoading("Initializing");
        mExecutor.submit(this::initializeAppAuth);
    }

    @OnClick(R.id.retry)
    public void reinitializeAppAuth() {
                mExecutor.submit(this::initializeAppAuth);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }

        if (mAuthStateManager.getCurrent().isAuthorized()) {
            displayAuthorized();
         }else {

            // the stored AuthState is incomplete, so check if we are currently receiving the result of
            // the authorization flow from the browser.
            AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
            AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

            if (response != null || ex != null) {
                mAuthStateManager.updateAfterAuthorization(response, ex);
            }

            if (response != null && response.authorizationCode != null) {
                // authorization code exchange is required
                mAuthStateManager.updateAfterAuthorization(response, ex);
                exchangeAuthorizationCode(response);
            } else if (ex != null) {
                displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
            } else {
                displayNotAuthorized("No authorization state retained - re-authorization required");
            }
        }
    }

    @MainThread
    private void displayAuthorized() {

        // authorized.setVisibility(View.VISIBLE);
        not_authorized.setVisibility(View.GONE);
        loading_container.setVisibility(View.GONE);

        AuthState state = mAuthStateManager.getCurrent();

        final String refreshToken = state.getRefreshToken();
        refreshTokenInfoView.setText((refreshToken == null)
                ? R.string.no_refresh_token_returned
                : R.string.refresh_token_returned);

        idTokenInfoView.setText((state.getIdToken()) == null
                ? R.string.no_id_token_returned
                : R.string.id_token_returned);

        if (state.getAccessToken() == null) {
            accessTokenInfoView.setText(R.string.no_access_token_returned);
            if(refreshToken == null || new CognitoIdToken(refreshToken).getExpiration().compareTo(new Date()) < 0){
                signOut();
                reinitializeAppAuth();
            }

        } else {
            Long expiresAt = state.getAccessTokenExpirationTime();
            if (expiresAt == null) {
                accessTokenInfoView.setText(R.string.no_access_token_expiry);
                Log.w(TAG, "refresh impossible. This access token has no expire");
                // Note: here we can not use TOKEN_REFRESH_BUFFER because if the next expireAt < currentTimeMillis - TOKEN_REFRESH_BUFFER
                // the login goes in infinite loop (this condition is checked again and again after every refresh)
            } else if (expiresAt < System.currentTimeMillis()) {
                Log.i(TAG, "refreshing token that expired at" + new Date(expiresAt).toString());
                accessTokenInfoView.setText(R.string.access_token_expired);
                refreshAccessToken();
                return;
            } else {
                Log.i(TAG, "the current token will expire at" + new Date(expiresAt).toString());
                String template = getResources().getString(R.string.access_token_expires_at);
                accessTokenInfoView.setText(String.format(template,
                        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss ZZ").print(expiresAt)));
                User localUser = it.geosolutions.savemybike.model.Configuration.getUserProfile(this);

                if(localUser == null // first login
                    || localUser.getAcceptedTermsOfService() == null // old users that didn't have the profile autocomplete but did login
                    || localUser.getAcceptedTermsOfService() == false // terms of service unchecked.
                    //  TODO: find out check if different credentials are used
                        ) {

                    displayLoading(/*getResources().getString(R.string.checking_user_data)*/ "Checking user data");
                    //TODO: retrieve user info and prompt profile completion.
                    // TODO: the following block have to be moved after async check to display CompleteProfile wizard
                    final Context context = this;
                    RetrofitClient.getInstance(this).getPortalServices().getUser().enqueue(new Callback<UserInfo>() {
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
                    });

                } else {
                    Intent intent = new Intent(this, SaveMyBikeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }

                return;
            }
        }

        refreshTokenButton.setVisibility(refreshToken != null
                ? View.VISIBLE
                : View.GONE);
        refreshTokenButton.setOnClickListener((View view) -> refreshAccessToken());

        AuthorizationServiceDiscovery discoveryDoc =
                state.getAuthorizationServiceConfiguration().discoveryDoc;
        if ((discoveryDoc == null || discoveryDoc.getUserinfoEndpoint() == null)
                && mConfiguration.getUserInfoEndpointUri() == null) {
            viewProfileButton.setVisibility(View.GONE);
        } else {
            viewProfileButton.setVisibility(View.VISIBLE);
            viewProfileButton.setOnClickListener((View view) -> fetchUserInfo());
        }


        JSONObject userInfo = mUserInfoJson.get();
        if (userInfo == null) {
            userInfoCard.setVisibility(View.INVISIBLE);
        } else {
            try {
                String name = "???";
                if (userInfo.has("name")) {
                    name = userInfo.getString("name");
                }
                userinfo_name.setText(name);
                userinfo_json.setText(mUserInfoJson.toString());
                userInfoCard.setVisibility(View.VISIBLE);
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to read userinfo JSON", ex);
            }
        }

    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mExecutor.shutdownNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAuthService != null) {
            mAuthService.dispose();
            mAuthService = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Returned. Code: "+ resultCode);
        displayAuthOptions();
        if (resultCode == RESULT_CANCELED && data == null) {
            displayAuthCancelled();
        }  else {

            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.d(TAG, key + " " + (value == null ? "NULL" : value.toString() + " (" + value.getClass().getName()));
                }
            }

            // the stored AuthState is incomplete, so check if we are currently receiving the result of
            // the authorization flow from the browser.
            AuthorizationResponse response = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);

            if (response != null || ex != null) {
                mAuthStateManager.updateAfterAuthorization(response, ex);
            }

            if (response != null && response.authorizationCode != null) {
                Log.i(TAG, "LOGIN SUCCESS");
                // authorization code exchange is required
                mAuthStateManager.updateAfterAuthorization(response, ex);
                exchangeAuthorizationCode(response);
            } else if (ex != null) {
                displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
            } else {
                displayNotAuthorized("No authorization state retained - reauthorization required");
            }

        }
    }

    @MainThread
    @OnClick({R.id.start_auth})
    public void startAuth() {
        displayLoading("Making authorization request");

        mUsePendingIntents = (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) ;

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        mExecutor.submit(this::doAuth);
    }

    /**
     * Initializes the authorization service configuration if necessary, either from the local
     * static values or by retrieving an OpenID discovery document.
     */
    @WorkerThread
    private void initializeAppAuth() {
        Log.i(TAG, "Initializing AppAuth");
        recreateAuthorizationService();

        if (mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration() != null) {
            // configuration is already created, skip to client initialization
            Log.i(TAG, "auth config already established");
            initializeClient();
            return;
        }

        // if we are not using discovery, build the authorization service configuration directly
        // from the static configuration values.
        if (mConfiguration.getDiscoveryUri() == null) {
            Log.i(TAG, "Creating auth config from res/raw/auth_config.json");
            AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
                    mConfiguration.getAuthEndpointUri(),
                    mConfiguration.getTokenEndpointUri(),
                    mConfiguration.getRegistrationEndpointUri());

            mAuthStateManager.replace(new AuthState(config));
            initializeClient();
            return;
        }

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        runOnUiThread(() -> displayLoading("Retrieving discovery document"));
        Log.i(TAG, "Retrieving OpenID discovery doc");
        AuthorizationServiceConfiguration.fetchFromUrl(
                mConfiguration.getDiscoveryUri(),
                this::handleConfigurationRetrievalResult,
                mConfiguration.getConnectionBuilder());
    }

    @MainThread
    private void handleConfigurationRetrievalResult(
            AuthorizationServiceConfiguration config,
            AuthorizationException ex) {
        if (config == null) {
            Log.i(TAG, "Failed to retrieve discovery document", ex);
            displayError("Failed to retrieve discovery document: " + ex.getMessage(), true);
            return;
        }

        Log.i(TAG, "Discovery document retrieved");
        mAuthStateManager.replace(new AuthState(config));
        mExecutor.submit(this::initializeClient);
    }

    /**
     * Initiates a dynamic registration request if a client ID is not provided by the static
     * configuration.
     */
    @WorkerThread
    private void initializeClient() {
        if (mConfiguration.getClientId() != null) {
            Log.i(TAG, "Using static client ID: " + mConfiguration.getClientId());
            // use a statically configured client ID
            mClientId.set(mConfiguration.getClientId());
            runOnUiThread(this::initializeAuthRequest);
            return;
        }

        RegistrationResponse lastResponse =
                mAuthStateManager.getCurrent().getLastRegistrationResponse();
        if (lastResponse != null) {
            Log.i(TAG, "Using dynamic client ID: " + lastResponse.clientId);
            // already dynamically registered a client ID
            mClientId.set(lastResponse.clientId);
            runOnUiThread(this::initializeAuthRequest);
            return;
        }

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        runOnUiThread(() -> displayLoading("Dynamically registering client"));
        Log.i(TAG, "Dynamically registering client");

        RegistrationRequest registrationRequest = new RegistrationRequest.Builder(
                mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                Collections.singletonList(mConfiguration.getRedirectUri()))
                .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
                .build();

        mAuthService.performRegistrationRequest(
                registrationRequest,
                this::handleRegistrationResponse);
    }

    @MainThread
    private void handleRegistrationResponse(
            RegistrationResponse response,
            AuthorizationException ex) {
        mAuthStateManager.updateAfterRegistration(response, ex);
        if (response == null) {
            Log.i(TAG, "Failed to dynamically register client", ex);
            displayErrorLater("Failed to register client: " + ex.getMessage(), true);
            return;
        }

        Log.i(TAG, "Dynamically registered client: " + response.clientId);
        mClientId.set(response.clientId);
        initializeAuthRequest();
    }

    /**
     * Performs the authorization request, using the browser selected in the spinner,
     * and a user-provided `login_hint` if available.
     */
    @WorkerThread
    private void doAuth() {
        try {
            mAuthIntentLatch.await();
        } catch (InterruptedException ex) {
            Log.w(TAG, "Interrupted while waiting for auth intent");
        }

        if (mUsePendingIntents) {
            Intent completionIntent = new Intent(this, LoginActivity.class);
            Intent cancelIntent = new Intent(this, LoginActivity.class);
            cancelIntent.putExtra(EXTRA_FAILED, true);
            cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            mAuthService.performAuthorizationRequest(
                    mAuthRequest.get(),
                    createPendingResult( RC_AUTH, completionIntent, 0),
                    createPendingResult(RC_AUTH, cancelIntent, 0),
                    mAuthIntent.get());
        } else {
            Intent intent = mAuthService.getAuthorizationRequestIntent(
                    mAuthRequest.get(),
                    mAuthIntent.get());
            startActivityForResult(intent, RC_AUTH);
        }
    }

    private void recreateAuthorizationService() {
        if (mAuthService != null) {
            Log.i(TAG, "Discarding existing AuthService instance");
            mAuthService.dispose();
        }
        mAuthService = createAuthorizationService();
        mAuthRequest.set(null);
        mAuthIntent.set(null);
    }

    private AuthorizationService createAuthorizationService() {
        Log.i(TAG, "Creating authorization service");
        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();
        builder.setBrowserMatcher(mBrowserMatcher);
        builder.setConnectionBuilder(mConfiguration.getConnectionBuilder());

        return new AuthorizationService(this, builder.build());
    }

    @MainThread
    private void fetchUserInfo() {
        displayLoading("Fetching user info");
        mAuthStateManager.getCurrent().performActionWithFreshTokens(mAuthService, this::fetchUserInfo);
    }

    @MainThread
    private void fetchUserInfo(String accessToken, String idToken, AuthorizationException ex) {
        if (ex != null) {
            Log.e(TAG, "Token refresh failed when fetching user info");
            mUserInfoJson.set(null);
            runOnUiThread(this::displayAuthorized);
            return;
        }

        AuthorizationServiceDiscovery discovery =
                mAuthStateManager.getCurrent()
                        .getAuthorizationServiceConfiguration()
                        .discoveryDoc;

        URL userInfoEndpoint;
        try {
            userInfoEndpoint =
                    mConfiguration.getUserInfoEndpointUri() != null
                            ? new URL(mConfiguration.getUserInfoEndpointUri().toString())
                            : new URL(discovery.getUserinfoEndpoint().toString());
        } catch (MalformedURLException urlEx) {
            Log.e(TAG, "Failed to construct user info endpoint URL", urlEx);
            mUserInfoJson.set(null);
            runOnUiThread(this::displayAuthorized);
            return;
        }

        mExecutor.submit(() -> {
            try {
                HttpURLConnection conn =
                        (HttpURLConnection) userInfoEndpoint.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setInstanceFollowRedirects(false);
                String response = Okio.buffer(Okio.source(conn.getInputStream()))
                        .readString(Charset.forName("UTF-8"));
                mUserInfoJson.set(new JSONObject(response));
            } catch (IOException ioEx) {
                Log.e(TAG, "Network error when querying userinfo endpoint", ioEx);
                Toast.makeText(getBaseContext(),"Fetching user info failed", Toast.LENGTH_SHORT).show();
            } catch (JSONException jsonEx) {
                Log.e(TAG, "Failed to parse userinfo response");
                Toast.makeText(getBaseContext(), "Failed to parse user info", Toast.LENGTH_SHORT).show();
            }

            runOnUiThread(this::displayAuthorized);
        });
    }

    @MainThread
    private void displayLoading(String loadingMessage) {
        loading_container.setVisibility(View.VISIBLE);
        auth_container.setVisibility(View.GONE);
        error_container.setVisibility(View.GONE);

        loading_description.setText(loadingMessage);
    }

    @MainThread
    private void displayError(String error, boolean recoverable) {
        error_container.setVisibility(View.VISIBLE);
        loading_container.setVisibility(View.GONE);
        auth_container.setVisibility(View.GONE);

        error_description.setText(error);
        retry.setVisibility(recoverable ? View.VISIBLE : View.GONE);
    }

    // WrongThread inference is incorrect in this case
    @SuppressWarnings("WrongThread")
    @AnyThread
    private void displayErrorLater(final String error, final boolean recoverable) {
        runOnUiThread(() -> displayError(error, recoverable));
    }

    @MainThread
    private void initializeAuthRequest() {
        createAuthRequest(getLoginHint());
        warmUpBrowser();
        displayAuthOptions();
    }

    @MainThread
    private void displayAuthOptions() {
        auth_container.setVisibility(View.VISIBLE);
        loading_container.setVisibility(View.GONE);
        error_container.setVisibility(View.GONE);
    }

    private void displayAuthCancelled() {
        Toast.makeText(
                this,
                "Authorization canceled",
                Toast.LENGTH_SHORT)
                .show();
    }

    private void warmUpBrowser() {
        mAuthIntentLatch = new CountDownLatch(1);
        mExecutor.execute(() -> {
            Log.i(TAG, "Warming up browser instance for auth request");
            CustomTabsIntent.Builder intentBuilder =
                    mAuthService.createCustomTabsIntentBuilder(mAuthRequest.get().toUri());
            intentBuilder.setToolbarColor(getColorCompat(R.color.colorPrimary));
            mAuthIntent.set(intentBuilder.build());
            mAuthIntentLatch.countDown();
        });
    }

    private void createAuthRequest(@Nullable String loginHint) {
        Log.i(TAG, "Creating auth request for login hint: " + loginHint);
        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
                mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                mClientId.get(),
                ResponseTypeValues.CODE,
                mConfiguration.getRedirectUri())
                .setScope(mConfiguration.getScope());

        if (!TextUtils.isEmpty(loginHint)) {
            authRequestBuilder.setLoginHint(loginHint);
        }

        mAuthRequest.set(authRequestBuilder.build());
    }

    private String getLoginHint() {
        return "";
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    private int getColorCompat(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(color);
        } else {
            return getResources().getColor(color);
        }
    }

    @MainThread
    private void displayNotAuthorized(String explanation) {
        error_container.setVisibility(View.VISIBLE);
        not_authorized.setVisibility(View.VISIBLE);
        authorized.setVisibility(View.GONE);
        loading_container.setVisibility(View.GONE);

        retry.setVisibility(View.GONE);

        Log.e(TAG, explanation);

    }

    @WorkerThread
    private void handleCodeExchangeResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {

        mAuthStateManager.updateAfterTokenResponse(tokenResponse, authException);
        if (!mAuthStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed"
                    + ((authException != null) ? authException.error : "");

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
            runOnUiThread(() -> displayNotAuthorized(message));
        } else {
            runOnUiThread(this::displayAuthorized);
        }
    }

    @MainThread
    private void refreshAccessToken() {
        displayLoading("Refreshing access token");
        performTokenRequest(
                mAuthStateManager.getCurrent().createTokenRefreshRequest(),
                this::handleAccessTokenResponse);
    }

    @MainThread
    private void performTokenRequest(
            TokenRequest request,
            AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = mAuthStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.i(TAG, "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
            displayNotAuthorized("Client authentication method is unsupported");
            return;
        }

        mAuthService.performTokenRequest(
                request,
                clientAuthentication,
                callback);
    }

    @WorkerThread
    private void handleAccessTokenResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {
        mAuthStateManager.updateAfterTokenResponse(tokenResponse, authException);
        runOnUiThread(this::displayAuthorized);
    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        displayLoading(getString(R.string.exchanging_authorization_code));
        performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);
    }

    @MainThread
    @OnClick({R.id.sign_out})
    public void signOut() {
        Log.w(TAG, "Signing out");
        clearAuthState();
        displayAuthOptions();
    }

    private void clearAuthState() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = mAuthStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mAuthStateManager.replace(clearedState);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        // user info is retained to survive activity restarts, such as when rotating the
        // device or switching apps. This isn't essential, but it helps provide a less
        // jarring UX when these events occur - data does not just disappear from the view.
        if (mUserInfoJson.get() != null) {
            state.putString(KEY_USER_INFO, mUserInfoJson.toString());
        }
    }
}
