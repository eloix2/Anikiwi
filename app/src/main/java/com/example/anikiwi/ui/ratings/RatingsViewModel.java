package com.example.anikiwi.ui.ratings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.RatingWithAnime;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.networking.User;
import com.example.anikiwi.repositories.RatingRepository;
import com.example.anikiwi.utilities.OnDataLoadedListener;

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
    private MutableLiveData<String> watchStatus;
    private RatingRepository ratingRepository;
    private boolean isLoading = false;

    public RatingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is watching fragment");
    }

    public void init(OnDataLoadedListener onDataLoadedListener) {
        if(ratedAnimesLiveData != null){
            return;
        }
        ratingRepository = RatingRepository.getInstance();
        // Refreshes the animes list by calling and saving the savedQueryParams
        //reloadAnimes();
        User activeUser = SessionManager.getInstance().getActiveUser();
        if (activeUser != null) {
            reloadRatings(new OnDataLoadedListener() {
                @Override
                public void onDataLoaded() {
                    // call the callback to notify the UI
                    onDataLoadedListener.onDataLoaded();
                }

                @Override
                public void onDataLoadFailed(String errorMessage) {
                    onDataLoadedListener.onDataLoadFailed(errorMessage);
                }
            });
            ratedAnimesLiveData = ratingRepository.getRatingWithAnimeList();
        }
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

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getWatchStatus() {
        if (watchStatus == null) {
            watchStatus = new MutableLiveData<>();
            watchStatus.setValue("watching");
        }
        return watchStatus;
    }
    public void setWatchStatus(String watchStatus) {
        this.watchStatus.setValue(watchStatus);
    }

    public LiveData<List<RatingWithAnime>> getRatedAnimesLiveData(String userId) {
        if (ratedAnimesLiveData == null) {
            ratedAnimesLiveData = RatingRepository.getInstance().getRatingWithAnimeList();
        }
        return ratedAnimesLiveData;
    }

    public void reloadRatings(OnDataLoadedListener onDataLoadedListener) {
        pageNumber = DEFAULT_START_PAGE;
        Map<String, Object> queryParams = new HashMap<>();
        savedQueryParams.putAll(queryParams);
        queryParams.putAll(getDefaultQueryParams());
        // Clears anime list and loads new data
        ratingRepository.clearRatingWithAnimeList();
        ratingRepository.loadMore(queryParams, new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                // call the callback to notify the UI
                onDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onDataLoadFailed(String errorMessage) {
                onDataLoadedListener.onDataLoadFailed(errorMessage);
            }
        });
        refreshCompleteLiveData.setValue(true);
    }

    public void refreshRatings(OnDataLoadedListener onDataLoadedListener) {
        pageNumber = DEFAULT_START_PAGE;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.putAll(savedQueryParams);
        queryParams.putAll(getDefaultQueryParams());
        // Clears anime list and loads new data
        ratingRepository.clearRatingWithAnimeList();
        ratingRepository.loadMore(queryParams, new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                // call the callback to notify the UI
                onDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onDataLoadFailed(String errorMessage) {
                onDataLoadedListener.onDataLoadFailed(errorMessage);
            }
        });
        refreshCompleteLiveData.setValue(true);
    }

    /**
     * Loads more ratings into the list.
     * Loads more ratings into the list by increasing the page number and calling the API again.
     */
    public void loadMoreData(OnDataLoadedListener onDataLoadedListener) {
        pageNumber++;
        Map<String,Object> queryParams = new HashMap<>();
        queryParams.putAll(savedQueryParams);
        queryParams.putAll(getDefaultQueryParams());
        // Load more data and append to the list
        ratingRepository.loadMore(queryParams, new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                isLoading = false;
                // Call the callback to notify the UI
                onDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onDataLoadFailed(String errorMessage) {
                isLoading = false;
                onDataLoadedListener.onDataLoadFailed(errorMessage);
            }
        });
    }

    /**
     * Gets the default query params.
     * @return the default query params
     */
    private Map<String, Object> getDefaultQueryParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("watchStatus", getWatchStatus().getValue());
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

    //Do Add episodes to rating api call
    public void addEpisodeToRating(String ratingId, OnDataLoadedListener onDataLoadedListener) {
        ratingRepository.addEpisodeToRating(ratingId, new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                onDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onDataLoadFailed(String errorMessage) {
                onDataLoadedListener.onDataLoadFailed(errorMessage);
            }
        });


    }

}