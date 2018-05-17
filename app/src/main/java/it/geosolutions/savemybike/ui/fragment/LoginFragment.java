package it.geosolutions.savemybike.ui.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoAccessToken;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoIdToken;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoRefreshToken;
import com.amazonaws.regions.Regions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;

/**
 * Created by Lorenzo Pini on 09.03.18
 * Based on https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 * <p>
 * UI for login
 */

public class LoginFragment extends Fragment {

    private final static String TAG = "LoginFragment";

    // private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_username)     EditText _usernameText;
    @BindView(R.id.input_password)  EditText _passwordText;
    @BindView(R.id.btn_login)       Button _loginButton;
    @BindView(R.id.btn_test)        Button _testButton;
    // @BindView(R.id.link_signup) TextView _signupLink;

    private ProgressDialog progressDialog;

    /**
     * inflates the view of this fragment and initializes it
     *
     * @return the inflated view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_login)
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(getActivity(),
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        tryAuthentication();

    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        // finish();
        ((SaveMyBikeActivity) getActivity()).changeFragment(0);
    }

    public void onLoginFailed() {
        Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() /*|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()*/) {
            _usernameText.setError("enter a valid email address");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    @OnClick(R.id.btn_test)
    public void test() {
        Log.d(TAG, "Test");
        tryAuthentication();
    }

    void tryAuthentication() {
        // Create a CognitoUserPool object to refer to your user pool
        CognitoUserPool userPool = new CognitoUserPool((getActivity()), Constants.AWS_POOL, Constants.AWS_CLIENT_ID_WO_SECRET, null, Regions.US_WEST_2);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String userId = _usernameText.getText().toString();
        final String accessTokenString = preferences.getString(Constants.PREF_CONFIG_IDTOKEN, null);
        final String idTokenString = preferences.getString(Constants.PREF_CONFIG_ACCESSTOKEN, null);
        final String refreshTokenString = preferences.getString(Constants.PREF_CONFIG_REFRESHTOKEN, null);

        CognitoUser cognitoUser = userPool.getUser(userId);

        SharedPreferences.Editor ed = preferences.edit();
        ed.putString(Constants.PREF_USERID, cognitoUser.getUserId());
        ed.apply();

        CognitoAccessToken accessToken = new CognitoAccessToken(accessTokenString);
        CognitoIdToken idToken = new CognitoIdToken(idTokenString);
        CognitoRefreshToken refreshToken = new CognitoRefreshToken(refreshTokenString);

        CognitoUserSession previousSession = new CognitoUserSession(idToken,accessToken,refreshToken);

        if(!previousSession.isValid()) {
            Log.d(TAG, "Invalid session, re-authenticate");

            // Sign in the user
            cognitoUser.getSessionInBackground(authenticationHandler);

            // Fetch the user details
            cognitoUser.getDetailsInBackground(getDetailsHandler);
        }else{
            Log.d(TAG, "STILL VALID!");
            cognitoUser.getDetailsInBackground(getDetailsHandler);
        }
    }

    // TODO: move to a longer lived object

    // Callback handler for the sign-in process
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice newDevice) {
            // Sign-in was successful, cognitoUserSession will contain tokens for the user
            Log.d(TAG, "onSuccess");

            String accessToken = cognitoUserSession.getAccessToken().getJWTToken();
            String idToken = cognitoUserSession.getIdToken().getJWTToken();
            String refreshToken = cognitoUserSession.getRefreshToken().getToken();

            Log.d(TAG, accessToken);
            Log.d(TAG, idToken);
            Log.d(TAG, refreshToken);

            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            ed.putString(Constants.PREF_CONFIG_ACCESSTOKEN, accessToken);
            ed.putString(Constants.PREF_CONFIG_IDTOKEN, idToken);
            ed.putString(Constants.PREF_CONFIG_REFRESHTOKEN, refreshToken);
            ed.apply();

            if (newDevice != null) {
                if(newDevice.getDeviceName() != null) Log.d(TAG, newDevice.getDeviceName());
                if(newDevice.getDeviceKey() != null) Log.d(TAG, newDevice.getDeviceKey());
            }

            onLoginSuccess();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {

            String password = _passwordText.getText().toString();

            Log.d(TAG, "Using the password: "+ password);
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, password, null);

            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);

            // Allow the sign-in to continue
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            Log.d(TAG, "getMFACode");
            String mfaVerificationCode = "dummy";
            // Multi-factor authentication is required; get the verification code from user
            multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
            multiFactorAuthenticationContinuation.continueTask();
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            Log.d(TAG, "authenticationChallenge");

            Log.d(TAG, continuation.getChallengeName());

        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-in failed, check exception for the cause
            Log.e(TAG, "onFailure", exception);
            onLoginFailed();
        }
    };

    // Implement callback handler for getting details
    GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            // The user detail are in cognitoUserDetails
            for (String s : cognitoUserDetails.getAttributes().getAttributes().keySet()) {
                Log.d(TAG, s + " : " + cognitoUserDetails.getAttributes().getAttributes().get(s));
            }
        }

        @Override
        public void onFailure(Exception exception) {
            // Fetch user details failed, check exception for the cause
            Log.d(TAG, "FAILED DETAILS", exception);
        }
    };

}
