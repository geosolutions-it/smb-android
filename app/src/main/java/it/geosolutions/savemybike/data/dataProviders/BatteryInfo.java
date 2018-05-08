package it.geosolutions.savemybike.data.dataProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import it.geosolutions.savemybike.data.service.SaveMyBikeService;

/**
 * Created by Robert Oehler on 08.11.17.
 *
 * Class which receives the current battery level from the system
 *
 * it evaluates the value and calculates the current battery consumption of the device
 */

public class BatteryInfo extends BroadcastReceiver implements IDataProvider {

    private final static int ONE_HOUR = 1000 * 60 * 60;

    private SaveMyBikeService service;

    private long batteryChangeTime;
    private int lastBatteryLevel;
    private boolean isRegistered = false;

    public BatteryInfo(final SaveMyBikeService saveMyBikeService){

        this.service = saveMyBikeService;
    }

    @Override
    public void start() {

        startListening();
    }

    @Override
    public void stop() {

        stopListening();
    }

    private void stopListening() {

        if(isRegistered) {
            service.unregisterReceiver(this);
            isRegistered = false;
        }
    }
    private void startListening() {

        if(!isRegistered) {
            service.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            isRegistered = true;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final int batteryLevel = intent.getIntExtra("level", -1);

        if (batteryLevel != lastBatteryLevel) {
            if (lastBatteryLevel != 0) {

                if (service.getSessionLogic() != null && service.getSessionLogic().getSession() != null) {

                    service.getSessionLogic().getSession().getCurrentDataPoint().batteryLevel = batteryLevel;

                    if (batteryChangeTime != 0) {

                        long onePercentTime = System.currentTimeMillis() - batteryChangeTime;
                        float consumptionPerHour = ONE_HOUR / (float) onePercentTime;

                       service.getSessionLogic().getSession().getCurrentDataPoint().batConsumptionPerHour = consumptionPerHour;
                    }
                }
            }
            lastBatteryLevel = batteryLevel;
            batteryChangeTime = System.currentTimeMillis();
        }
    }

    @Override
    public String getName() {
        return "BatteryInfo";
    }
}
