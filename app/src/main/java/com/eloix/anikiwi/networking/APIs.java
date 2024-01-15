package com.eloix.anikiwi.networking;

import com.eloix.anikiwi.model.Anime;
import com.eloix.anikiwi.model.Rating;
import com.eloix.anikiwi.model.RatingWithAnime;
import com.eloix.anikiwi.model.Recommendation;
import com.eloix.anikiwi.model.StatisticsResponse;
import com.eloix.anikiwi.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface APIs {
    String BASE_URL = "https://anikiwi-api-13fbded2aee1.herokuapp.com/api/";
    //String BASE_URL = "https://anikiwi-api.onrender.com/api/";

    @GET("animes/search")
    Call<List<Anime>> getAnimesQuery(@QueryMap Map<String, Object> queryParams);

    @GET("state")
    Call<String> wakeUp();

    @POST("users")
    Call<User> createUserInDatabase(@Body User user);

    @GET("animes/search/{id}")
    Call<Anime> getAnime(@Path("id") String id);

    @POST("rate")
    Call<Rating> rateAnime(@Body Rating rating);

    @GET("ratings/{userId}/{animeId}")
    Call<Rating> getRatingByUserIdAndAnimeId(@Path("userId")String userId, @Path("animeId") String animeId);

    @GET("ratings/search")
    Call<List<RatingWithAnime>> getRatingsQuery(@QueryMap Map<String, Object> queryParams);

    @GET("users/{userId}/recommendations")
    Call<Recommendation> getRecommendedAnimes(@Path("userId") String userId);

    @PUT("rating/{id}/addEpisode")
    Call<RatingWithAnime> addEpisodeToRating(@Path("id") String ratingId);

    @GET("users/{id}/stats")
    Call<StatisticsResponse> getUserStatistics(@Path("id") String userId);

}
