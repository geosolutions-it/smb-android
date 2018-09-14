package it.geosolutions.savemybike.ui.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import it.geosolutions.savemybike.data.db.SMBDatabase;
import it.geosolutions.savemybike.data.server.S3Manager;
import it.geosolutions.savemybike.model.Session;


/**
 * a task to load session from the local database
 */
public class UploadSessionTask extends AsyncTask<Void,Void,Boolean> {
    public interface SessionCallback {
        void showProgressView();
        void hideProgressView();
        void done(boolean success);
    }
    private boolean uploadWithWifiOnly;
    private Context contextRef;
    private SessionCallback callback;

    public UploadSessionTask(final Context context, final SessionCallback pCallback, boolean uploadWithWifiOnly ){
        this.uploadWithWifiOnly = uploadWithWifiOnly;
        this.contextRef = context;
        this.callback = pCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(callback != null) {
            callback.showProgressView();
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        final SMBDatabase database = new SMBDatabase(contextRef);
        try {

            final S3Manager s3Manager = new S3Manager(contextRef, uploadWithWifiOnly);
            s3Manager.checkUpload();
        } catch (Exception e) {
            return false;
        } finally {
            database.close();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        if(callback != null) {
            callback.hideProgressView();
            callback.done(success);
        }
    }
}