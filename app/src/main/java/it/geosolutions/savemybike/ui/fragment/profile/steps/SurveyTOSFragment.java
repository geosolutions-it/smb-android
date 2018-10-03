package it.geosolutions.savemybike.ui.fragment.profile.steps;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.ui.utils.ProfileUtils;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Fragment to insert Survey entries and TOS acceptance
 */
public class SurveyTOSFragment extends WizardStepFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.step_survey_tos_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @OnCheckedChanged(R.id.accepted_terms_of_service)
    public void onTermsOfServiceChanged(CompoundButton button, boolean checked) {
        onChange(ProfileUtils.ACCEPTED_TOS, checked ? "true" : "false");
    }

}
