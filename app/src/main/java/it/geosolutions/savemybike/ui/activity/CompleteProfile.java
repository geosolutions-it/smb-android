package it.geosolutions.savemybike.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;

import it.geosolutions.savemybike.AuthStateManager;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.AuthClient;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.user.User;
import it.geosolutions.savemybike.ui.utils.AuthUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteProfile extends SMBBaseActivity {
    private static final String TAG = "COMPLETEPROFILE";
    AuthStateManager mStateManager = AuthStateManager.getInstance(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
    }


    @Override
    public void onRequestPermissionGrant(PermissionIntent permissionIntent) {

    }

    public void onComplete(User user) {
        RetrofitClient client = RetrofitClient.getInstance(getBaseContext());
        SMBRemoteServices service = client.getPortalServices();
        Context context = this;
        service.updateUser(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent = new Intent(context, SaveMyBikeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG);
            }
        });
    }
}
