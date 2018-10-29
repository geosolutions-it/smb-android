package it.geosolutions.savemybike.data.server;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.data.Util;
import it.geosolutions.savemybike.data.db.SMBDatabase;
import it.geosolutions.savemybike.data.service.UserNotificationManager;
import it.geosolutions.savemybike.model.Session;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Robert Oehler on 27.11.17.
 *
 * Handles uploads
 */

public class S3Manager implements TransferListener{

    private final static String TAG = "S3Manager";

    private Context context;
    private boolean wifiOnly;
    private static int uploadCount = 0;

    // The TransferUtility is the primary class for managing transfer to S3
//    private TransferUtility transferUtility;

    // A List of all transfers
    private HashMap<Integer, Long> observerMap;

    public S3Manager(final Context context, final boolean wifiOnly) {

        this.context = context;
        this.wifiOnly = wifiOnly;
    }

    /**
     * checks if sessions need to be uploaded
     * launches upload if necessary
     */
    public void checkUpload(){

        if(!Util.isOnline(context)){
            Log.w(TAG, "no internet connection, cannot upload anything");
            return;
        }

        if(wifiOnly && !isWifiConnection()){
            Log.w(TAG, "wifi only but no wifi connection, cannot upload ");
            return;
        }

        if(Looper.myLooper() == Looper.getMainLooper()){
            Log.w(TAG, "I'm in the main Thread!");
        }else{
            Log.w(TAG, "I'm in the background");
        }
        SMBDatabase database = new SMBDatabase(context);
        ArrayList<Session> sessionsToUpload = new ArrayList<>();

        try{
            if(database.open()) {
                sessionsToUpload = database.getSessionsToUpload();
            }
        }finally {
            database.close();
        }

        if(BuildConfig.DEBUG){
            Log.i(TAG, String.format(Locale.US, "Found %d sessions to upload", sessionsToUpload.size()));
        }

        if(sessionsToUpload.size() > 0){
            uploadSessions(sessionsToUpload);
        }else{
            Log.d(TAG, "Nothing to upload");
        }
    }

