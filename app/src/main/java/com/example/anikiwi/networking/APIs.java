package com.example.anikiwi.networking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIs {
    String BASE_URL = "https://anikiwi-api.onrender.com/api/";

    @GET("animes/search?year=2023&page=1&limit=50")
    Call<List<Anime>> getAnimes();

    @GET("animes/search?year=2023&limit=50")
    Call<List<Anime>> getAnimes2(@Query("page") int pageNumber);

    @GET("state")
    Call<String> wakeUp();

    @POST("users")
    Call<User> createUserInDatabase(@Body User user);

    @GET("animes/search/{id}")
    Call<Anime> getAnime(@Path("id") String id);
}
