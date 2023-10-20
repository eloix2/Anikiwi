package com.example.anikiwi.ui.watching;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.RatingWithAnime;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.networking.User;
import com.example.anikiwi.repositories.AnimeRepository;
import com.example.anikiwi.repositories.RatingRepository;

import java.util.List;

public class WatchingViewModel extends ViewModel {
    private MutableLiveData<List<RatingWithAnime>> ratedAnimesLiveData;
    private final MutableLiveData<String> mText;
    private RatingRepository ratingRepository;

    public WatchingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is watching fragment");
    }

    public void init() {
        if(ratedAnimesLiveData != null){
            return;
        }
        ratingRepository = RatingRepository.getInstance();
        // Refreshes the animes list by calling and saving the savedQueryParams
        //refreshAnimes();
        User activeUser = SessionManager.getInstance().getActiveUser();
        if (activeUser != null) {
            ratedAnimesLiveData = ratingRepository.getAnimesRatedByUser(activeUser.getId());
        }
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<RatingWithAnime>> getRatedAnimesLiveData(String userId) {
        if (ratedAnimesLiveData == null) {
            ratedAnimesLiveData = RatingRepository.getInstance().getAnimesRatedByUser(userId);
        }
        return ratedAnimesLiveData;
    }

}