    /**
     * uploads the sessions @param sessionsToUpload
     * @param sessionsToUpload sessions to upload
     */
    private void uploadSessions(ArrayList<Session> sessionsToUpload) {

        //sd ready ?
        if(!Environment.getExternalStorageDirectory().canWrite()){
            Log.w(TAG, "cannot write to external memory");
            return;
        }

        if (!Util.createSMBDirectory()) {
            Log.w(TAG, "could not create app dir");
            return;
        }

        if(Looper.myLooper() == Looper.getMainLooper()){
            Log.w(TAG, "uploadsession - I'm in the main Thread!");
        }else{
            Log.w(TAG, "I'm in the background");
        }
        // create random object
        Random random = new Random();

        //create CSV
        CSVCreator csvCreator = new CSVCreator();
        RetrofitClient retrofitClient = RetrofitClient.getInstance(context);
        uploadCount = sessionsToUpload.size();
        for(Session session : sessionsToUpload){

            //String sessionFilePath = csvCreator.createCSV(session);

            String dataPointsFilePath = csvCreator.createCSV(session.getDataPoints(), Long.toString(session.getId()));

            File zipFile = createZip(String.format(Locale.US,Constants.ZIP_FILE_NAME, session.getId()), dataPointsFilePath);

            if(zipFile == null || !zipFile.exists()){
                Log.e(TAG, "error creating zip file");
                continue;
            }

            final String s3ObjectKey = getS3ObjectKey(zipFile);

            //upload zip using S3 transfer utility
            //final TransferObserver observer = getTransferUtility().upload(Constants.AWS_BUCKET_NAME, s3ObjectKey, zipFile);
            // observer.setTransferListener(this);
            //getObserverMap().put(observer.getId(), session.getId());

            //upload zip using Retrofit Client through API Gateway
            // get next next pseudorandom value
            int randomValue = random.nextInt();
            while(getObserverMap().containsKey(randomValue)){
                // TODO: remove this when this class will not implement TransferListener
                randomValue = random.nextInt();
            }

            final int dirtyHackWaitingForARefactor = randomValue;
            getObserverMap().put(dirtyHackWaitingForARefactor, session.getId());

            if(Looper.myLooper() == Looper.getMainLooper()){
                Log.w(TAG, "beforeuploadFile - I'm in the main Thread!");
            }else{
                Log.w(TAG, "beforeuploadFile - I'm in the background");
            }
            retrofitClient.uploadFile(s3ObjectKey, zipFile,
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           retrofit2.Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            Log.v("Upload", "success");
                            S3Manager.this.onStateChanged(dirtyHackWaitingForARefactor, TransferState.COMPLETED);
                        }else{
                            Log.v("Upload", "failed");
                            S3Manager.this.onStateChanged(dirtyHackWaitingForARefactor, TransferState.FAILED);
                        }
                        uploadCount--;
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Upload error:", t.getMessage());
                        S3Manager.this.onError(dirtyHackWaitingForARefactor, new Exception(t));
                        uploadCount--;
                    }
                });
        }
    }

    public static boolean isUploading() {
        return uploadCount > 0;
    }

    @NonNull
    private static String getS3ObjectKey(@NonNull File zipFile) {

        return zipFile.getName();

    }

    @Override
    public void onStateChanged(int id, TransferState newState) {

        if(BuildConfig.DEBUG) {
            Log.i(TAG, "onStateChanged: " + id + ", " + newState.name());
        }

        if(newState == TransferState.COMPLETED){

            if(getObserverMap().containsKey(id)) {

                final long sessionId = getObserverMap().get(id);

                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "cleaning up for session : " + sessionId);
                }

                //flag to db that the session was uploaded
                SMBDatabase database = new SMBDatabase(context);

                if(database.open()) {
                    boolean success = database.flagSessionAsUploaded(sessionId);
                    if(!success){
                        Log.w(TAG, "Session was not flagged as Uploaded");
                    }
                    database.close();
                }else{
                    Log.e(TAG, "could not open database");
                }

                //clean up created files
                cleanUpFilesForID(sessionId);
            }
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        if(BuildConfig.DEBUG) {
            Log.v(TAG, String.format("onProgressChanged: %d, total: %d, current: %d", id, bytesTotal, bytesCurrent));
        }
    }

    @Override
    public void onError(int id, Exception ex) {

        Log.e(TAG, "Error uploading " + id, ex);
    }

    /**
     * creates a zip of @param fileToZip
     * @param zipFile the name of the zipFile to create
     * @param fileToZip the file to zip
     * @return the path to the created file or null if the the operation failed
     */
    private File createZip(String zipFile, String fileToZip){

        File file = new File(Util.getSMBDirectory().getPath() + String.format(Locale.US, "/%s", zipFile));

        try  {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(file);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[1024];

            Log.i(TAG, "zipping : " + fileToZip);

            FileInputStream fi = new FileInputStream(fileToZip);
            origin = new BufferedInputStream(fi, 1024);
            ZipEntry entry = new ZipEntry(fileToZip.substring(fileToZip.lastIndexOf("/") + 1));
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();


            out.close();
        } catch(Exception e) {
           Log.e(TAG, "error zipping "+ fileToZip);
           return null;
        }
        return file;
    }

    /**
     * cleans up the file for the session upload @param sessionId
     * @param sessionId the id of the session
     */
    private void cleanUpFilesForID(long sessionId) {

        File sessionFile = new File(Util.getSMBDirectory().getPath() + String.format(Locale.US, Constants.SESSION_FILE_NAME, sessionId));
        File dataPointsFile = new File(Util.getSMBDirectory().getPath() + String.format(Locale.US, Constants.DATAPOINTS_FILE_NAME, sessionId));
        File zipFile = new File(Util.getSMBDirectory().getPath() + String.format(Locale.US, Constants.ZIP_FILE_NAME, sessionId));

        if(sessionFile.exists()){
            sessionFile.delete();
        }
        if(dataPointsFile.exists()){
            dataPointsFile.delete();
        }
        if(zipFile.exists()){
            zipFile.delete();
        }
    }


    /**
     * checks if the current Internet connection is a Wifi connection
     * @return true if Wifi, false otherwise
     */
    private boolean isWifiConnection(){

        ConnectivityManager cm =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null){
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnected()){
            android.net.NetworkInfo connWifiType = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            return connWifiType.isConnected();
        }
        return false;
    }

    /**
     * returns the transfer utility- creates it if necessary
     * @return a TransferUtility instance

    private TransferUtility getTransferUtility() {

        if(transferUtility == null){
            transferUtility = TransferUtility.builder().s3Client(getS3Client()).defaultBucket(Constants.AWS_BUCKET_NAME).context(context).build();
        }

        return transferUtility;
    }
     */
    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @return A default S3 client.

    private AmazonS3Client getS3Client() {

        AmazonS3Client sS3Client = new AmazonS3Client(getBasicCredentialsProvider());
        sS3Client.setRegion(Region.getRegion(Regions.fromName(Constants.AWS_REGION)));

        return sS3Client;
    }
     */
    /**
     * Gets an instance of BasicAWSCredentials which uses the credentials inside this app
     * This may not be very secure
     * @return the BasicAWSCredentials

    private BasicAWSCredentials getBasicCredentialsProvider(){

        return new BasicAWSCredentials(Constants.AWS_ACCESS_KEY, Constants.AWS_ACCESS_SECRET);
    }
     */
    /**
     * a map to map upload ids to session ids
     * @return the map
     */
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Long> getObserverMap() {

        if(observerMap == null){
            observerMap = new HashMap<>();
        }

        return observerMap;
    }
}
