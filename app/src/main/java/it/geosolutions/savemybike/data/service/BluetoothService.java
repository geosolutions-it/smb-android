package it.geosolutions.savemybike.data.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.sensors.ResponseSensorsCallback;
import it.geosolutions.savemybike.sensors.bluetooth.BluetoothBleManager;
import it.geosolutions.savemybike.sensors.bluetooth.BluetoothJobReceiver;
import it.geosolutions.savemybike.sensors.bluetooth.SMBScanCallback;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.geosolutions.savemybike.data.Constants.STOP_BLE;

public class BluetoothService extends Service {


    private static final String TAG = BluetoothService.class.getSimpleName();

    private BluetoothBleManager bluetoothManager;

    private SMBScanCallback callback;

    public static final String CHANNEL_ID = "BluetoothForegroundService";

    private static final int NOTIFICATION_ID = 222;

    public static boolean isRunning = false;


    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothManager = new BluetoothBleManager(this);
        callback=new SMBScanCallback(bluetoothManager);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        isRunning = true;
        if (bluetoothManager.initialize())
            if(!bluetoothManager.isScanning())
              bluetoothManager.startScan(callback);
        else {
                Log.e(TAG, "Unable to start scanning");
                stopSelf();
            }
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothManager.stopScan(callback);
        isRunning = false;

    }




    public void issueRequest(String uuid) {
        RetrofitClient client = RetrofitClient.getInstance(this);
        SMBRemoteServices portalServices = client.getPortalServices();
        ResponseSensorsCallback.JsonBikeConsumer command = new ResponseSensorsCallback.JsonBikeConsumer(uuid){

            @Override
            public void accept(String jsonBike) {
                bluetoothManager.stopScan(callback);
                Intent intent = new Intent(getApplicationContext(), BluetoothJobReceiver.class);
                intent.setAction(STOP_BLE);
                sendBroadcast(intent);
                UserNotificationManager notificationManager = UserNotificationManager.getInstance(getBaseContext());
                notificationManager.notifyNearLostBike(jsonBike);
            }

        };

        ResponseSensorsCallback respCallback = new ResponseSensorsCallback(command, this){
            @Override
            protected void onBikeNotFound(Bike bike) {
                super.onBikeNotFound(bike);
                bluetoothManager.startScan(callback);
            }
        };
        client.performAuthenticatedCall(
                portalServices.getTaggedBike(uuid), respCallback);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.enableVibration(true);
            android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    private Notification getNotification() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, SaveMyBikeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Intent jobReceiver = new Intent(getApplicationContext(), BluetoothJobReceiver.class);
        jobReceiver.setAction(STOP_BLE);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, jobReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action stopAction = new NotificationCompat.Action.Builder(R.drawable.ic_stop, getString(R.string.stop), pendingIntent2).build();
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.ble_notification))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.bluetooth_ic)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L})
                .addAction(stopAction)
                .build();
    }


}
