package com.example.anikiwi.networking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIs {
    String BASE_URL = "https://anikiwi-api.onrender.com/api/";

    @GET("animes/search?year=2023&page=1&limit=50")
    Call<List<Anime>> getAnimes();
}
