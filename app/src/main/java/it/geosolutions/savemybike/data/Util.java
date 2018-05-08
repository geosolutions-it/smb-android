package it.geosolutions.savemybike.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Robert Oehler on 12.11.17.
 *
 */

public class Util {

    /**
     * converts an amount of milliseconds into a human readable String
     * @param time millis
     * @return the String
     */
    public static String longToTimeString(long time){

        boolean negative = false;
        if(time < 0){
            negative = true;
            time  = Math.abs(time);
        }

        String format = String.format(Locale.US,"%%0%dd", 2);
        String seconds = String.format(format, (time % Constants.ONE_MINUTE) / 1000);
        String minutes = String.format(format, (time % Constants.ONE_HOUR) / Constants.ONE_MINUTE);

        if(time < Constants.ONE_HOUR){
            return String.format(Locale.US,"%s%s:%s", negative ? "-" : "", minutes, seconds);
        }else{
            String hours = String.format(format, time / Constants.ONE_HOUR);
            return String.format(Locale.US,"%s%s:%s", negative ? "-" : "", hours, minutes);
        }

    }

    /**
     * creates if necessary the smb directory
     * @return if creating the dir was successful
     */
    public static boolean createSMBDirectory(){

        File exportDir = new File(Environment.getExternalStorageDirectory(), Constants.APP_DIR);

        if (!exportDir.exists()) {
            if(!exportDir.mkdirs()){
                Log.w("Util","Error creating SaveMyBike dir");
                return false;
            }
        }
        return true;

    }

    /**
     * @return the app's dir
     */
    public static File getSMBDirectory(){

        return new File(Environment.getExternalStorageDirectory(), Constants.APP_DIR);
    }

    /**
     * creates a file in the apps dir
     *
     * if the file does not exist it is created, otherwise deleted (overwritten)
     *
     * @param fileName name of the file to create
     * @return the file or null if an error occurred
     */
    public static File createFile(String fileName){

        if (!Util.createSMBDirectory()) {
            Log.w("App", "could not create app dir");
            return null;
        }

        File file = new File(Util.getSMBDirectory().getPath() + String.format(Locale.US, "/%s", fileName));

        boolean success = false;
        if(!file.exists()){
            try {
                success = file.createNewFile();
            } catch (IOException e) {
                Log.e("App", "error creating csv file " + fileName, e);
            }
        }else{
            success = file.delete();
        }
        if(success){
            return file;
        }else{
            return null;
        }
    }

    /**
     * checks if the device is online
     * @return true if online, false otherwise
     */
    public static boolean isOnline(@NonNull final Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

        } catch (Exception e) {
            Log.e("App", "error achieving online status", e);
            return false;
        }
    }
}
