package com.example.anikiwi.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.repositories.AnimeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AnimeViewModel extends ViewModel {
    private MutableLiveData<List<Anime>> animes;
    private AnimeRepository animeRepository;
    private boolean isLoading = false;
    //private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private int pageNumber = 1;
    private String query = "";
    private Map<String, Object> savedQueryParams = new HashMap<>();

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
        pageNumber = 1;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("year", 2023);
        savedQueryParams.putAll(queryParams);

        queryParams.put("page", pageNumber);
        queryParams.put("limit", 50);
        animeRepository.loadMore(queryParams);
        //animeRepository.makeAnimeApiCall();
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
        Map<String,Object> queryParams = new HashMap<>();
        queryParams.putAll(savedQueryParams);

        queryParams.put("page", pageNumber);
        queryParams.put("limit", 50);
        //queryParams.put("year", 2023);
        animeRepository.loadMore(queryParams);
    }
    public void filterAnimes(Map<String, Object> queryParams) {
        pageNumber = 1;
        savedQueryParams.clear();
        savedQueryParams.putAll(queryParams);

        queryParams.put("page", pageNumber);
        queryParams.put("limit", 50);
        animeRepository.makeAnimeApiCall(queryParams);
    }

    public String getSavedQuery(String key) {
        Object value = savedQueryParams.get(key);
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Integer) {
            return String.valueOf(value);
        } else {
            return null; // Handle the case where value is null or of an unexpected type
        }
    }
}