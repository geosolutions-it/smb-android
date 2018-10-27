package it.geosolutions.savemybike.data.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.util.Log;

import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.model.Vehicle;
import it.geosolutions.savemybike.ui.VehicleUtils;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;

/**
 * Created by Robert Oehler on 14.11.17.
 *
 * A manager for the notification shown during a session
 *
 * Be careful to use android.support.v7.app.NotificationCompat and NOT android.support.v4.app.NotificationCompat
 * as otherwise notification buttons won't be visible !
 */

public class NotificationManager extends BroadcastReceiver {

    private final static String TAG = "NotificationManager";

    private final static String CHANNEL_ID = "it.geosolutions.android.SaveMyBike";
    private final static String CHANNEL_NAME = "SaveMyBike";

    public static final int NOTIFICATION_ID = 111;
    private static final int REQUEST_CODE = 100;

    private SaveMyBikeService mService;
    private final android.app.NotificationManager mNotificationManager;

    private final PendingIntent mModeIntent;
    private final PendingIntent mStopIntent;

    private boolean mStarted = false;

    private String mCurrentMessage;
    private Vehicle mVehicle;

    public NotificationManager(final SaveMyBikeService service){

        mService = service;
        mNotificationManager = (android.app.NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_LOW);
            mChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        String pkg = mService.getPackageName();
        mModeIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(Constants.NOTIFICATION_UPDATE_MODE).setPackage(pkg),PendingIntent.FLAG_CANCEL_CURRENT);
        mStopIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(Constants.NOTIFICATION_UPDATE_STOP).setPackage(pkg),PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll();
    }

    /**
     * Posts the notification and starts tracking the session to keep it
     * updated. The notification will automatically be removed if the session is
     * destroyed before {@link #stopNotification} is called.
     */
    public Notification startNotification(final String message, Vehicle vehicle) {
        if (!mStarted) {

            mCurrentMessage = message;
            mVehicle = vehicle;

            // The notification must be updated after setting started to true
            Notification notification = createNotification();
            if (notification != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Constants.NOTIFICATION_UPDATE_MODE);
                filter.addAction(Constants.NOTIFICATION_UPDATE_STOP);
                mService.registerReceiver(this, filter);
                mService.startForeground(NOTIFICATION_ID, notification);
                mStarted = true;
                return notification;
            }
        }
        return null;
    }

    /**
     * updates the notification with @param message and applies @param vehicle by changing the icon
     * @param message the message to update
     * @param vehicle the vehicle to update
     */
    public void updateNotification(final String message, Vehicle vehicle) {
        this.mCurrentMessage = message;
        this.mVehicle = vehicle;
        mNotificationManager.notify(NOTIFICATION_ID, createNotification());
    }


    /**
     * Removes the notification and stops tracking the session. If the session
     * was destroyed this has no effect.
     */
    public void stopNotification() {

        if (mStarted) {
            mStarted = false;
            try {
                mNotificationManager.cancel(NOTIFICATION_ID);
                mService.unregisterReceiver(this);
            } catch (IllegalArgumentException ex) {
                // ignore if the receiver is not registered.
            }
            mService.stopForeground(true);
        }
    }

    /**
     * receives the pending intent when the user interacts with the notification
     * @param context a context
     * @param intent the intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        if(action == null){
            Log.w(TAG, "unexpected intent action null");
            return;
        }

        if(BuildConfig.DEBUG) {
            Log.d(TAG, "Received intent with action " + action);
        }

        switch (action) {

            case Constants.NOTIFICATION_UPDATE_MODE:

                // change state
                int currentType = mVehicle.getType().ordinal();
                currentType++;
                if(currentType >= Vehicle.VehicleType.values().length){
                    currentType = 0;
                }
                //inform service about update
                mService.vehicleChanged(mService.vehicleFromType(currentType));
                //inform UI (if available) about update
                mService.sendBroadcast(new Intent(Constants.INTENT_VEHICLE_UPDATE));

                break;
            case Constants.NOTIFICATION_UPDATE_STOP:

                //inform service about stop
                mService.stopSession();
                //inform UI (if available) about stop
                mService.sendBroadcast(new Intent(Constants.INTENT_STOP_FROM_SERVICE));

                break;
            default:
                Log.w(TAG, "Unknown intent ignored. Action = " + action);
        }
    }

    /**
     * creates the notification for the SaveMyBike service
     * @return the created notification
     */
    private Notification createNotification(){

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mService, CHANNEL_ID);
        int modeSrc = VehicleUtils.getDrawableForVeichle(mVehicle.getType());
        // not sure it is really needed, this is only a porting of the old behiviour corner case
        if(modeSrc == R.drawable.ic_home) {
            modeSrc = R.drawable.ic_directions_bike;
        }

        NotificationCompat.Action modeAction = new NotificationCompat.Action.Builder(modeSrc, mService.getString(R.string.mode), mModeIntent).build();
        NotificationCompat.Action stopAction = new NotificationCompat.Action.Builder(R.drawable.ic_stop, mService.getString(R.string.stop), mStopIntent).build();

        notificationBuilder
                .addAction(modeAction)
                .addAction(stopAction)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setStyle(new MediaStyle()
                        .setShowActionsInCompactView(0,1))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentIntent(createContentIntent())
                .setContentTitle(mCurrentMessage)
                .setContentText(
                        mService.getBaseContext().getResources().getString(
                                VehicleUtils.getVehicleName(mService.getSessionLogic().getVehicle().getType())
                        )
                );


        return notificationBuilder.build();
    }

    /**
     * creates the intent which is fired when the notification itself (not the buttons) is clicked
     * -> will launch the activity
     * @return the pending intent
     */
    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mService, SaveMyBikeActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        openUI.putExtra(SaveMyBikeActivity.EXTRA_PAGE, SaveMyBikeActivity.EXTRA_RECORD);

        return PendingIntent.getActivity(mService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
