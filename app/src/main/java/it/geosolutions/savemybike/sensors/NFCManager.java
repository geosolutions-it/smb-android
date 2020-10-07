package it.geosolutions.savemybike.sensors;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.ui.activity.SMBBaseActivity;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.fragment.BikeFoundNotificationFragment;

import static it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity.EXTRA_DATA;

public class NFCManager {

    private static final String TAG = "NFCActivity";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private static final List<String> acceptedTechs= Arrays.asList("android.nfc.tech.NfcA","android.nfc.tech.MifareUltralight","android.nfc.tech.NdefFormatable");
    private NfcAdapter mNfcAdapter;
    private Activity context;

    public NFCManager(Activity context){
        this.context=context;
    }

    public void init () {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);

        if (mNfcAdapter == null) {
            Toast.makeText(context, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();

        }
        if (!((SMBBaseActivity) context).permissionNecessary(Manifest.permission.NFC, null))
            startNFC();

    }

    private void startNFC() {
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(context, "NFC is disabled", Toast.LENGTH_LONG).show();
        } else {
            handleIntent(context.getIntent());
        }
    }

    public void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                handleRequest(tag.getId().toString());
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            for (String tech : techList) {
                if (acceptedTechs.contains(tech)) {
                    byte [] id = tag.getId();
                    String uuid = bytesToHex(id);
                    handleRequest(uuid.replaceAll(" ",""));
                    break;
                }
            }

        }
    }

    private void handleRequest (String id){
        RetrofitClient client = RetrofitClient.getInstance(context);
        SMBRemoteServices portalServices = client.getPortalServices();
        ResponseSensorsCallback.JsonBikeConsumer command = new ResponseSensorsCallback.JsonBikeConsumer(id){

            @Override
            public void accept(String jsonBike) {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_DATA, jsonBike.toString());
                Fragment bikeFoundFragment = new BikeFoundNotificationFragment();
                bikeFoundFragment.setArguments(bundle);
                ((SaveMyBikeActivity)context).changeFragment(R.id.bike_found_layout, jsonBike);
            }
        };

        ResponseSensorsCallback callback = new ResponseSensorsCallback(command, context);

        client.performAuthenticatedCall(
                portalServices.getTaggedBike(id),callback);
    }


    public void setupForegroundDispatch() {
        final Intent intent = new Intent(context.getApplicationContext(), context.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        mNfcAdapter.enableForegroundDispatch(context, pendingIntent, filters, techList);
    }


    public void stopForegroundDispatch() {
        mNfcAdapter.disableForegroundDispatch(context);
    }

    private String getTagMessage (Tag tag){
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by the Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Unsupported Encoding", e);
                }
            }
        }
        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();

        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        int languageCodeLength = payload[0] & 0063;


        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

}
