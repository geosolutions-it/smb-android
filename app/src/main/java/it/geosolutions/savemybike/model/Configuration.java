package it.geosolutions.savemybike.model;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.model.user.User;
import it.geosolutions.savemybike.model.user.UserInfo;

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
     * If a list of bikes was received and saved it is loaded from preferences and returned
     * @param context a context
     * @return a list of Bike
     */
    public static List<Bike> getBikes(final Context context){

        final String savedBikes = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREF_BIKES, null);

        if(savedBikes != null){

            Type listType = new TypeToken<ArrayList<Bike>>(){}.getType();
            List<Bike> bikesList = new Gson().fromJson(savedBikes, listType);
            if(bikesList != null){
                return bikesList;
            }
        }

        return new ArrayList<>();
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
     * saves the config @param configuration as json to the preferences of context @param context
     * @param context a context
     * @param bikesList list of bikes to store
     */
    public static void saveBikes(final Context context, @NonNull final List<Bike> bikesList){

        String json = new Gson().toJson(bikesList);

        if(json != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.PREF_BIKES, json).apply();
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

    /**
     * saves the config @param configuration as json to the preferences of context @param context
     * @param context a context
     * @param user the user to store
     */
    public static void saveUserProfile(final Context context, @NonNull final UserInfo user){

        String json = new Gson().toJson(user);

        if(json != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.USER_PROFILE, json).apply();
        }

    }
    /**
     * If a list of bikes was received and saved it is loaded from preferences and returned
     * @param context a context
     * @return a list of Bike
     */
    public static UserInfo getUserProfile(final Context context){

        final String userString = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.USER_PROFILE, null);

        if(userString != null){

            Type userType = new TypeToken<UserInfo>(){}.getType();
            UserInfo user = new Gson().fromJson(userString, userType);
            return user;
        }
    return null;

    }

}
