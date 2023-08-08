package com.example.anikiwi.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.RetrofitClient;
import com.example.anikiwi.networking.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeRepository {

    //singleton
    private static AnimeRepository instance;
    private static MutableLiveData<List<Anime>> animes = new MutableLiveData<>();

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
        makeApiCall();
        //Log.d("AnimeRepository", "getAnimes: " + animes.toString());
        return animes;
    }

    public void makeApiCall() {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<List<Anime>> call = api.getAnimes();
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

    public void loadMore(int pageNumber) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<List<Anime>> call = api.getAnimes2(pageNumber);
        call.enqueue(new Callback<List<Anime>>() {
            @Override
            public void onResponse(Call<List<Anime>> call, Response<List<Anime>> response) {
                //add the new data to the list
                List<Anime> oldAnimes = animes.getValue();
                assert oldAnimes != null;
                assert response.body() != null;
                oldAnimes.addAll(response.body());
                animes.postValue(oldAnimes);
            }

            @Override
            public void onFailure(Call<List<Anime>> call, Throwable t) {
                animes.postValue(null);
            }
        });
    }

    public static void createUserInDatabase(String displayName, String email) {
        //TODO: recuerda tener en cuenta que la api esta siempre caida al inicio
        APIs api = RetrofitClient.getInstance().getApis();
        User user = new User(displayName, email);
        Call<User> call = api.createUserInDatabase(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Log.d("AnimeRepository", "onResponse: " + response.body());
                }
                else if (response.code() == 409) {
                    Log.d("AnimeRepository", "onResponse: " + response.body());
                }
                else {
                    Log.d("AnimeRepository", "onResponse: " + response.body());
                }

                //User user = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("AnimeRepository", "onFailure: " + t.getMessage());
            }
        });
    }

    public void wakeUp() {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<String> call = api.wakeUp();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("AnimeRepository", "onResponse: " + response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("AnimeRepository", "onFailure: " + t.getMessage());
            }
        });
    }
}
