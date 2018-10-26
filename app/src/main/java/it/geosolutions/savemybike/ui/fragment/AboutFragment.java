package it.geosolutions.savemybike.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import it.geosolutions.savemybike.R;
import it.geosolutions.savemybike.data.Constants;

public class AboutFragment extends Fragment{


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.smb_site_link)
    public void openSMBSite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.smb_site_url)));
        startActivity(browserIntent);
    }
    @OnClick(R.id.goodgo_site_link)
    public void openGoodGoSite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PORTAL_ENDPOINT));
        startActivity(browserIntent);
    }
    @OnClick(R.id.geosolutions_link)
    public void openGeoSolutionsSite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.geosolutions_site_url)));
        startActivity(browserIntent);
    }
}
