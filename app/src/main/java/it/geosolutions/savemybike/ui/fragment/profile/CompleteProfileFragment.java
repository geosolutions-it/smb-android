package it.geosolutions.savemybike.ui.fragment.profile;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.server.RetrofitClient;
import it.geosolutions.savemybike.data.server.SMBRemoteServices;
import it.geosolutions.savemybike.model.user.User;
import it.geosolutions.savemybike.model.user.Profile;
import it.geosolutions.savemybike.ui.activity.CompleteProfile;
import it.geosolutions.savemybike.ui.adapters.ViewPagerAdapter;
import it.geosolutions.savemybike.ui.fragment.WizardFragment;
import it.geosolutions.savemybike.ui.fragment.profile.steps.MandatoryFieldsFragment;
import it.geosolutions.savemybike.ui.fragment.profile.steps.ProfileFieldsFragment;
import it.geosolutions.savemybike.ui.fragment.profile.steps.SurveyTOSFragment;
import it.geosolutions.savemybike.ui.fragment.profile.steps.WizardStepFragment;
import it.geosolutions.savemybike.ui.utils.ProfileUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompleteProfileFragment extends WizardFragment {

    private User user = new User();

    @Override
    public void setupSteps(ViewPagerAdapter adapter) {
        user.setProfile(new Profile());
        // Add Fragments to adapter one by one
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MandatoryFieldsFragment());
        fragmentList.add(new ProfileFieldsFragment());
        fragmentList.add(new SurveyTOSFragment());
        int i = 0;
        // setup listener for user profile changes
        for(Fragment f : fragmentList) {
            if(f instanceof WizardStepFragment) {
                ((WizardStepFragment) f).setOnChangeCallbacks(
                        (String name, String value) -> {
                            switch (name) {
                                case ProfileUtils.BIO: {
                                    user.getProfile().setBio(value);
                                    break;
                                }
                                case ProfileUtils.GENDER: {
                                    user.getProfile().setGender(value);
                                    break;
                                }
                                case ProfileUtils.OCCUPATION: {
                                    user.getProfile().setOccupation(value);
                                    break;
                                }
                                case ProfileUtils.AGE:{
                                    user.getProfile().setAge(value);
                                    break;
                                }
                                case ProfileUtils.ACCEPTED_TOS: {
                                    user.setAcceptedTermsOfService(value == "true" ? true : false);
                                    break;
                                }
                                default:
                                    break;
                            }
                            updateBottomBar();
                        }


                );
            }
            adapter.addFragment(f,  "" + i);
            i++;
        }
    }

    @Override
    public boolean isValid(int position) {
        switch (position) {
            case 0:
                return user.getProfile() != null
                    && user.getProfile().getGender() != null
                    && user.getProfile().getAge() != null
                    && user.getProfile().getOccupation() != null;
            case 1:
                return true;
            case 2:
                return user.getAcceptedTermsOfService() != null
                        ? user.getAcceptedTermsOfService()
                        : false;
            default:
                return true;
        }
    }

    @Override
    public void onComplete() {
        ((CompleteProfile) getActivity()).onComplete(user);
    }
}
