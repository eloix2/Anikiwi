package com.example.anikiwi.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.Rating;
import com.example.anikiwi.networking.RetrofitClient;
import com.example.anikiwi.networking.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingRepository {

    //singleton
    private static RatingRepository instance;

    // Puede servir para guardar los animes que tienen rating
    //private static MutableLiveData<List<Anime>> animes = new MutableLiveData<>();

    //private static MutableLiveData<Anime> anime = new MutableLiveData<>();

    //private static MutableLiveData<List<Rating>> ratings = new MutableLiveData<>();
    private static MutableLiveData<Rating> rating = new MutableLiveData<>();


    public static RatingRepository getInstance() {
        if (instance == null) {
            instance = new RatingRepository();
        }
        //Log.d("AnimeRepository", "getInstance: " + instance.toString());
        return instance;
    }

    private RatingRepository() {
        //ratings = new MutableLiveData<>();
    }


    public void rateAnime(Rating ratingAnime) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<Rating> call = api.rateAnime(ratingAnime);
        call.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                //anime.postValue(response.body());
                //update ratings list? idk
                Log.d("AnimeRepository", "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                //anime.postValue(null);
                Log.d("AnimeRepository", "onFailure: " + t.getMessage());
            }
        });
    }

    public MutableLiveData<Rating> getRating(String activeUserId, String animeId) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<Rating> call = api.getRating(activeUserId, animeId);
        call.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                rating.postValue(response.body());
                Log.d("AnimeRepository", "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                rating.postValue(null);
                Log.d("AnimeRepository", "onFailure: " + t.getMessage());
            }
        });
        return rating;
    }
}
