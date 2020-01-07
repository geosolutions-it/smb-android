package it.geosolutions.savemybike.data.server;

import android.util.Log;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.Util;
import it.geosolutions.savemybike.model.DataPoint;
import it.geosolutions.savemybike.model.Session;

/**
 * Created by Robert Oehler on 29.11.17.
 *
 * Creates csv files
 */

public class CSVCreator {

    private final static String TAG = "CSVCreator";


    /**
     * creates a CSV of a session
     *
     * 1. reads fields of Session (manually)
     * 2. writes a row with these field titles
     * 3. for the session fields writes a row of data
     *
     * @param session the session to write a CSV for
     * @return path to the created file or null if an error occurred
     */
    public String createCSV(final Session session){

        ArrayList<String> fieldNames = Session.getFieldNames();

        File sessionFile = Util.createFile(String.format(Locale.US,Constants.SESSION_FILE_NAME, session.getId()));

        if(sessionFile == null){
            return null;
        }

        FileWriter fw;
        try {
            fw = new FileWriter(sessionFile);

            //1.fields
            for(String field : fieldNames){
                fw.append(field);
                fw.append(',');
            }
            fw.append('\n');

            //2.data
            for (String field : fieldNames) {
                String value = Session.getValueForFieldName(field, session);
                fw.append(value);
                fw.append(',');
            }
            fw.append('\n');
            fw.flush();
        } catch (IOException e) {
            Log.e(TAG, "error creating fileWriter", e);
        }

        return sessionFile.getAbsolutePath();
    }





    /**
     * creates a CSV of dataPoints
     *
     * 1. reads fields of dataPoint via reflection
     * 2. writes a row with these field titles
     * 3. for every dataPoint and every field maps the field name to the field value and writes a row of data
     *
     * @param dataPoints dataPoints to create the CSV for
     * @param sessionId id of the session (used for file name)
     * @return path to the created file or null if an error occurred
     */
    public String createCSV(final ArrayList<DataPoint> dataPoints, String sessionId){

        File dataPointsFile = Util.createFile(String.format(Locale.US, Constants.DATAPOINTS_FILE_NAME, sessionId));

        if(dataPointsFile == null){
            return null;
        }

        final ArrayList<String> fieldNames = DataPoint.getFieldNames();
        FileWriter fw;
        try {
            fw = new FileWriter(dataPointsFile);

            //1.fields
            for(String field : fieldNames){
                fw.append(field);
                fw.append(',');
            }
            fw.append('\n');
            //2.data
            for(DataPoint dataPoint : dataPoints){
                for(String field : fieldNames){
                    String value = DataPoint.getValueForFieldName(field, dataPoint);
                    fw.append(value);
                    fw.append(',');
                }
                fw.append('\n');
            }
            fw.flush();

        } catch (IOException e) {
           Log.e(TAG, "error creating fileWriter", e);
        }


        return dataPointsFile.getAbsolutePath();
    }
}
