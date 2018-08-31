package it.geosolutions.savemybike.ui.tasks;
import android.content.Context;
import android.os.AsyncTask;

import it.geosolutions.savemybike.model.Session;
import java.lang.ref.WeakReference;

import it.geosolutions.savemybike.data.db.SMBDatabase;


/**
 * a task to load session from the local database
 */
public class GetSessionTask extends AsyncTask<Void,Void,Session> {
    public interface SessionCallback {
        void showProgressView();
        void hideProgressView();
        void done(Session session);
    }
    private Long id;
    private WeakReference<Context> contextRef;
    private SessionCallback callback;

    public GetSessionTask(final Context context, final SessionCallback pCallback, Long id ){
        this.id = id;
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
    protected Session doInBackground(Void... voids) {

        Session session;
        final SMBDatabase database = new SMBDatabase(contextRef.get());
        try{

            database.open();
            session = database.getSession(id);

        }finally {
            database.close();
        }

        return session;
    }

    @Override
    protected void onPostExecute(Session session) {
        super.onPostExecute(session);

        if(callback != null) {
            callback.hideProgressView();
            callback.done(session);
        }
    }
}