package it.geosolutions.savemybike.data.server;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import net.openid.appauth.AuthState;

import java.io.File;
import java.io.IOException;
import java.util.Locale;


import it.geosolutions.savemybike.AuthStateManager;
import it.geosolutions.savemybike.BuildConfig;
import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.PaginatedResult;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Robert Oehler on 07.12.17.
 * Edit by Lorenzo Pini
 * A Retrofit client which connect to a AWS endpoint
 */

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";

    private final static String AWS_ENDPOINT = "https://ex2rxvvhpc.execute-api.us-west-2.amazonaws.com/prod/";

    private Retrofit retrofit;
    private Retrofit portalRetrofit;

    private OkHttpClient portalClient;

    private Context context;

    private static RetrofitClient instance = null;

    public static RetrofitClient getInstance(final Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }

        return instance;
    }

    public RetrofitClient(final Context context) {
        this.context = context;
    }

    /**
     * get the config from the server
     * checks if a valid server token is available and uses it to fetch the current config
     * otherwise or if no token is available acquires a new token
     *
     * @param callback callback for the result
     */
    public void getRemoteConfig(@NonNull final GetConfigCallback callback, @NonNull final GetBikesCallback bikesCallback) {

/*
        if (System.currentTimeMillis() < accessToken.getExpiration().getTime()) {
/*/
            fetchConfig(callback);
            fetchBikes(bikesCallback);
/*

        } else {

            Log.d(TAG, "Token Expired");

            acquireToken(new Authenticate() {
                @Override
                public void success() {
                    fetchConfig(preferences.getString(Constants.PREF_CONFIG_IDTOKEN, null), callback);
                }

                @Override
                public void error(String message) {
                    Log.e(TAG, message);
                }
            });

        }
        */
    }


    /**
     * get the bikes list from the server

     * @param bikesCallback callback for the result
     */
    public void getBikes(@NonNull final GetBikesCallback bikesCallback) {

        fetchBikes(bikesCallback);

    }


    /**
     * fetches the current configuration from the AWS server using @param token for authentication
     * @param callback call for the result
     */
    private void fetchConfig(@NonNull final GetConfigCallback callback){

        //do the (retrofit) get call
        final Call<Configuration> call = getServices().getConfig();

        try {
            final Configuration configuration = call.execute().body();

            callback.gotConfig(configuration);

        } catch (IOException e) {
            Log.e(TAG, "error executing getConfig", e);
            callback.error("io-error executing getConfig");
        }
    }


    /**
     * fetches the current configuration from the AWS server using @param token for authentication
     * @param callback call for the result
     */
    private void fetchBikes(@NonNull final GetBikesCallback callback){

        //do the (retrofit) get call
        getPortalServices().getMyBikes().enqueue(new Callback<PaginatedResult<Bike>>() {
            @Override
            public void onResponse(Call<PaginatedResult<Bike>> call, retrofit2.Response<PaginatedResult<Bike>> response) {
                final PaginatedResult<Bike> bikesList = response.body();
                callback.gotBikes(bikesList);
            }

            @Override
            public void onFailure(Call<PaginatedResult<Bike>> call, Throwable t) {
                callback.error("io-error executing fetchBikes");
            }
        });


    }

    private Retrofit getRetrofit(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .client(getPortalClient())
                    .baseUrl(AWS_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }

    private Retrofit getPortalRetrofit(){
        if(portalRetrofit == null){
            portalRetrofit = new Retrofit.Builder()
                    .client(getPortalClient())
                    .baseUrl(Constants.PORTAL_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return portalRetrofit;
    }

    /**
     * Compose a Retrofit instance for Retrofit (setting up configuration).
     * TODO: avoid unnecessary interceptors and configuratons
     * @return the Retrofit instance to build the auth client
     */
    public Retrofit getAuthRetrofit() {
        return new Retrofit.Builder()
                .client(getPortalClient())
                .baseUrl(Constants.PORTAL_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private OkHttpClient getPortalClient(){

        if(portalClient == null) {


            portalClient  = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(context))
                    .addInterceptor(new LoggingInterceptor())
                    .build();
        }
        return portalClient;
    }

    /**
     * used to authenticate
     */
    private static class TokenInterceptor implements Interceptor {

        private Context context;

        TokenInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {

            // TODO: inject authentication token

            Request request = chain.request();
            AuthState state = AuthStateManager.getInstance(context).getCurrent();
            String token = state.getAccessToken();

            if (token != null){
                Request authenticatedRequest = request.newBuilder().header("Authorization", "Bearer " + token).build();
                return chain.proceed(authenticatedRequest);
            }
            return chain.proceed(request);

        }
    }


    /**
     * used to log communication
     */
    private static class LoggingInterceptor implements Interceptor {

        @Override public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            String requestLog = String.format(Locale.US, "Sending request %s on %s%n%s",request.url(), chain.connection(), request.headers());

            if(request.method().compareToIgnoreCase("post")==0){
                requestLog ="\n"+requestLog+"\n"+bodyToString(request);
            }
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "request" + "\n" + requestLog);
            }
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();

            String responseLog = String.format(Locale.US, "Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers());
            String bodyString = response.body().string();

            if(BuildConfig.DEBUG) {
                Log.d(TAG, "response" + "\n" + responseLog + "\n" + bodyString);
            }

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .build();
        }
    }

    private static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            Log.e(TAG,"error bodyToString", e);
            return "error";
        }
    }

    /**
     * This is only used to send the collected points to the S3 instance through the API Gateway
     * @return retrofit services to interact with the AWS endpoint
     */
    private SMBRemoteServices getServices(){

        return getRetrofit().create(SMBRemoteServices.class);

    }

    /**
     * Returns an auth client for retrofit.
     * @return
     */
    public AuthClient getAuthClient() {
        return getAuthRetrofit().create(AuthClient.class);
    }


    public SMBRemoteServices getPortalServices(){

        return getPortalRetrofit().create(SMBRemoteServices.class);

    }

    public interface GetConfigCallback
    {
        void gotConfig(Configuration configuration);
        void error(String message);
    }

    public interface GetBikesCallback
    {
        void gotBikes(PaginatedResult<Bike> bikesList);
        void error(String message);
    }
/*
    public interface Authenticate
    {
        void success();
        void error(String message);
    }
*/

    public void uploadFile(String s3ObjectKey, File file, Callback<ResponseBody> callback) {

        // TODO make a singleton for the services
        // create upload service client
        SMBRemoteServices service = getServices();

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("application/zip"),
                        file
                );

        // finally, execute the request
        Call<ResponseBody> call = service.upload(s3ObjectKey, requestFile);
        call.enqueue(callback);
    }

}
