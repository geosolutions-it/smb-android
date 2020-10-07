package it.geosolutions.savemybike.sensors.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * BluetoothGattCallback implementation that holds an OperationQueue attribute,
 * in order to ensure that each read or write operation are executed when an eventually
 * previous one is terminated.
 */
public class SMBBluetoothGattCallback extends BluetoothGattCallback {

    private static final String TAG = BluetoothGattCallback.class.getSimpleName();

    private OperationQueue opQueue;

    public SMBBluetoothGattCallback (OperationQueue opQueue){
        this.opQueue=opQueue;
    }
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "Connected to GATT server.");
            // Attempts to discover services after successful connection.
            Log.i(TAG, "Attempting to start service discovery");
            BluetoothDevice device = gatt.getDevice();
            int bondState = device.getBondState();
            if (bondState == BluetoothDevice.BOND_NONE || bondState == BluetoothDevice.BOND_BONDED) {

                int delayWhenBonded = 0;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
                    delayWhenBonded = 1500;

                final int theDelay = bondState == BluetoothDevice.BOND_BONDED ? delayWhenBonded : 0;
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gatt.discoverServices();
                    }
                }, theDelay);
            }
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i(TAG, "Disconnected from GATT server.");
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.w(TAG, "onServicesDiscovered received: " + status);
        opQueue.completeOperation();
        opQueue.nextOperation();
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status) {
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        opQueue.completeOperation();
        opQueue.nextOperation();
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }
}
