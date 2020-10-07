package it.geosolutions.savemybike.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.sensors.ResponseSensorsCallback;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class QRFragment extends Fragment implements ZXingScannerView.ResultHandler {


    public static final String TAG = "QR";
    @BindView(R.id.scan_qr_button)
    ImageView qrButton;
    @BindView(R.id.loading_container)
    View loadingContainer;
    @BindView(R.id.qrFrame)
    FrameLayout frame;
    ZXingScannerView mScannerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_qr, container, false);
        ButterKnife.bind(this, view);
        mScannerView = new ZXingScannerView(getContext());
        mScannerView.setFormats(Arrays.asList(BarcodeFormat.QR_CODE));
        mScannerView.setAutoFocus(true);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopQrScan();
    }

    public void onStop() {
        super.onStop();
        stopQrScan();
    }

    @Override
    public void handleResult(Result rawResult) {
        mScannerView.stopCamera();
        frame.removeAllViews();
        qrButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_record_play_pause));
        handleRequest(rawResult.getText());
    }


    private void handleRequest(String id) {
        setLoading(true);
        RetrofitClient client = RetrofitClient.getInstance(this.getActivity());
        SMBRemoteServices portalServices = client.getPortalServices();
        ResponseSensorsCallback.JsonBikeConsumer command = new ResponseSensorsCallback.JsonBikeConsumer(id){

            @Override
            public void accept(String jsonBike) {
                setLoading(false);
                goToBikeFound(jsonBike);
            }
        };

        ResponseSensorsCallback callback = new ResponseSensorsCallback(command, getContext());
        client.performAuthenticatedCall(
                portalServices.getTaggedBike(id), callback);
    }


    @OnClick(R.id.scan_qr_button)
    public void buttonScanClicked() {
        if (checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    SaveMyBikeActivity.PERMISSION_REQUEST);

        }
        if (!mScannerView.isAttachedToWindow()) {
            startQrScan();
        } else {
            stopQrScan();
        }
    }

    private void startQrScan () {
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        frame.addView(mScannerView);
        qrButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_record_stop));
    }

    private void stopQrScan () {
        mScannerView.stopCamera();
        frame.removeView(mScannerView);
        qrButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_record_play_pause));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SaveMyBikeActivity.PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startQrScan();
                } else {
                    Toast.makeText(this.getActivity(), "Permission denied to read your Camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void setLoading(boolean loading) {

            if(loadingContainer != null) {
                if(loading) {
                    loadingContainer.setVisibility(View.VISIBLE);
                    if(loadingContainer.getParent()!=null)
                        ((ViewGroup)loadingContainer.getParent()).removeView(loadingContainer);
                    frame.addView(loadingContainer);
                } else {
                    loadingContainer.setVisibility(View.GONE);
                    frame.removeView(loadingContainer);
                }
        }
    }

    private void goToBikeFound(String bike){
        try {
            ((SaveMyBikeActivity) getActivity())
                    .changeFragment(R.id.bike_found_layout , bike);
        } catch (Exception e) {
            // and error happens when back button was pressed before save end.
            Log.e(TAG, "Error while exiting.", e);
        }
    }

}
