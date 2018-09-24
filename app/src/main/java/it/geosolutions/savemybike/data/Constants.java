package it.geosolutions.savemybike.data;

/**
 * Created by Robert Oehler on 02.11.17.
 *
 */

public class Constants {

    public final static String PORTAL_ENDPOINT = "https://goodgo.savemybike.geo-solutions.it/";
    // public final static String PORTAL_ENDPOINT = "https://dev.savemybike.geo-solutions.it/"; // DEV

    /**
     * AWS method used for the current environment
     */
    public final static String UPLOAD_RESOURCE = "upload/";
    // public final static String UPLOAD_RESOURCE = "upload-dev/"; // DEV

    public final static int DEFAULT_DATA_READ_INTERVAL = 1000;
    public final static int DEFAULT_PERSISTENCE_INTERVAL = 15000;
    public final static boolean DEFAULT_WIFI_ONLY = true;

    public final static String APP_DIR = "SaveMyBike/";

    public final static String SESSION_FILE_NAME = "session_%d.txt";
    public final static String DATAPOINTS_FILE_NAME = "dataPoints_%s.txt";
    public final static String ZIP_FILE_NAME = "data_%d.zip";

    public final static long ONE_HOUR = 3600000;
    public final static long ONE_MINUTE = 60000;

    public final static double MAX_LATITUDE = 85.05112878;
    public final static double MIN_LATITUDE = -85.05112878;
    public final static double MAX_LONGITUDE = 180;
    public final static double MIN_LONGITUDE = -180;

    public static final String DEFAULT_CONFIGURATION_FILE  = "conf.json";
    public static final String DEFAULT_SESSION_NAME = "Session";

    public static final String SERVICE_NAME = "it.geosolutions.savemybike.data.service.SaveMyBikeService";

    public static final String NOTIFICATION_UPDATE_MODE = "it.geosolutions.savemybike.intent.mode";
    public static final String NOTIFICATION_UPDATE_STOP = "it.geosolutions.savemybike.intent.stop";

    public static final String INTENT_STOP_FROM_SERVICE = "it.geosolutions.savemybike.stop.from.service";
    public static final String INTENT_VEHICLE_UPDATE    = "it.geosolutions.savemybike.vehicle_update";

    public static final String PREF_WIFI_ONLY_UPLOAD         = "it.geosolutions.savemybike.pref.wifi_only";
    public static final String PREF_CURRENT_CONFIG           = "it.geosolutions.savemybike.pref.config";
    public static final String PREF_USERID                   = "it.geosolutions.savemybike.pref.username";
    public static final String PREF_CONFIG_ACCESSTOKEN       = "it.geosolutions.savemybike.pref.config.accessToken";
    public static final String PREF_CONFIG_IDTOKEN           = "it.geosolutions.savemybike.pref.config.idToken";
    public static final String PREF_CONFIG_REFRESHTOKEN      = "it.geosolutions.savemybike.pref.config.refreshToken";
    public static final String PREF_BIKES                    = "it.geosolutions.savemybike.pref.bikes";
    public final static String USER_PROFILE                  = "it.geosolutions.savemybike.pref.user";
    public final static String UNIT_KMH = "km/h";
    public final static String UNIT_MPH = "mph";
    public final static String UNIT_KM = "km";
    public final static String UNIT_MI = "mi";
    public final static String UNIT_M = "m";
    public final static String UNIT_FT = "ft";

    public final static float KM_TO_MILES = 0.621371192f;
    public final static float METER_TO_FEET = 3.2808399f;


}
