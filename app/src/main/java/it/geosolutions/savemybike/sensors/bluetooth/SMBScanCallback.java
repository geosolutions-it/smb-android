package it.geosolutions.savemybike.sensors.bluetooth;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContextWrapper;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import it.geosolutions.savemybike.data.service.BluetoothService;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.fragment.BleFragment;

/**
 * A scan callback handling the scan results.
 * A new instance is needed for each class calling startScan
 */
public class SMBScanCallback extends ScanCallback {

    private static final String TAG = SMBScanCallback.class.getSimpleName();

    protected BluetoothBleManager manager;

    private String lastMACSent="";

    public SMBScanCallback (BluetoothBleManager manager){
        this.manager=manager;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        this.manager.stopScan(this);
        handleScanResult(Arrays.asList(result.getDevice().getAddress()));
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        this.manager.stopScan(this);
        List<String> addresses = results.stream().map(s -> s.getDevice().getAddress()).collect(Collectors.toList());
        handleScanResult(addresses);
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
        Log.e(TAG,"Scan failed with error code "+errorCode);
        manager.stopScan(this);
    }

    private void handleScanResult(List<String> addresses) {
        ContextWrapper context=manager.getCurrentContext();
        if (context instanceof BluetoothService) {
            for (String a: addresses) {
                a="FF:0E:50:AD:8D:69";
                if (!a.equals(lastMACSent)) {
                    ((BluetoothService) context).issueRequest(a);
                    lastMACSent = a;
                }
            }
        } else if (context instanceof SaveMyBikeActivity) {
            SaveMyBikeActivity activity = (SaveMyBikeActivity) context;
            Fragment fragment = activity.getCurrentFragment();
            if (fragment instanceof BleFragment){
                for (String a: addresses) {
                    if (!a.equals(lastMACSent)) {
                        ((BleFragment)fragment).issueRequest(a);
                        lastMACSent = a;
                    }
                }
            }
        }
        lastMACSent="";
    }
}
