package it.geosolutions.savemybike.data.server;

import java.io.File;

import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.model.Configuration;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Lorenzo Pini on 23/03/2018.
 */

public interface SMBRemoteServices {
    @GET("config")
    Call<Configuration> getConfig();

    @PUT("upload/"+ Constants.AWS_BUCKET_NAME+"/{s3ObjectKey}")
    Call<ResponseBody> upload(
            @Path("s3ObjectKey") String s3ObjectKey,
            @Body RequestBody file
    );
}
