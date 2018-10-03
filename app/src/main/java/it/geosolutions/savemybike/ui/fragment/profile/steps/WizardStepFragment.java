package it.geosolutions.savemybike.ui.fragment.profile.steps;

import android.support.v4.app.Fragment;

import it.geosolutions.savemybike.ui.callback.OnChangeCallback;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Base class with callbacks bindings for wizard steps
 * TODO: externalize in a wizard fragment
 */
public class WizardStepFragment extends Fragment {
    OnChangeCallback callback;
    public void setOnChangeCallbacks(OnChangeCallback callback) {
        this.callback = callback;
    }
    public void onChange(String name, String value) {
        if(callback != null) {
            callback.onChange(name, value);
        }
    }
}
