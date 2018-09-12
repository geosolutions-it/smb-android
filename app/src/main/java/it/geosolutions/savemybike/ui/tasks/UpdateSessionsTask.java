package it.geosolutions.savemybike.ui.tasks;
import android.content.Context;
import android.os.AsyncTask;

import it.geosolutions.savemybike.model.Session;
import java.lang.ref.WeakReference;
import java.util.List;

import it.geosolutions.savemybike.data.db.SMBDatabase;
import it.geosolutions.savemybike.model.Track;


/**
 * a task to load session from the local database
 */
public class UpdateSessionsTask extends AsyncTask<Void,Void, Boolean> {
    public interface SessionCallback {
        void showProgressView();
        void hideProgressView();
        void done(Boolean result);
    }
    private WeakReference<Context> contextRef;
    private SessionCallback callback;

    public UpdateSessionsTask(final Context context, final SessionCallback pCallback ){
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
    protected Boolean doInBackground(Void... voids) {

        final SMBDatabase database = new SMBDatabase(contextRef.get());
        try {

            database.open();
            List<Session> sessions = database.getSessionToUpdate();
            for (Session s : sessions) {
                database.deleteSession(s.getId());
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
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