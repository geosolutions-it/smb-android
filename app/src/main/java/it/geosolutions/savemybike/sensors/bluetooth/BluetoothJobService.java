package it.geosolutions.savemybike.sensors.bluetooth;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import it.geosolutions.savemybike.data.service.BluetoothService;

public class BluetoothJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), BluetoothService.class);
        if (!BluetoothService.isRunning)
            startForegroundService(service);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}
