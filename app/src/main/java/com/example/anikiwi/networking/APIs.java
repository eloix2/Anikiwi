package com.example.anikiwi.networking;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface APIs {
    String BASE_URL = "https://anikiwi-api-13fbded2aee1.herokuapp.com/api/";
    //String BASE_URL = "https://anikiwi-api.onrender.com/api/";

    @GET("animes/search?year=2023&page=1&limit=50")
    Call<List<Anime>> getAnimes();

    @GET("animes/search?limit=50&year=2023{query}")
    Call<List<Anime>> getAnimes2(@Query("page") int pageNumber, @Path("query") String query);
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

    @GET("ratings/user/{userId}/animes")
    Call<List<RatingWithAnime>> getAnimesRatedByUser(@Path("userId") String userId);

    @GET("ratings/search")
    Call<List<RatingWithAnime>> getRatingsQuery(@QueryMap Map<String, Object> queryParams);

    @GET("users/{userId}/recommendations")
    Call<RecommendationResponse> getRecommendedAnimes(@Path("userId") String userId);
    
    //@GET("animes/search/limit=50")
    //Call<List<Anime>> filterAnimes(@Query("page") String text);
}
