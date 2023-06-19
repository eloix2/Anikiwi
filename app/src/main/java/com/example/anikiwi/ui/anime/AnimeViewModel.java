package com.example.anikiwi.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeViewModel extends ViewModel {
    private MutableLiveData<List<Anime>> animes;
    public AnimeViewModel() {
        animes = new MutableLiveData<>();
    }

    public MutableLiveData<List<Anime>> getAnimesObserver() {
        return animes;
    }

    public void makeApiCall() {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<List<Anime>> call = api.getAnimes();
        call.enqueue(new Callback<List<Anime>>() {
            @Override
            public void onResponse(Call<List<Anime>> call, Response<List<Anime>> response) {
                animes.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Anime>> call, Throwable t) {
                animes.postValue(null);
            }
        });
    }

}