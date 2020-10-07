package it.geosolutions.savemybike.sensors.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.geosolutions.savemybike.data.service.BluetoothService;

/**
 * @author Marco Volpini, Geosolutions S.a.s
 * This class manages the access to  the bluetooth scanner as well as connection to ble device.
 * Due the asynchronous nature of write and read characteristics, when a write or read operation is issued,
 * it should be added to the queue of Runnable in OperationQueue instance, to be executed later
 * in the Callback methods.
 */
public class BluetoothBleManager {

    private static final String TAG = BluetoothBleManager.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean active;

    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    protected ContextWrapper currentContext;

    private OperationQueue opQueue;

    public final static UUID UUID_SAVEMYBIKE_SERVICE = UUID.fromString("00001583-1212-efde-1523-785feabcd123");

    public final static UUID UUID_SAVEMYBIKE_CHARACTERISTIC = UUID.fromString("00001584-1212-efde-1523-785feabcd123");

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private int mConnectionState = STATE_DISCONNECTED;

    private boolean scanning = false;

    private boolean disabled=false;

    public BluetoothBleManager(ContextWrapper currentContext) {
        this.opQueue = new OperationQueue();
        this.currentContext = currentContext;
        this.mBluetoothManager = (BluetoothManager) currentContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (!currentContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            this.disabled=true;
        }
    }


    public boolean initialize() {
        if (!disabled) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
                return false;
            }
            if (!mBluetoothAdapter.isEnabled())
                mBluetoothAdapter.enable();
            active = true;
            return active;
        }
        return false;
    }

    private ScanSettings getSettings() {
        boolean isService = currentContext instanceof BluetoothService;
        ScanSettings.Builder builder = new ScanSettings.Builder();
        ScanSettings settings = builder.setScanMode(isService ? ScanSettings.SCAN_MODE_LOW_POWER : ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE).build();
        return settings;
    }

    private List<ScanFilter> getScanFilters() {
        List<ScanFilter> filters = new ArrayList<ScanFilter>(1);
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID_SAVEMYBIKE_SERVICE))
                .build();
        filters.add(filter);
        return filters;
    }

    public void startScan(SMBScanCallback smbScanCallback) {
        if (!disabled) {
            if (!mBluetoothAdapter.isEnabled())
                mBluetoothAdapter.enable();
            scanning = true;
            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
            ScanSettings settings = getSettings();
            List<ScanFilter> filters = getScanFilters();
            scanner.startScan(filters, settings, smbScanCallback);
        }
    }

    public void stopScan(SMBScanCallback smbScanCallback) {
        if (!disabled) {
            mBluetoothAdapter.getBluetoothLeScanner().flushPendingScanResults(smbScanCallback);
            if (isScanning()) {
                scanning = false;
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(smbScanCallback);
            }
        }
    }


    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState=STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(currentContext, true, new SMBBluetoothGattCallback(opQueue));
        mConnectionState=STATE_CONNECTING;
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }


    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (mConnectionState == STATE_CONNECTED || mConnectionState==STATE_CONNECTING) {
            mBluetoothGatt.disconnect();
            mConnectionState=STATE_DISCONNECTED;
        }
    }


    public String readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        String result = null;
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            result = stringBuilder.toString();
        }

        return result;
    }

    /**
     * Write a characteristic. Ensure this method is only called in a Runnable
     * added to the operations queue
     * @param characteristic
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void setAlarmMode(int mode) {
        this.opQueue.addCommand(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothGatt != null && mBluetoothGatt.getService(UUID_SAVEMYBIKE_SERVICE) != null) {
                    BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(UUID_SAVEMYBIKE_SERVICE).getCharacteristic(UUID_SAVEMYBIKE_CHARACTERISTIC);
                    if (characteristic != null) {
                        byte[] strBytes = new byte[2];
                        strBytes[0] = (byte) (0xA3);
                        switch (mode) {
                            case 0:
                                strBytes[1] = (byte) (0x00); //NONE
                                break;
                            case 1:
                                strBytes[1] = (byte) (0x01); //BUZZ
                                break;
                            case 2:
                                strBytes[1] = (byte) (0x02); //LED
                                break;
                            case 3:
                                strBytes[1] = (byte) (0x03); //BUZLED
                                break;
                        }
                        characteristic.setValue(strBytes);
                        writeCharacteristic(characteristic);
                    }
                }
            }
        });
    }

    /**
     * Start device
     */
    public void startDevice() {
        this.opQueue.addCommand(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothGatt != null && mBluetoothGatt.getService(UUID_SAVEMYBIKE_SERVICE) != null) {
                    BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(UUID_SAVEMYBIKE_SERVICE).getCharacteristic(UUID_SAVEMYBIKE_CHARACTERISTIC);
                    if (characteristic != null) {
                        byte[] strBytes = new byte[1];
                        strBytes[0] = (byte) (0xA1);
                        characteristic.setValue(strBytes);
                        writeCharacteristic(characteristic);
                    }
                }
            }
        });
    }

    /**
     * Stop device
     */
    public void stopDevice() {
        this.opQueue.addCommand(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothGatt != null && mBluetoothGatt.getService(UUID_SAVEMYBIKE_SERVICE) != null) {
                    BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(UUID_SAVEMYBIKE_SERVICE).getCharacteristic(UUID_SAVEMYBIKE_CHARACTERISTIC);
                    if (characteristic != null) {
                        byte[] strBytes = new byte[1];
                        strBytes[0] = (byte) (0xA2);
                        characteristic.setValue(strBytes);
                        writeCharacteristic(characteristic);
                    }
                }
            }
        });

        this.opQueue.addCommand(new Runnable() {
            @Override
            public void run() {
                disconnect();
            }
        });
    }


    public boolean isScanning() {
        return scanning;
    }


    /**
     * This BroadcastReceiver is needed to handle the BOND_BONDING state.
     * In that case the CallBack cannot start the service discovery process.
     * The changed status is then handled in this class.
     */
    private final BroadcastReceiver bondingStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mBluetoothGatt == null) {
                Log.w(TAG, "Bluetooth no more available does nothing");
                return;
            }
            String action = intent.getAction();
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (mBluetoothGatt != null && mBluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
                if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    int bondState = device.getBondState();
                    discoverServiceIfNotBonding(bondState);
                }
            }
        }

        private void discoverServiceIfNotBonding(int bondState) {
            switch (bondState) {
                case (BluetoothDevice.BOND_BONDING):
                    //does nothing
                    break;
                default:
                    // no more in bonding state, starts the services discovery
                    boolean result = mBluetoothGatt.discoverServices();
                    if (!result)
                        Log.e(TAG, "Error while starting service discovery");
            }
        }
    };

    public void setmConnectionState(int mConnectionState) {
        this.mConnectionState = mConnectionState;
    }

    public BroadcastReceiver getBondingStateReceiver() {
        return bondingStateReceiver;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public ContextWrapper getCurrentContext() {
        return currentContext;
    }
}
