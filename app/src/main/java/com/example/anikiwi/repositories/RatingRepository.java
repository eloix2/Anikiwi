package com.example.anikiwi.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.Rating;
import com.example.anikiwi.networking.RatingWithAnime;
import com.example.anikiwi.networking.RetrofitClient;

import java.util.List;
import java.util.Map;

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
    private static MutableLiveData<List<RatingWithAnime>> ratedAnimesLiveData = new MutableLiveData<>();


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

    public MutableLiveData<List<RatingWithAnime>> getRatingWithAnimeList() {
        return ratedAnimesLiveData;
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

    public MutableLiveData<Rating> getRatingByUserIdAndAnimeId(String activeUserId, String animeId) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<Rating> call = api.getRatingByUserIdAndAnimeId(activeUserId, animeId);
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


    public MutableLiveData<List<RatingWithAnime>> getAnimesRatedByUser(String userId) {

        APIs api = RetrofitClient.getInstance().getApis();
        Call<List<RatingWithAnime>> call = api.getAnimesRatedByUser(userId);
        call.enqueue(new Callback<List<RatingWithAnime>>() {
            @Override
            public void onResponse(Call<List<RatingWithAnime>> call, Response<List<RatingWithAnime>> response) {
                if (response.isSuccessful()) {
                    List<RatingWithAnime> ratings = response.body();
                    ratedAnimesLiveData.postValue(ratings);
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<RatingWithAnime>> call, Throwable t) {
                // Handle network error
            }
        });

        return ratedAnimesLiveData;
    }

    public void loadMore(Map<String, Object> queryParams) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<List<RatingWithAnime>> call = api.getRatingsQuery(queryParams);
        call.enqueue(new Callback<List<RatingWithAnime>>() {
            @Override
            public void onResponse(Call<List<RatingWithAnime>> call, Response<List<RatingWithAnime>> response) {
                if(response.body() == null){
                    Log.d("RatingRepository", "LoadMore onResponse: response.body() == null");
                    ratedAnimesLiveData.postValue(null);
                    return;
                }

                List<RatingWithAnime> oldRatings = ratedAnimesLiveData.getValue();
                if(oldRatings == null){
                    Log.d("AnimeRepository", "LoadMore onResponse: oldAnimes == null so we create a new list");
                    oldRatings = response.body();
                } else {
                    Log.d("AnimeRepository", "LoadMore onResponse: oldAnimes != null so we add the new data to the list");
                    oldRatings.addAll(response.body());
                }
                ratedAnimesLiveData.postValue(oldRatings);
            }

            @Override
            public void onFailure(Call<List<RatingWithAnime>> call, Throwable t) {
                Log.d("AnimeRepository", "LoadMore onFailure: " + t.getMessage());
                ratedAnimesLiveData.postValue(null);
            }
        });
    }

    public void clearRatingWithAnimeList() {
        ratedAnimesLiveData.postValue(null);
    }
}
