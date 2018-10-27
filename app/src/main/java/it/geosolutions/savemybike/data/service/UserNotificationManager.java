package it.geosolutions.savemybike.data.service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.ui.activity.LoginActivity;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.adapters.BadgeAdapter;
import it.geosolutions.savemybike.ui.utils.BadgeUtils;

/**
 * Manager for user notification (tracks status update, badges, prizes).
 * TODO: merge with existing @see {@link it.geosolutions.savemybike.data.service.NotificationManager} that manages the permanent notification
 */
public class UserNotificationManager {
    private Context mCtx;
    private static UserNotificationManager mInstance;

    private static int TRACK_NOTIFICATION_ID = 42;
    private static int BADGE_NOTIFICATION_ID = 43;
    private static int PRIZE_NOTIFICATION_ID = 44;

    private UserNotificationManager(Context context) {
        mCtx = context;
        // create channels
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) (android.app.NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // valid tracks
            NotificationChannel validChanel = new NotificationChannel(Constants.Channels.TRACKS_VALID_ID, Constants.Channels.TRACKS_VALID_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            validChanel.setShowBadge(true);
            mNotificationManager.createNotificationChannel(validChanel);

            // invalid tracks
            NotificationChannel invalidChannel = new NotificationChannel(Constants.Channels.TRACK_INVALID_ID, Constants.Channels.TRACKS_INVALID_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            invalidChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(invalidChannel);
            // badge won
            NotificationChannel badgeWonChannel = new NotificationChannel(Constants.Channels.BADGES_WON_ID, Constants.Channels.BADGES_WON_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            badgeWonChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(badgeWonChannel);
            // prize won
            NotificationChannel prizeWonChannel = new NotificationChannel(Constants.Channels.PRIZES_WON_ID, Constants.Channels.PRIZES_WON_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            prizeWonChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(prizeWonChannel);
        }
    }
    public static synchronized UserNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserNotificationManager(context);
        }
        return mInstance;
    }
    public void notifyTrackValid() {
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) (android.app.NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.Channels.TRACKS_VALID_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setBadgeIconType(R.drawable.ic_line_track)
                        .setContentTitle(mCtx.getResources().getString(R.string.validation_success_title))
                        .setContentText(mCtx.getResources().getString(R.string.validation_success_description))
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(mCtx, SaveMyBikeActivity.class);
        resultIntent.putExtra(SaveMyBikeActivity.EXTRA_PAGE, SaveMyBikeActivity.EXTRA_TRACKS);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify( TRACK_NOTIFICATION_ID , mBuilder.build());
        }
    }
    public void notifyTrackInvalid(String reason) {
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) (android.app.NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.Channels.TRACK_INVALID_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setBadgeIconType(R.drawable.ic_line_track)
                        .setContentTitle(mCtx.getResources().getString(R.string.validation_error_title))
                        .setContentText(mCtx.getResources().getString(R.string.validation_error_description))
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(mCtx, SaveMyBikeActivity.class);
        resultIntent.putExtra(SaveMyBikeActivity.EXTRA_PAGE, SaveMyBikeActivity.EXTRA_TRACKS);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify( TRACK_NOTIFICATION_ID , mBuilder.build());
        }
    }

    public void handleBadgeWon(String badgeName) {
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) (android.app.NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.Channels.BADGES_WON_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setBadgeIconType(getBadgeIconByName(badgeName))
                        .setContentTitle(mCtx.getResources().getString(R.string.badge_won_title))
                        .setContentText(mCtx.getResources().getString(getBadgeTitleByName(badgeName)))
                        .setAutoCancel(true);
        Intent resultIntent = new Intent(mCtx, SaveMyBikeActivity.class);
        resultIntent.putExtra(SaveMyBikeActivity.EXTRA_PAGE, SaveMyBikeActivity.EXTRA_BADGES);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        if (mNotificationManager != null) {
            mNotificationManager.notify( BADGE_NOTIFICATION_ID, mBuilder.build());
        }
    }
    private int getBadgeTitleByName(String badgeName) {
        return BadgeUtils.NAME_TITLE_MAP.get(badgeName);
    }
    private int getBadgeIconByName(String badgeName) {
        return BadgeUtils.NAME_ICON_MAP.get(badgeName);
    }
    public void handlePrizeWon(String prizeName) {
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) (android.app.NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.Channels.PRIZES_WON_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setBadgeIconType(R.drawable.ic_trophy)
                        .setContentTitle(mCtx.getResources().getString(R.string.prize_won_title))
                        .setContentText(prizeName)
                        .setAutoCancel(true);
        Intent resultIntent = new Intent(mCtx, SaveMyBikeActivity.class);
        resultIntent.putExtra(SaveMyBikeActivity.EXTRA_PAGE, SaveMyBikeActivity.EXTRA_MY_PRIZES);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        if (mNotificationManager != null) {
            mNotificationManager.notify( PRIZE_NOTIFICATION_ID, mBuilder.build());
        }
    }



}
