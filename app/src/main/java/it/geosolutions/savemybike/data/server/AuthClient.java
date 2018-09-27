package it.geosolutions.savemybike.data.server;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AuthClient {
    @GET
    Call<ResponseBody> logout(@Url String end_session_endpoint, @Query("redirect_uri") String redirect_uri, @Query("id_token_hint") String id_token_hint);
}
