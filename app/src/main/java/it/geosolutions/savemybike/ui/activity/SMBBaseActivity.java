package it.geosolutions.savemybike.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.S3Manager;
import it.geosolutions.savemybike.model.Configuration;

public abstract class SMBBaseActivity extends AppCompatActivity {
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


}
