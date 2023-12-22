package com.eloix.anikiwi.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eloix.anikiwi.networking.Anime;
import com.eloix.anikiwi.networking.SessionManager;
import com.eloix.anikiwi.repositories.AnimeRepository;
import com.eloix.anikiwi.utilities.OnDataLoadedListener;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<List<Anime>> recommendedAnimesLiveData;
    private AnimeRepository animeRepository;

    public void init(OnDataLoadedListener onDataLoadedListener) {
        if(recommendedAnimesLiveData != null && recommendedAnimesLiveData.getValue() != null){
            // Data is already loaded, notify the UI directly
            onDataLoadedListener.onDataLoaded();
            return;
        }
        recommendedAnimesLiveData = new MutableLiveData<>();
        animeRepository = AnimeRepository.getInstance();
        // Refreshes the animes list by calling and saving the savedQueryParams
        reloadRecommendedAnimes(new OnDataLoadedListener() {
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
    }

    public void reloadRecommendedAnimes(OnDataLoadedListener onDataLoadedListener) {
        animeRepository.makeRecommendedApiCall(getActiveUserId(), new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                // call the callback to notify the UI
                recommendedAnimesLiveData = animeRepository.getRecommendations();
                onDataLoadedListener.onDataLoaded();
            }

            @Override
            public void onDataLoadFailed(String errorMessage) {
                onDataLoadedListener.onDataLoadFailed(errorMessage);
            }
        });
    }

    public String getActiveUserId() {
        return SessionManager.getInstance().getActiveUser().getId();
    }


    public LiveData<List<Anime>> getRecommendations() {
        return recommendedAnimesLiveData;
    }
}