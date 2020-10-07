package it.geosolutions.savemybike.ui.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.sensors.ResponseSensorsCallback;
import it.geosolutions.savemybike.sensors.bluetooth.BluetoothBleManager;
import it.geosolutions.savemybike.sensors.bluetooth.SMBScanCallback;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.callback.IOnBackPressed;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class BleFragment extends Fragment implements IOnBackPressed {

    public static final String TAG = "BleFragment";

    private BluetoothBleManager manager;

    private SMBScanCallback scanCallback;

    @BindView(R.id.alarm_none)
    Button alarmNone;
    @BindView(R.id.alarm_buzz)
    Button alarmBuzz;
    @BindView(R.id.alarm_led)
    Button alarmLed;
    @BindView(R.id.alarm_buzled)
    Button alarmBuzled;
    @BindView(R.id.alarm_off)
    Button alarmOff;
    @BindView(R.id.disconnect)
    Button disconnect;
    @BindView(R.id.bike_ble_title)
    TextView bikeName;
    @BindView(R.id.loading_container_ble)
    View loading;
    @BindView(R.id.ble_bike_frame)
    LinearLayout bikeFrame;

    private String bleAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        manager = new BluetoothBleManager(getActivity());
        manager.initialize();
        if (manager.isDisabled()) {
            Toast.makeText(getActivity(), R.string.ble_scan_unsupported, Toast.LENGTH_LONG).show();
            exit();
            return null;
        }
        registerReceiver(manager.getBondingStateReceiver());
        scanCallback = new SMBScanCallback(manager);
        final View view = inflater.inflate(R.layout.fragment_ble_bike, container, false);
        ButterKnife.bind(this, view);
        showLoading(true);
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!manager.isDisabled()) {
            if (!manager.isScanning() && bleAddress == null)
                manager.startScan(scanCallback);
            registerReceiver(manager.getBondingStateReceiver());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        manager.stopScan(scanCallback);
        unRegisterReceiver(manager.getBondingStateReceiver());
    }

    @Override
    public void onStop() {
        super.onStop();
        manager.stopScan(scanCallback);
        unRegisterReceiver(manager.getBondingStateReceiver());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showFrame(false);
        manager.stopScan(scanCallback);
        unRegisterReceiver(manager.getBondingStateReceiver());
    }

    public void issueRequest(String uuid) {
        RetrofitClient client = RetrofitClient.getInstance(getActivity());
        SMBRemoteServices portalServices = client.getPortalServices();
        client.performAuthenticatedCall(
                portalServices.getMyTaggedBike(uuid),
                new ResponseSensorsCallback(getContext()) {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String jsonBike = getJsonBikeFromResponseBody(response);
                        Bike bikeObj = jsonBike != null ? new Gson().fromJson(jsonBike, Bike.class) : null;
                        if (bikeObj != null) {
                            manager.stopScan(scanCallback);
                            bikeName.setText(bikeObj.getName() != null ? bikeObj.getName() : bikeObj.getNickname());
                            bleAddress = uuid;
                            if (bleAddress != null) {
                                manager.startDevice();
                                manager.connect(bleAddress);
                                showLoading(false);
                                showFrame(true);
                            }
                        }
                    }

                    @Override
                    protected void onBikeNotFound(Bike bike) {
                        super.onBikeNotFound(bike);
                        manager.startScan(scanCallback);
                    }
                }
        );
    }

    public void showLoading(boolean show) {
        loading.setVisibility(show? View.VISIBLE:View.GONE);
    }

    public void showFrame(boolean show){
        bikeFrame.setVisibility(show?View.VISIBLE:View.GONE);
    }

    @OnClick(R.id.alarm_none)
    public void alarmNoneClicked(){
        manager.setAlarmMode(0);
    }

    @OnClick(R.id.alarm_buzz)
    public void alarmBuzzClicked(){
        manager.setAlarmMode(1);
    }

    @OnClick(R.id.alarm_led)
    public void alarmLedClicked(){
        manager.setAlarmMode(2);
    }

    @OnClick(R.id.alarm_buzled)
    public void alarmBuzledClicked(){
        manager.setAlarmMode(3);
    }

    @OnClick(R.id.alarm_off)
    public void alarmOffClicked(){
        manager.stopDevice();
    }

    @OnClick(R.id.disconnect)
    public void disconnectClicked(){
        manager.disconnect();
        exit();
    }


    private void exit() {
        try {
            ((SaveMyBikeActivity) getActivity()).changeFragment(R.id.navigation_home);
        } catch (Exception e) {
            // and error happens when back button was pressed before save end.
            Log.e(TAG, "Error while exiting.", e);
        }
    }

    @Override
    public boolean onBackPressed() {
        manager.stopScan(scanCallback);
        exit();
        return true;
    }

    private void registerReceiver (BroadcastReceiver receiver){
        try {
            getActivity().registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        }catch(IllegalArgumentException iae){
            Log.w(TAG,"Receiver already registered");
        }
    }

    private void unRegisterReceiver (BroadcastReceiver receiver){
        try {
            getActivity().unregisterReceiver(receiver);
        }catch(IllegalArgumentException iae){
            Log.w(TAG,"Receiver already registered");
        }
    }
}
