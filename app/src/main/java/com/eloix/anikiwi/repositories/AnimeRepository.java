package com.eloix.anikiwi.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.eloix.anikiwi.networking.APIs;
import com.eloix.anikiwi.networking.Anime;
import com.eloix.anikiwi.networking.RecommendationResponse;
import com.eloix.anikiwi.networking.RetrofitClient;
import com.eloix.anikiwi.utilities.OnDataLoadedListener;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeRepository {

    //singleton
    private static AnimeRepository instance;
    private static MutableLiveData<List<Anime>> animes = new MutableLiveData<>();
    private static MutableLiveData<Anime> anime = new MutableLiveData<>();
    private static MutableLiveData<List<Anime>> recommendedAnimesLiveData = new MutableLiveData<>();

    public static AnimeRepository getInstance() {
        if (instance == null) {
            instance = new AnimeRepository();
        }
        //Log.d("AnimeRepository", "getInstance: " + instance.toString());
        return instance;
    }

    /**
     * Initializes the AnimeRepository.
     * Initializes the AnimeRepository by creating a new MutableLiveData for the animes list.
     */
    private AnimeRepository() {
        animes = new MutableLiveData<>();
    }

    /**
     * Gets the animes list.
     * @return the animes list
     */
    public MutableLiveData<List<Anime>> getAnimes() {
        //Log.d("AnimeRepository", "getAnimes: " + animes.toString());
        return animes;
    }

    // This method is not used, but was useful before clearing the animes and loading more
    public void makeAnimeApiCall(Map<String, Object> queryParams) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<List<Anime>> call = api.getAnimesQuery(queryParams);
        call.enqueue(new Callback<List<Anime>>() {
            @Override
            public void onResponse(Call<List<Anime>> call, Response<List<Anime>> response) {
                //Log.d("AnimeRepository", "onResponse: " + response.body().toString());
                animes.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Anime>> call, Throwable t) {
                //Log.d("AnimeRepository", "onFailure: " + t.getMessage());
                animes.postValue(null);
            }
        });
    }

    public void loadMore(Map<String, Object> queryParams, OnDataLoadedListener onDataLoadedListener) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<List<Anime>> call = api.getAnimesQuery(queryParams);
        call.enqueue(new Callback<List<Anime>>() {
            @Override
            public void onResponse(Call<List<Anime>> call, Response<List<Anime>> response) {
                if(response.body() == null){
                    Log.d("AnimeRepository", "LoadMore onResponse: response.body() == null");
                    onDataLoadedListener.onDataLoadFailed("Response body is null");
                    return;
                }

                List<Anime> oldAnimes = animes.getValue();
                if(oldAnimes == null){
                    Log.d("AnimeRepository", "LoadMore onResponse: oldAnimes == null so we create a new list");
                    oldAnimes = response.body();
                } else {
                    Log.d("AnimeRepository", "LoadMore onResponse: oldAnimes != null so we add the new data to the list");
                    oldAnimes.addAll(response.body());
                }
                animes.postValue(oldAnimes);

                onDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onFailure(Call<List<Anime>> call, Throwable t) {
                Log.d("AnimeRepository", "LoadMore onFailure: " + t.getMessage());
                onDataLoadedListener.onDataLoadFailed(t.getMessage());
            }
        });
    }


    public MutableLiveData<Anime> getAnime(String animeId) {
        getAnimeApiCall(animeId);
        return anime;
    }

    public void getAnimeApiCall(String animeId){
        APIs api = RetrofitClient.getInstance().getApis();
        Call<Anime> call = api.getAnime(animeId);
        call.enqueue(new Callback<Anime>() {
            @Override
            public void onResponse(Call<Anime> call, Response<Anime> response) {
                anime.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Anime> call, Throwable t) {
                anime.postValue(null);
            }
        });
    }

    public void clearAnimeList() {
        animes.postValue(null);
    }

    public MutableLiveData<List<Anime>> getRecommendations() {
        return recommendedAnimesLiveData;
    }

    public void makeRecommendedApiCall(String userId, OnDataLoadedListener onDataLoadedListener) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<RecommendationResponse> call = api.getRecommendedAnimes(userId);
        call.enqueue(new Callback<RecommendationResponse>() {
            @Override
            public void onResponse(Call<RecommendationResponse> call, Response<RecommendationResponse> response) {
                if(response.body() == null){
                    Log.d("AnimeRepository", "LoadMore onResponse: response.body() == null");
                    onDataLoadedListener.onDataLoadFailed("Response body is null");
                    return;
                }

                recommendedAnimesLiveData.postValue(response.body().getRecommendations());
                onDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onFailure(Call<RecommendationResponse> call, Throwable t) {
                Log.d("AnimeRepository", "LoadMore onFailure: " + t.getMessage());
                onDataLoadedListener.onDataLoadFailed(t.getMessage());
            }
        });
    }
}
