package it.geosolutions.savemybike.model;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import it.geosolutions.savemybike.data.Constants;

/**
 * Created by Robert Oehler on 26.10.17.
 *
 */

public class Configuration implements Serializable {

    public String id;
    public Integer version;

    public ArrayList<Vehicle> vehicles;

    public ArrayList<Bike> bikes;

    @SerializedName("persistanceInterval")
    public int persistanceInterval;
    @SerializedName("dataReadInterval")
    public int dataReadInterval;
    @SerializedName("metric")
    public boolean metric;


    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * loads the configuration
     * If a remote configuration was received and saved it is loaded from preferences and returned
     * otherwise the default config from res/raw is used
     * @param context a context
     * @return a Configuration
     */
    public static Configuration loadConfiguration(final Context context){

        final String currentConfig = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREF_CURRENT_CONFIG, null);

        if(currentConfig != null){

            Configuration conf = new Gson().fromJson(currentConfig, Configuration.class);
            if(conf != null){
                return conf;
            }
        }

        //load the default config
        final String jsonConf = loadJSONFromAsset(context);

        return new Gson().fromJson(jsonConf, Configuration.class);
    }

    /**
     * saves the config @param configuration as json to the preferences of context @param context
     * @param context a context
     * @param configuration a configuration to save
     */
    public static void saveConfiguration(final Context context, @NonNull final Configuration configuration){

        String json = new Gson().toJson(configuration);

        if(json != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.PREF_CURRENT_CONFIG, json).apply();
        }

    }

    /**
     * loads a file from the apps asset folder
     * @param context a context
     * @return the json
     */
    private static String loadJSONFromAsset(final Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(Constants.DEFAULT_CONFIGURATION_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e("Configuration", "error reading conf json from assets", e);
            return null;
        }
        return json;
    }

}
