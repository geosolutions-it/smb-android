package it.geosolutions.savemybike.ui.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import it.geosolutions.savemybike.data.server.RetrofitClient;

/**
     * loads the config and the bikes from remote
     */
    public class GetRemoteConfigTask extends AsyncTask<Void, Void, Void> {

        private final RetrofitClient.GetBikesCallback bikesCallback;
        private WeakReference<Context> contextRef;
        private RetrofitClient.GetConfigCallback callback;

        public GetRemoteConfigTask(final Context context, @NonNull RetrofitClient.GetConfigCallback callback, @NonNull RetrofitClient.GetBikesCallback bikesCallback) {
            this.contextRef = new WeakReference<>(context);
            this.callback = callback;
            this.bikesCallback = bikesCallback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            RetrofitClient retrofitClient = new RetrofitClient(contextRef.get());
            // retrofitClient.getRemoteConfig(callback, bikesCallback);
            retrofitClient.getBikes(bikesCallback);
            return null;
        }
    }