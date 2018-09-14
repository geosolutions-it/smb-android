package it.geosolutions.savemybike.ui.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.geosolutions.savemybike.AuthStateManager;
import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.Util;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.data.service.SaveMyBikeService;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.CurrentStatus;
import it.geosolutions.savemybike.model.PaginatedResult;
import it.geosolutions.savemybike.model.Session;
import it.geosolutions.savemybike.model.Vehicle;
import it.geosolutions.savemybike.ui.BikeAdapter;
import it.geosolutions.savemybike.ui.callback.OnFragmentInteractionListener;
import it.geosolutions.savemybike.ui.fragment.BikeListFragment;
import it.geosolutions.savemybike.ui.fragment.RecordFragment;
import it.geosolutions.savemybike.ui.fragment.SessionsFragment;
import it.geosolutions.savemybike.ui.fragment.StatsFragment;
import it.geosolutions.savemybike.ui.fragment.TrackDetailsFragment;
import it.geosolutions.savemybike.ui.tasks.GetRemoteConfigTask;
import it.geosolutions.savemybike.ui.tasks.UpdateSessionsTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Robert Oehler on 25.10.17.
 * Edited by Lorenzo Pini on 2018.
 *
 * Main activity of the SaveMyBike app
 */
public class SaveMyBikeActivity extends SMBBaseActivity implements OnFragmentInteractionListener {

    private final static String TAG = "SaveMyBikeActivity";

    private final static int UI_UPDATE_INTERVAL = 1000;

    private SaveMyBikeService mService;

    private Vehicle currentVehicle;
    private boolean applyServiceVehicle = false;

    protected static final byte PERMISSION_REQUEST = 122;
    private Handler handler;
    private MReceiver mReceiver;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private boolean simulate = false;
    private boolean uploadWithWifiOnly = true;

    @BindView(R.id.navigation) BottomNavigationView navigation;

    @BindView(R.id.my_toolbar) Toolbar smbToolbar;

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;

    @BindView(R.id.nav_view) NavigationView navView ;

    private AuthorizationService mAuthService;
    private AuthStateManager mStateManager;
    // private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();
    private ExecutorService mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inflate
        setContentView(R.layout.drawer_layout);
        ButterKnife.bind(this);

        mStateManager = AuthStateManager.getInstance(this);
        mExecutor = Executors.newSingleThreadExecutor();


        it.geosolutions.savemybike.Configuration config = it.geosolutions.savemybike.Configuration.getInstance(this);
        if (config.hasConfigurationChanged()) {
            Log.w(TAG, "hasConfigurationChanged() == true");

            signOut();
            return;
        }

        mAuthService = new AuthorizationService(
                this,
                new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(config.getConnectionBuilder())
                        .build());



        setSupportActionBar(smbToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        changeFragment(0);
        //load the configuration and select the current vehicle
        this.currentVehicle = getCurrentVehicleFromConfig();

        //when online, update the config from remote
        if (Util.isOnline(getBaseContext())) {

            new GetRemoteConfigTask(getBaseContext(), new RetrofitClient.GetConfigCallback() {
                @Override
                public void gotConfig(final Configuration configuration) {

                    if (configuration != null) {

                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "config downloaded : " + configuration.id);
                        }

                        //save the config
                        Configuration.saveConfiguration(getBaseContext(), configuration);

                        //update model
                        SaveMyBikeActivity.this.configuration = configuration;
                        SaveMyBikeActivity.this.currentVehicle = getCurrentVehicleFromConfig();

                        runOnUiThread(() -> {
                            //invalidate UI
                            invalidateRecordingUI();
                        });

                    } else {
                        Log.e(TAG, "error downloading config ");
                    }
                }

                @Override
                public void error(String message) {
                    Log.e(TAG, "error downloading config " + message);
                }
            }, new RetrofitClient.GetBikesCallback() {
                @Override
                public void gotBikes(final PaginatedResult<Bike> bikesList) {

                    if (bikesList != null){
                        if (bikesList.getResults() != null) {

                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "Number of downloaded bikes: " + bikesList.getResults().size());
                            }

                            //save the config
                            Configuration.saveBikes(getBaseContext(), bikesList.getResults());

                        } else {
                            Log.w(TAG, "Wrong bikes response: server did not return a results array");
                            // Removing the bikes list
                            Configuration.saveBikes(getBaseContext(), new ArrayList<>());
                        }

                    } else {
                        Log.e(TAG, "Wrong bikes response: check for authentication or network errors");
                        // Removing the bikes list
                        Configuration.saveBikes(getBaseContext(), new ArrayList<>());
                    }
                }

