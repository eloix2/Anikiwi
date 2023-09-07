package com.example.anikiwi.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.RetrofitClient;
import com.example.anikiwi.networking.RetryQueue;
import com.example.anikiwi.networking.User;

import java.util.AbstractCollection;
import java.util.HashMap;
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

    public static AnimeRepository getInstance() {
        if (instance == null) {
            instance = new AnimeRepository();
        }
        //Log.d("AnimeRepository", "getInstance: " + instance.toString());
        return instance;
    }

    private AnimeRepository() {
        animes = new MutableLiveData<>();
    }

    public MutableLiveData<List<Anime>> getAnimes() {
        //TODO: este map meterlo en una clase o algo aparte para reutilizarlo
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("year", 2023);
        queryParams.put("page", 1);
        queryParams.put("limit", 50);
        makeAnimeApiCall(queryParams);
        //Log.d("AnimeRepository", "getAnimes: " + animes.toString());
        return animes;
    }

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

    public void loadMore(Map<String, Object> queryParams) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<List<Anime>> call = api.getAnimesQuery(queryParams);
        call.enqueue(new Callback<List<Anime>>() {
            @Override
            public void onResponse(Call<List<Anime>> call, Response<List<Anime>> response) {
                //add the new data to the list
                List<Anime> oldAnimes = animes.getValue();
                //TODO: he eliminado el assert para que se añadan en caso de que sea null, a la espera a ver si da problemas y a ver si puedo eliminar el otro assert
                //assert oldAnimes != null;
                //este assert es peligroso
                assert response.body() != null;
                if(oldAnimes == null){
                    oldAnimes = response.body();
                } else {
                    oldAnimes.addAll(response.body());
                }

                //oldAnimes.addAll(response.body());
                animes.postValue(oldAnimes);

            }

            @Override
            public void onFailure(Call<List<Anime>> call, Throwable t) {
                animes.postValue(null);
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
}
