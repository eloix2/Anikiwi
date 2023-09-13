package com.example.anikiwi.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.repositories.AnimeRepository;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AnimeViewModel extends ViewModel {
    private static final int DEFAULT_PAGE_LIMIT = 50;
    private static final int DEFAULT_START_PAGE = 1;
    private int pageNumber = DEFAULT_START_PAGE;
    private MutableLiveData<List<Anime>> animes;
    private AnimeRepository animeRepository;
    private boolean isLoading = false;
    //private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private Map<String, Object> savedQueryParams = new HashMap<>();

    /**
     * Initializes the AnimeViewModel.
     * Initializes the AnimeViewModel by getting the AnimeRepository instance and the animes list.
     */
    public void init() {
        if(animes != null){
            return;
        }
        animeRepository = AnimeRepository.getInstance();
        // Refreshes the animes list by calling and saving the savedQueryParams
        refreshAnimes();
        animes = animeRepository.getAnimes();
    }

    /**
     * Gets the animes list.
     * @return the animes list
     */
    public LiveData<List<Anime>> getAnimesObserver() {
        return animes;
    }

    /**
     * Gets the size of the animes list.
     * @return the size of the animes list
     */
    public int getAnimeSize(){
        return Objects.requireNonNull(animes.getValue()).size();
    }

    /**
     * Gets the loading state.
     * @return the loading state
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * Sets the loading state.
     * @param loading the loading state
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    /**
     * Refreshes the animes list.
     * Refreshes the animes list by calling the API again with the default query params.
     */
    public void refreshAnimes() {
        pageNumber = DEFAULT_START_PAGE;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("year", Year.now().getValue());
        savedQueryParams.putAll(queryParams);
        queryParams.putAll(getDefaultQueryParams());
        // Clears anime list and loads new data
        animeRepository.clearAnimeList();
        animeRepository.loadMore(queryParams);
    }

    /**
     * Loads more animes into the list.
     * Loads more animes into the list by increasing the page number and calling the API again.
     */
    public void loadMore() {
        pageNumber++;
        Map<String,Object> queryParams = new HashMap<>();
        queryParams.putAll(savedQueryParams);
        queryParams.putAll(getDefaultQueryParams());
        // Load more data and append to the list
        animeRepository.loadMore(queryParams);
    }

    /**
     * Filters the animes list.
     * Filters the animes list by calling the API again with the given query params.
     * @param queryParams the query params to filter the animes list
     */
    public void filterAnimes(Map<String, Object> queryParams) {
        pageNumber = DEFAULT_START_PAGE;
        savedQueryParams.clear();
        savedQueryParams.putAll(queryParams);
        queryParams.putAll(getDefaultQueryParams());
        // Clears anime list and loads new data
        animeRepository.clearAnimeList();
        animeRepository.loadMore(queryParams);
    }

    /**
     * Gets the default query params.
     * @return the default query params
     */
    private Map<String, Object> getDefaultQueryParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", pageNumber);
        queryParams.put("limit", DEFAULT_PAGE_LIMIT);
        return queryParams;
    }

    /**
     * Gets the saved query params.
     * @return the saved query params
     */
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