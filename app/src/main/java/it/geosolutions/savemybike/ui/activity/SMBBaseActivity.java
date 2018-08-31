package it.geosolutions.savemybike.ui.activity;

import android.support.v7.app.AppCompatActivity;

import it.geosolutions.savemybike.model.Configuration;

public class SMBBaseActivity extends AppCompatActivity {
    protected Configuration configuration;

    /**
     * gets the configuration - if it is null it is loaded
     *
     * @return the configuration
     */
    public Configuration getConfiguration() {
        if (configuration == null) {
            configuration = Configuration.loadConfiguration(getBaseContext());
        }
        return configuration;
    }
}
