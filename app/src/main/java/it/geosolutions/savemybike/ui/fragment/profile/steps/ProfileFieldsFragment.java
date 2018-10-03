package it.geosolutions.savemybike.ui.fragment.profile.steps;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.ui.utils.ProfileUtils;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Fragment to inser profile non-mandatory fields
 */
public class ProfileFieldsFragment extends WizardStepFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.step_profile_fields_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @OnTextChanged(R.id.bio)
    public void onBioChanged(CharSequence text) {
        onChange(ProfileUtils.BIO, text.toString());
    }
    @OnTextChanged(R.id.phone_number)
    public void onPhoneNumberChanged(CharSequence text) {
        onChange(ProfileUtils.PHONE_NUMBER, text.toString());
    }
}
