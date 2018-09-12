package it.geosolutions.savemybike.ui.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import it.geosolutions.savemybike.data.db.SMBDatabase;
import it.geosolutions.savemybike.model.Session;

/**
 * a task to load all sessions from the local database
 */
public class InvalidateSessionsTask extends AsyncTask<Void,Void,ArrayList<Session>> {
    public interface InvalidateSessionsCallback
    {
        void showProgressView();
        void hideProgressView();
        void done(ArrayList<Session> sessions);
    }
    private WeakReference<Context> contextRef;
    private InvalidateSessionsCallback callback;

    public InvalidateSessionsTask(final Context context, final InvalidateSessionsCallback pCallback){

        this.contextRef = new WeakReference<>(context);
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
    protected ArrayList<Session> doInBackground(Void... voids) {

        ArrayList<Session> sessions = null;
        final SMBDatabase database = new SMBDatabase(contextRef.get());
        try{

            database.open();
            sessions = database.getSessionsToUpload(); // TODO: return all sessions and show uploaded in a different way


        }finally {
            database.close();
        }
        return sessions;
    }

    @Override
    protected void onPostExecute(ArrayList<Session> sessions) {
        super.onPostExecute(sessions);

        if(callback != null) {
            callback.hideProgressView();
            callback.done(sessions);
        }
    }
}


