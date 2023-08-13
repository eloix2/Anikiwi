package com.example.anikiwi.ui.animedata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.Anime;

public class AnimeDataViewModel extends ViewModel {
    private MutableLiveData<Anime> animeDataLiveData;

    // Inject API service here

    public void init(String animeId) {
        if (animeDataLiveData != null) {
            return; // ViewModel already initialized
        }
        animeDataLiveData = new MutableLiveData<>();

        // Fetch anime data from API using animeId and update LiveData
        // Replace this with API call to fetch anime data by ID
        // For example:
        // ApiService.getInstance().getAnimeById(animeId, new Callback<AnimeData>() {
        //     @Override
        //     public void onResponse(Call<AnimeData> call, Response<AnimeData> response) {
        //         animeDataLiveData.setValue(response.body());
        //     }
        //
        //     @Override
        //     public void onFailure(Call<AnimeData> call, Throwable t) {
        //         // Handle error
        //     }
        // });
    }

    public LiveData<Anime> getAnimeData() {
        return animeDataLiveData;
    }


}
