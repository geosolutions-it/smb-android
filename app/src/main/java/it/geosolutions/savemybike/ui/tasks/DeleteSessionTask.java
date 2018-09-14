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
public class DeleteSessionTask extends AsyncTask<Void,Void,Boolean> {
    public interface DeleteSessionCallback
    {
        void showProgressView();
        void hideProgressView();
        void done(Boolean sessions);
    }
    private WeakReference<Context> contextRef;
    private DeleteSessionCallback callback;
    private Session session;

    public DeleteSessionTask(final Context context, final DeleteSessionCallback pCallback, Session session){

        this.contextRef = new WeakReference<>(context);
        this.callback = pCallback;
        this.session = session;
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


        final SMBDatabase database = new SMBDatabase(contextRef.get());
        try{

            database.open();
            database.deleteSession(session.getId());
            // TODO: check if the session has a track uploaded and then delete them
            // TODO: so only the sessions that has not been ingested yet
            return true;
        } catch (Exception e) {
            return false;
        }
        finally {
            database.close();
        }


    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if(callback != null) {
            callback.hideProgressView();
            callback.done(result);
        }
    }
}


