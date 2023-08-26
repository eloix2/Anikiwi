package com.example.anikiwi.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.repositories.AnimeRepository;

import java.util.List;
import java.util.Objects;

public class AnimeViewModel extends ViewModel {
    private MutableLiveData<List<Anime>> animes;
    private AnimeRepository animeRepository;
    private boolean isLoading = false;
    //private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private int pageNumber = 1;
    public void init() {
        if(animes != null){
            //Log.d("AnimeViewModel", "init: animes != null");
            return;
        }

        //logs to logcat
        //Log.d("AnimeViewModel", "init: animes == null");

        animeRepository = AnimeRepository.getInstance();
        animes = animeRepository.getAnimes();
    }

    public void refreshAnimes() {
        animeRepository.makeAnimeApiCall();
    }
    public LiveData<List<Anime>> getAnimesObserver() {
        return animes;
    }

    public int getAnimeSize(){
        return Objects.requireNonNull(animes.getValue()).size();
    }

    public boolean isLoading() {
        return isLoading;
    }
    public void setLoading(boolean loading) {
        isLoading = loading;
    }
  // method to load new data
    public void loadMore() {
        pageNumber++;
        animeRepository.loadMore(pageNumber);
    }



}