package it.geosolutions.savemybike.data;

import android.os.Bundle;

import retrofit2.Response;

/**
 * Utility Class to use store common methods and constants
 * of FireBase Analytics events and user properties.
 */
public class Analytics {
    public static final class UserProperties {
        public static final String EMAIL = "email";
        public static final String LAST_USER_UPDATE = "last_user_update";
        public static final String STATE = "state";
    }
    public static final class Events {
        public static final String UPDATE_ERROR = "UPDATE_ERROR";
    }
    public static final class Params {
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String ERROR_BODY = "error";
    }

    /**
     * Create an event bundle parsing a Retrofit Response object
     * @param response the server response
     * @return
     */
    public static Bundle createEventBundle(Response response) {
        Bundle b = new Bundle();
        b.putInt(Params.CODE, response.code());
        b.putString(Params.MESSAGE, response.message());
        b.putString(Params.ERROR_BODY,response.errorBody().toString());
        return b;
    }
    /**
     * Create an event bundle parsing the list of arguments, in couples.
     * @param elements the elements list
     * @return
     */
    public static Bundle createEventBundle(String... elements) {
        Bundle b = new Bundle();
        if(elements == null || elements.length == 0) {
            return b;
        }
        if(elements.length == 1) {
            b.putString("data", elements[0]);
            return b;
        }
        for(int i = 0; i <elements.length; i = i+2) {
            if( i+1 < elements.length && elements[i] != null && elements[i+1] != null) {
                b.putString(elements[i], elements[i+1]);
            }
        }
        return b;
    }
}
