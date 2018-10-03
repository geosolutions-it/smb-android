package it.geosolutions.savemybike.ui.fragment.profile.steps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.ui.utils.ProfileUtils;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Fragment to inser mandatory fields
 */
public class MandatoryFieldsFragment extends WizardStepFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.step_mandatory_fields_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @OnClick({R.id.gender_male, R.id.gender_female})
    public void onRadioButtonClicked(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();

        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.gender_male:
                if (checked) {
                    onChange(ProfileUtils.GENDER, getResources().getStringArray(R.array.genders)[0]);
                }
                break;
            case R.id.gender_female:
                if (checked) {
                    onChange(ProfileUtils.GENDER, getResources().getStringArray(R.array.genders)[1]);
                }
                break;
        }
    }
    @OnItemSelected(R.id.ages_spinner)
    public void onAgeSelected(int position) {
        String value = getResources().getStringArray(R.array.ages_values)[position];
        onChange("age", value);
    }
    @OnItemSelected(R.id.occupation_spinner)
    public void onOccupationSelected(int position) {
        String value = getResources().getStringArray(R.array.occupation_values)[position];
        onChange("occupation", value);
    }



}
