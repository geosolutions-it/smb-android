package it.geosolutions.savemybike;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.service.UserNotificationManager;
import it.geosolutions.savemybike.ui.activity.LoginActivity;

/**
 * Manages Firebase messages
 */
public class FirebaseService extends FirebaseMessagingService {
    private static final String TAG = FirebaseService.class.getSimpleName();
    public static final String MESSAGE_NAME_KEY = "message_name";

    public static final class MESSAGE_TYPES {
        public static final String TRACK_VALIDATED = "track_validated";
        public static final String BADGE_WON = "badge_won";
        public static final String PRIZE_WON = "prize_won";
    }
    public static final class VALIDATION_KEYS {
        public static final String IS_VALID = "is_valid";
        public static final String VALIDATION_ERRORS = "validation_errors";
    }
    public static final class NOTIFICATION_KEYS {
        public static final String BADGE_NAME = "badge_name";
        public static final String PRIZE_NAME = "prize_name";
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        storeRegistration(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            remoteMessage.getData();
            String msgName = remoteMessage.getData().get(MESSAGE_NAME_KEY);
            switch (msgName) {
                case MESSAGE_TYPES.TRACK_VALIDATED: {
                    String validString = remoteMessage.getData().get(VALIDATION_KEYS.IS_VALID);
                    if(validString != null && "false".equalsIgnoreCase(validString)) {
                        handleInvalid(remoteMessage.getData().get(VALIDATION_KEYS.VALIDATION_ERRORS));
                    } else {
                        handleValid();
                    }
                    break;
                }
                case MESSAGE_TYPES.BADGE_WON: {
                    handleBadgeWon(remoteMessage.getData().get(NOTIFICATION_KEYS.BADGE_NAME));
                    break;
                }
                case MESSAGE_TYPES.PRIZE_WON: {
                    handlePrizeWon(remoteMessage.getData().get(NOTIFICATION_KEYS.PRIZE_NAME));
                    break;
                }
            }
        } else {
            // TODO: manage generic notification to the user
        }
    }

    /**
     * Handles a notification of an invalid track error
     * @param errors
     */
    public void handleInvalid(String errors ) {
        getUserNotificationManager().notifyTrackInvalid(errors);
        // send broadcast to update UI
        Intent intent = new Intent();
        intent.setAction(MESSAGE_TYPES.TRACK_VALIDATED);
        sendBroadcast(intent);
    }
    /**
     * Handles a notification of an valid track
     */
    public void handleValid() {
        getUserNotificationManager().notifyTrackValid();

        // send broadcast to update UI
        Intent intent = new Intent();
        intent.setAction(MESSAGE_TYPES.TRACK_VALIDATED);
        sendBroadcast(intent);
    }

    public void handleBadgeWon(String badgeName) {
        getUserNotificationManager().handleBadgeWon(badgeName);
    }
    public void handlePrizeWon(String prizeName) {
        getUserNotificationManager().handlePrizeWon(prizeName);
    }
    public UserNotificationManager getUserNotificationManager() {
        return UserNotificationManager.getInstance(getBaseContext());
    }
    void storeRegistration(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String oldToken = sharedPreferences.getString(Constants.FIREBASE_INSTANCE_ID, null);
        if(oldToken != null) {
            // TODO: manage token update
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.FIREBASE_INSTANCE_ID, token);
        editor.apply();
    }

}
