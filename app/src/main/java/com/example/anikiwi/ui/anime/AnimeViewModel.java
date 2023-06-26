package com.example.anikiwi.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.RetrofitClient;
import com.example.anikiwi.repositories.AnimeRepository;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeViewModel extends ViewModel {
    private MutableLiveData<List<Anime>> animes;
    private AnimeRepository animeRepository;
    //private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private int pageNumber = 1;
    public void init() {
        if (animes != null) {
            return;
        }
        animeRepository = AnimeRepository.getInstance();
        animes = animeRepository.getAnimes();
    }
    public LiveData<List<Anime>> getAnimesObserver() {
        return animes;
    }

    public int getAnimeSize(){
        return Objects.requireNonNull(animes.getValue()).size();
    }
  // method to load new data
    public void loadMore() {
        pageNumber++;
        animeRepository.loadMore(pageNumber);
    }



}