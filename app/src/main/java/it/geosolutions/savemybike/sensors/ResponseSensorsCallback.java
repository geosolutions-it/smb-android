package it.geosolutions.savemybike.sensors;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.Consumer;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.ui.activity.SaveMyBikeActivity;
import it.geosolutions.savemybike.ui.fragment.BleFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResponseSensorsCallback implements Callback<ResponseBody> {


    private JsonBikeConsumer command;
    private Context context;

    public ResponseSensorsCallback(JsonBikeConsumer command, Context context) {
        this.command = command;
        this.context = context;
    }

    public ResponseSensorsCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        String jsonBike = getJsonBikeFromResponseBody(response);

        Bike bike = jsonBike != null ? new Gson().fromJson(jsonBike, Bike.class) : null;

        if (bike != null && bike.getCurrentStatus().getLost()) {
            command.accept(jsonBike);
        } else {
            if (bike != null)
                Toast.makeText(context, R.string.bike_not_lost_message, Toast.LENGTH_SHORT).show();
            else
                Log.w(getClass().getSimpleName(), "No bike found with uuid " + command.getDeviceUUID());
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        if (context instanceof SaveMyBikeActivity) {
            SaveMyBikeActivity activity = (SaveMyBikeActivity) context;
            Fragment fragment = activity.getCurrentFragment();
            if (fragment instanceof BleFragment) {
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    protected String getJsonBikeFromResponseBody(Response<ResponseBody> response) {
        try {
            JSONObject obj = new JSONObject(response.body().string());
            JSONArray results = (JSONArray) obj.get("results");
            if (results.length() > 0) {
                return results.get(0).toString();
            }
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), "Exception while retrieving  bike with uuid " + command.getDeviceUUID());
        }
        return null;
    }

    public static abstract class JsonBikeConsumer implements Consumer<String> {

        private String deviceUUID;

        public JsonBikeConsumer(String deviceUUID) {
            this.deviceUUID = deviceUUID;
        }

        public String getDeviceUUID() {
            return deviceUUID;
        }

    }
}
