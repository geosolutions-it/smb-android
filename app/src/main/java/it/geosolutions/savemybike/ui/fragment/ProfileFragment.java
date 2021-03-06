package it.geosolutions.savemybike.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.user.Profile;
import it.geosolutions.savemybike.model.user.User;

/**
 * @author Lorenzo Natali, GeoSolutions S.a.s.
 * Fragment for user profile
 */

public class ProfileFragment extends Fragment {
    public static final String TAG = "USER_PROFILE";
    private boolean modified;
    User user;

    @BindView(R.id.bio) TextView bio;
    @BindView(R.id.phone_number) TextView phoneNumber;
    // @BindView(R.id.btn_send) Button send;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.base_profile_fragment, container, false);
        ButterKnife.bind(this, view);
        user = Configuration.getUserProfile(getContext());
        if(user != null) {
            Profile p = user.getProfile();
            phoneNumber.setText(p.getPhoneNumber());
            bio.setText(p.getBio());
        }

        return view;
    }
    protected void setModified(boolean modified) {
        this.modified = modified;
        // send.setEnabled(true);
    }

    @OnTextChanged(R.id.bio)
    public void onBioChanged(CharSequence text) {
        setModified(true);
        user.getProfile().setBio(text.toString());
    }
    @OnTextChanged(R.id.phone_number)
    public void onPhoneNumberChanged(CharSequence text) {
        setModified(true);
        user.getProfile().setPhoneNumber(text.toString());
    }
}
