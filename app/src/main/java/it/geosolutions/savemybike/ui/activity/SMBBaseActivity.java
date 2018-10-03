package it.geosolutions.savemybike.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;

import it.geosolutions.savemybike.AuthStateManager;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.AuthClient;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.ui.utils.AuthUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SMBBaseActivity extends AppCompatActivity {
    public static final String TAG = "SMBBASEACTIVITY";
    protected static final byte PERMISSION_REQUEST = 122;
    public enum PermissionIntent {
        LOCATION,
        SD_CARD
    }

    protected Configuration configuration;

    private PermissionIntent mPermissionIntent;

    public abstract void onRequestPermissionGrant(PermissionIntent permissionIntent);

    /**
     * gets the configuration - if it is null it is loaded
     *
     * @return the configuration
     */
    public Configuration getConfiguration() {
        if (configuration == null) {
            configuration = Configuration.loadConfiguration(getBaseContext());
        }
        return configuration;
    }

    /**
     * ////////////// ANDROID 6 permissions /////////////////
     * checks if the permission @param is granted and if not requests it
     *
     * @param permission the permission to check
     * @return if the permission is necessary
     */
    public boolean permissionNecessary(final String permission, final PermissionIntent intent) {

        boolean required = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED;

        if (required) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST);
            mPermissionIntent = intent;
            return true;
        }

        return false;
    }
    /**
     * ////////////// ANDROID 6 permissions /////////////////
     * returns the result of the permission request
     *
     * @param requestCode  a requestCode
     * @param permissions  the requested permission
     * @param grantResults the result of the user decision
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (PERMISSION_REQUEST == requestCode) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                //the permission was denied by the user, show a message
                if (permissions.length > 0) {
                    //sdcard
                    //TODO show a message or quit app when external storage (data upload) was denied ?
                    if (!permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                            (permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) || permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION))) {
                        //location
                        Toast.makeText(getBaseContext(), R.string.permission_location_required, Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }

            //user did grant permission, what did we want to do ?
            onRequestPermissionGrant(mPermissionIntent);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* ************ LOGIN - LOGOUT UTILITIES ******************************/
    // TODO: externailze Login/Logout utilities

    public void clearAuthState() {
        AuthStateManager mStateManager = AuthStateManager.getInstance(this);
        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());

        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);
    }

    public void backToLogin() {
        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    public void logout() {
        AuthStateManager mStateManager = AuthStateManager.getInstance(this);
        AuthState mAuthState = mStateManager.getCurrent();
        String accessToken = mAuthState.getAccessToken();
        RetrofitClient client = RetrofitClient.getInstance(getBaseContext());
        AuthClient authClient = client.getAuthClient();
        AuthorizationServiceConfiguration asc = mAuthState.getAuthorizationServiceConfiguration();
        AuthorizationServiceDiscovery discoveryDoc = asc.discoveryDoc;
        if (discoveryDoc == null) {
            throw new IllegalStateException("no available discovery doc");
        }

        Uri endSessionEndpoint = AuthUtils.getEndSessionEndpoint(discoveryDoc);
        authClient
                .logout(
                        endSessionEndpoint.toString(),
                        it.geosolutions.savemybike.Configuration.getInstance(getBaseContext()).getRedirectUri().toString()
                        ,accessToken)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // reset saved user to re-ask for user info on next login (e.g. profile completed)
                        Configuration.saveUserProfile(getBaseContext(), null );
                        clearAuthState();
                        backToLogin();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Error doing logout", t);
                        if(!isNetworkAvailable()) {
                            Toast.makeText(getBaseContext(), R.string.could_not_logout, Toast.LENGTH_LONG);
                        } else {
                            Toast.makeText(getBaseContext(), R.string.logout_generic_issue, Toast.LENGTH_LONG);
                        }
                    }
                });
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
