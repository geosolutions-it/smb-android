package it.geosolutions.savemybike;

import android.support.v7.app.AppCompatDelegate;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
