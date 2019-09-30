package it.geosolutions.savemybike.data.server;

import it.geosolutions.savemybike.data.Constants;
import it.geosolutions.savemybike.model.Badge;
import it.geosolutions.savemybike.model.Bike;
import it.geosolutions.savemybike.model.Configuration;
import it.geosolutions.savemybike.model.CurrentStatus;
import it.geosolutions.savemybike.model.PaginatedResult;
import it.geosolutions.savemybike.model.Track;
import it.geosolutions.savemybike.model.TrackItem;
import it.geosolutions.savemybike.model.competition.Competition;
import it.geosolutions.savemybike.model.competition.CompetitionParticipantRequest;
import it.geosolutions.savemybike.model.competition.CompetitionParticipationInfo;
import it.geosolutions.savemybike.model.user.Device;
import it.geosolutions.savemybike.model.user.User;
import it.geosolutions.savemybike.model.user.UserInfo;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Lorenzo Pini on 23/03/2018.
 */

public interface SMBRemoteServices
{
    @GET("config")
    Call<Configuration> getConfig();

    @PUT(Constants.UPLOAD_RESOURCE + "{s3ObjectKey}")
    Call<ResponseBody> upload(
            @Path("s3ObjectKey") String s3ObjectKey,
            @Body RequestBody file
    );

    @GET("api/my-bikes")
    Call<PaginatedResult<Bike>> getMyBikes();

    @GET("api/my-bikes/{id}")
    Call<Bike> getMyBike(@Path("id") String id);

    @GET("api/my-bike-observations/")
    Call<ResponseBody> getBikeObservations(@Query("bike") String id);

    @POST("api/my-bike-statuses/")
    Call<Object> sendNewBikeStatus(
            @Body CurrentStatus newStatus
    );

    @GET("api/my-tracks/?format=json")
    Call<PaginatedResult<TrackItem>> getTracks();

    @GET("api/my-tracks/{id}?format=json")
    Call <Track> getTrack(@Path("id") long id);

    @GET("api/my-user")
    Call<UserInfo> getUser();

    @GET("api/my-badges")
    Call<PaginatedResult<Badge>> getBadges();

    @PATCH("api/my-user")
    Call<ResponseBody> updateUser(@Body User user);

	// Competitions

	@GET("api/my-competitions-available")
	Call<PaginatedResult<Competition>> getMyCompetitionsAvailable();

	@GET("api/my-competitions-current")
	Call<PaginatedResult<CompetitionParticipationInfo>> getMyCompetitionsCurrent();

	@POST("api/my-competitions-current/")
	Call<ResponseBody> requestCompetitionParticipation(@Body CompetitionParticipantRequest cmp);

	@DELETE("api/my-competitions-current/{id}/")
	Call<ResponseBody> cancelCompetitionParticipation(@Path("id") long id);

	@GET("api/my-competitions-won")
	Call<PaginatedResult<CompetitionParticipationInfo>> getMyCompetitionsWon();

	// Devices

    @PUT("api/my-devices/{token}/")
    Call<ResponseBody> updateDevice(@Path("token") String token, @Body Device device);

    @POST("api/my-devices/")
    Call<ResponseBody> updateDevice(@Body Device device);

    @DELETE("api/my-devices/{token}")
    Call<ResponseBody> deleteDevice(@Path("token") String token);

}