                @Override
                public void error(String message) {
                    Log.e(TAG, "error downloading bikes: " + message);
                }
            }).execute();
        }
        else{
          Log.w(TAG, "*****  NETWORK NOT DETECTED  ******");
        }
        //else local config is used


        /*
         * Check if data can be uploaded
         *
         *   //for the upload we need the permission to write to the sd card
         *  TODO we may give an explanation for what the SD card access is necessary
         *  TODO we may ask the user for upload permission and only then check this sd-permission
         */
        this.uploadWithWifiOnly = preferences.getBoolean(Constants.PREF_WIFI_ONLY_UPLOAD, Constants.DEFAULT_WIFI_ONLY);

        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionNecessary(Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionIntent.SD_CARD))) {

            updateSessions();

        }
        // TODO: Initialize MapView to speedup first activity load.
        configureToolbar();
    }
    private void configureToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    private void configureNavigationDrawer() {


        navView.setNavigationItemSelectedListener((MenuItem menuItem) -> {
                Fragment f = null;
                int itemId = menuItem.getItemId();
                /*if (itemId == R.id.refresh) {
                    f = new RefreshFragment();
                } else if (itemId == R.id.stop) {
                    f = new StopFragment();
                }
                if (f != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, f);
                    transaction.commit();
                    drawerLayout.closeDrawers();
                    return true;
                }*/
                return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();



        //check if a session is active, if so bind to it
        final boolean isServiceRunning = isServiceRunning(getBaseContext(), Constants.SERVICE_NAME);
        if (isServiceRunning) {
            bindToService(new Intent(this, SaveMyBikeService.class));
            applyServiceVehicle = true;
        }
        //start updating the UI
        getHandler().removeCallbacks(mUpdateUITask);
        getHandler().postDelayed(mUpdateUITask, 10);

        //register receiver
        registerReceiver(getReceiver(), new IntentFilter(Constants.INTENT_STOP_FROM_SERVICE));
        registerReceiver(getReceiver(), new IntentFilter(Constants.INTENT_VEHICLE_UPDATE));

        //when not having an ongoing session, invalidate with the local vehicle
        if (!applyServiceVehicle) {
            invalidateRecordingUI();
        }
        //otherwise the UI update is done when re-binding to the service in @link onServiceConnected()
    }

    @Override
    protected void onStop() {
        super.onStop();

        //unbind the UI from the service
        if (mService != null) {
            unbindService(mServiceConnection);
        }
        //stop updating the UI
        getHandler().removeCallbacks(mUpdateUITask);

        //unregister receiver
        unregisterReceiver(getReceiver());
    }

    /**
     * invalidates the UI of the recording fragment if it is currently visible
     */
    private void invalidateRecordingUI() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment instanceof RecordFragment) {
            ((RecordFragment) currentFragment).invalidateUI(currentVehicle);
        }
    }

    /**
     * starts the recording of a session by launching the recording service and binding to it
     */
    public void startRecording() {

        //check location permission
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionNecessary(Manifest.permission.ACCESS_FINE_LOCATION, PermissionIntent.LOCATION))) {

            //start service using bindService
            Intent serviceIntent = new Intent(this, SaveMyBikeService.class);

            //TODO in a future update a session could be continued, pass the session id here
            //passing -1 indicates that a new session is started
            long continueId = -1;

            serviceIntent.putExtra(SaveMyBikeService.PARAM_SIMULATE, simulate);
            serviceIntent.putExtra(SaveMyBikeService.PARAM_CONTINUE_ID, continueId);
            serviceIntent.putExtra(SaveMyBikeService.PARAM_VEHICLE, currentVehicle);
            serviceIntent.putExtra(SaveMyBikeService.PARAM_CONFIG, getConfiguration());

            startService(serviceIntent);

            bindToService(serviceIntent);
        }
    }



    /**
     * stops recording a session
     */
    public void stopRecording() {

        if (mService != null) {
            mService.stopSession();
        }

        unbindService(mServiceConnection);
        //onServiceDisconnected is only called when service crashes, hence nullify service here
        mService = null;

        invalidateOptionsMenu();
    }

    /**
     * bind or rebind to a running service
     *
     * @param serviceIntent the intent for the service to bind to
     */
    private void bindToService(Intent serviceIntent) {

        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * the current connection to the recording service
     */
    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            if (binder instanceof SaveMyBikeService.SaveMyBikeBinder) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onServiceConnected");
                }
                mService = ((SaveMyBikeService.SaveMyBikeBinder) binder).getService();
                mService.setHandler(getHandler());
                if (applyServiceVehicle) {

                    Fragment currentFragment = getCurrentFragment();
                    if (currentFragment != null && currentFragment instanceof RecordFragment && mService.getSessionLogic() != null && mService.getSessionLogic().getVehicle() != null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "rebound to service, applying vehicle " + mService.getSessionLogic().getVehicle().toString());
                        }
                        ((RecordFragment) currentFragment).invalidateUI(mService.getSessionLogic().getVehicle());
                    }

                    applyServiceVehicle = false;
                }
                invalidateOptionsMenu();
            } else {
                Log.w(TAG, "unexpected : binder is no saveMyBikeBinder");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onServiceDisconnected");
            }
            mService = null;
        }
    };

    /**
     * navigation listener to switch between fragments
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_record:
                        changeFragment(0);
                        return true;
                    case R.id.navigation_stats:
                        changeFragment(1);
                        return true;
                    case R.id.navigation_bikes:
                        changeFragment(2);
                        return true;
                }
                return false;
            };

    /**
     * load fragment (if necessary) for index @param position
     *
     * @param position menu index
     */
    public void changeFragment(int position) {

        Fragment currentFragment = getCurrentFragment();

        Fragment fragment = null;
        switch (position) {
            case 0:
                navigation.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
                if (currentFragment != null && currentFragment instanceof RecordFragment) {
                    return;
                }
                fragment = new RecordFragment();
                break;
            case 1:
                if (currentFragment != null && currentFragment instanceof SessionsFragment) {
                    return;
                }
                fragment = new StatsFragment();
                break;
            case 2:
                if (currentFragment != null && currentFragment instanceof BikeListFragment) {
                    return;
                }
                fragment = new BikeListFragment();
                break;
            case 3:
                if (currentFragment != null && currentFragment instanceof TrackDetailsFragment) {
                    return;
                }
                fragment = new TrackDetailsFragment();
                break;
            case 4:

            default:
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    /**
     * changes the current vehicle in the configuration and updates the UI if the record fragment is currently visible
     *
     * @param vehicleType  the new vehicle type
     * @param setInService if to inform the service about the vehicle change
     */
    public void changeVehicle(Vehicle.VehicleType vehicleType, boolean setInService) {

        for (Vehicle vehicle : getConfiguration().getVehicles()) {
            if (vehicle.getType() == vehicleType) {
                vehicle.setSelected(true);
                Fragment currentFragment = getCurrentFragment();

                if (currentFragment != null && currentFragment instanceof RecordFragment) {
                    ((RecordFragment) currentFragment).selectVehicle(vehicle);
                }
                currentVehicle = vehicle;

                if (setInService && mService != null) {
                    mService.vehicleChanged(vehicle);
                }

            } else {
                vehicle.setSelected(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_record, menu);

        MenuItem mItem = menu.findItem(R.id.menu_simulate);
        mItem.setChecked(simulate);

        mItem = menu.findItem(R.id.menu_upload_wifi);
        mItem.setChecked(uploadWithWifiOnly);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_simulate:

                simulate = !simulate;
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment != null && currentFragment instanceof RecordFragment) {
                    ((RecordFragment) currentFragment).applySimulate(simulate);
                }
                invalidateOptionsMenu();
                return true;
            case R.id.menu_upload_wifi:

                uploadWithWifiOnly = !uploadWithWifiOnly;
                //save this setting
                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putBoolean(Constants.PREF_WIFI_ONLY_UPLOAD, uploadWithWifiOnly).apply();
                invalidateOptionsMenu();
                return true;
            case R.id.menu_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.logout_title);
                builder.setMessage(R.string.logout_message);
                builder.setPositiveButton(R.string.logout_OK, (dialog, which) -> {
                    signOut();
                    dialog.dismiss();
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * task to invalidate the UI, executes itself periodically every UI_UPDATE_INTERVAL
     */
    private Runnable mUpdateUITask = new Runnable() {

        public void run() {

            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null && currentFragment instanceof RecordFragment) {
                Session session = getCurrentSession();
                ((RecordFragment) currentFragment).invalidateSessionStats(session);
            }

            getHandler().postDelayed(this, UI_UPDATE_INTERVAL);

        }
    };

    /**
     * BroadcastReceiver to receive events from Notification
     */
    public class MReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == null) {
                Log.w(TAG, "unexpected intent action null");
                return;
            }

            if (intent.getAction().equals(Constants.INTENT_STOP_FROM_SERVICE)) {

                unbindService(mServiceConnection);
                //onServiceDisconnected is only called when service crashes, hence nullify service here
                mService = null;

                invalidateOptionsMenu();

                Fragment currentFragment = getCurrentFragment();
                if (currentFragment != null && currentFragment instanceof RecordFragment) {
                    ((RecordFragment) currentFragment).applySessionState(Session.SessionState.STOPPED);
                }
            } else if (intent.getAction().equals(Constants.INTENT_VEHICLE_UPDATE)) {
                // Here we are reacting to the notification vehicle change
                // the service has just been updated so we query it to get the new vehicle
                // then we tell the ui to update, without telling the service again it was updated
                if (mService != null) {
                    Vehicle newVehicle = mService.getCurrentVehicle();
                    if (newVehicle != null) {
                        changeVehicle(newVehicle.getType(), false);
                    }
                }
            }
        }
    }

    /**
     * checks if a service is running in the system
     *
     * @param context     a context
     * @param serviceName the package name of the service
     * @return true if running
     */
    public boolean isServiceRunning(@NonNull Context context, @NonNull final String serviceName) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the current session when available - null otherwise
     */
    public Session getCurrentSession() {

        if (mService != null && mService.getSessionLogic() != null && mService.getSessionLogic().getSession() != null) {

            return mService.getSessionLogic().getSession();
        }
        return null;
    }


    /**
     * gets the currently selected vehicle from the configuration
     *
     * @return the vehicle
     */
    public Vehicle getCurrentVehicleFromConfig() {

        for (Vehicle vehicle : getConfiguration().getVehicles()) {
            if (vehicle.isSelected()) {
                return vehicle;
            }
        }

        return null;
    }

    /**
     * @return the currently visible fragment
     */
    private Fragment getCurrentFragment() {

        return getSupportFragmentManager().findFragmentById(R.id.content);
    }

    /**
     * @return the currently used vehicle
     */
    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    /**
     * Manage both recurring tasks and messages from the background service
     * @return a handler
     */
    Handler getHandler() {
        if (handler == null) {
            handler = new HandlerExtension(this);
        }
        return handler;
    }

    public MReceiver getReceiver() {

        if (mReceiver == null) {
            mReceiver = new MReceiver();
        }

        return mReceiver;
    }

    // TODO: investigate possible memory leaks after confirming it works
    private static class HandlerExtension extends Handler {

        private final WeakReference<SaveMyBikeActivity> currentActivity;

        private HandlerExtension(SaveMyBikeActivity activity){
            currentActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message){
            SaveMyBikeActivity activity = currentActivity.get();
            if (activity!= null){
                activity.updateResults(message.getData().getString("result"));
            }
        }
    }

    public void updateResults(String results) {

        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment instanceof RecordFragment) {
            Session session = getCurrentSession();
            ((RecordFragment) currentFragment).invalidateSessionStats(session);
        }
    }

    @MainThread
    public void signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);

        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAuthService != null) {
            mAuthService.dispose();
            mAuthService = null;
        }

        if(mExecutor != null) {
            mExecutor.shutdownNow();
        }
    }



    @Override
    public boolean onSupportNavigateUp() {

        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionGrant(PermissionIntent permissionIntent) {
        //user did grant permission, what did we want to do ?
        switch (permissionIntent) {
            case LOCATION:
                startRecording();
                break;
            case SD_CARD:

                break;

        }
    }
    public void updateSessions() {
        new UpdateSessionsTask(this, new UpdateSessionsTask.SessionCallback() {
            @Override
            public void showProgressView() {

            }

            @Override
            public void hideProgressView() {

            }

            @Override
            public void done(Boolean success) {

            }
        }).execute();
    }

}
