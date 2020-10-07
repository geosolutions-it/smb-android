package it.geosolutions.savemybike.sensors.bluetooth;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import it.geosolutions.savemybike.data.service.BluetoothService;

import static it.geosolutions.savemybike.data.Constants.APP_STARTED;
import static it.geosolutions.savemybike.data.Constants.BOOT_EVENT;
import static it.geosolutions.savemybike.data.Constants.STOP_BLE;

public class BluetoothJobReceiver extends BroadcastReceiver {

    private int  lastScheduledJob;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!jobAlreadyScheduled(context))
            scheduleJob(context);
        String action = intent.getAction();
        switch(action){
            case APP_STARTED:
                startService(context);
                break;
            case BOOT_EVENT:
                startService(context);
                break;
            case STOP_BLE:
                context.stopService(new Intent(context, BluetoothService.class));
                break;

        }

    }

    private void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, BluetoothJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setPeriodic(15 * 60 * 1000)
                .setPersisted(true)
                .setRequiresCharging(false);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        JobInfo jobInfo = builder.build();
        jobScheduler.schedule(jobInfo);
        lastScheduledJob=jobInfo.getId();
    }

    private boolean jobAlreadyScheduled (Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService( Context.JOB_SCHEDULER_SERVICE ) ;
        for ( JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == lastScheduledJob ) {
                return true;
            }
        }
        return false;
    }

    private void startService(Context context){
        if (!BluetoothService.isRunning)
            context.startForegroundService(new Intent(context, BluetoothService.class));
    }
}
