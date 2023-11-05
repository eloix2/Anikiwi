package com.example.anikiwi.ui.ratings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.RatingWithAnime;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.networking.User;
import com.example.anikiwi.repositories.RatingRepository;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingsViewModel extends ViewModel {
    private static final int DEFAULT_PAGE_LIMIT = 50;
    private static final int DEFAULT_START_PAGE = 1;
    private int pageNumber = DEFAULT_START_PAGE;
    private Map<String, Object> savedQueryParams = new HashMap<>();
    private MutableLiveData<List<RatingWithAnime>> ratedAnimesLiveData;
    private MutableLiveData<Boolean> refreshCompleteLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mText;
    private RatingRepository ratingRepository;

    public RatingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is watching fragment");
    }

    public void init() {
        if(ratedAnimesLiveData != null){
            return;
        }
        ratingRepository = RatingRepository.getInstance();
        // Refreshes the animes list by calling and saving the savedQueryParams
        //reloadAnimes();
        User activeUser = SessionManager.getInstance().getActiveUser();
        if (activeUser != null) {
            reloadRatings();
            ratedAnimesLiveData = ratingRepository.getRatingWithAnimeList();
        }
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<RatingWithAnime>> getRatedAnimesLiveData(String userId) {
        if (ratedAnimesLiveData == null) {
            ratedAnimesLiveData = RatingRepository.getInstance().getRatingWithAnimeList();
        }
        return ratedAnimesLiveData;
    }

    public void reloadRatings() {
        pageNumber = DEFAULT_START_PAGE;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("watchStatus", "watching");
        savedQueryParams.putAll(queryParams);
        queryParams.putAll(getDefaultQueryParams());
        // Clears anime list and loads new data
        ratingRepository.clearRatingWithAnimeList();
        ratingRepository.loadMore(queryParams);
        refreshCompleteLiveData.setValue(true);
    }

    public void refreshRatings() {
        pageNumber = DEFAULT_START_PAGE;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.putAll(savedQueryParams);
        queryParams.putAll(getDefaultQueryParams());
        // Clears anime list and loads new data
        ratingRepository.clearRatingWithAnimeList();
        ratingRepository.loadMore(queryParams);
        refreshCompleteLiveData.setValue(true);
    }

    /**
     * Gets the default query params.
     * @return the default query params
     */
    private Map<String, Object> getDefaultQueryParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", pageNumber);
        queryParams.put("limit", DEFAULT_PAGE_LIMIT);
        queryParams.put("userId", SessionManager.getInstance().getActiveUser().getId());
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

    public LiveData<Boolean> getRefreshCompleteObserver() {
        return refreshCompleteLiveData;
    }



}