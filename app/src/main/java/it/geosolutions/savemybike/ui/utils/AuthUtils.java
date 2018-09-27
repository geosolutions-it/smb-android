package it.geosolutions.savemybike.ui.utils;
import android.net.Uri;

import net.openid.appauth.AuthorizationServiceDiscovery;

import org.json.JSONException;

import java.net.URI;

public class AuthUtils {

    /**
     * The OpenID Connect EndSession endpoint URI.
     */
    public static Uri getEndSessionEndpoint(AuthorizationServiceDiscovery asd) {
        try {
            return Uri.parse(((String) asd.docJson.get("end_session_endpoint")));
        } catch (JSONException e) {
            return null;
        }
    }

}
