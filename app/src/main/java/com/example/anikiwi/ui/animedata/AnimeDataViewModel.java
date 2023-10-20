package com.example.anikiwi.ui.animedata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.Rating;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.repositories.AnimeRepository;
import com.example.anikiwi.repositories.RatingRepository;

public class AnimeDataViewModel extends ViewModel {
    private MutableLiveData<Anime> animeDataLiveData;
    private MutableLiveData<Rating> ratingLiveData;

    private AnimeRepository animeRepository;
    private RatingRepository ratingRepository;

    // Inject API service here

    public void init(String animeId) {
        if (animeDataLiveData != null) {
            return; // ViewModel already initialized
        }
        animeRepository = AnimeRepository.getInstance();
        animeDataLiveData = new MutableLiveData<>();
        animeDataLiveData = animeRepository.getAnime(animeId);

        ratingRepository = RatingRepository.getInstance();
        ratingLiveData = new MutableLiveData<>();
        ratingLiveData = ratingRepository.getRatingByUserIdAndAnimeId(getActiveUserId(), animeId);
    }

    public LiveData<Anime> getAnimeData() {
        return animeDataLiveData;
    }

    public LiveData<Rating> getRatingData() {
        return ratingLiveData;
    }

    public String getActiveUserId() {
        return SessionManager.getInstance().getActiveUser().getId();
    }

    public void rateAnime(Rating rating) {
        ratingRepository.rateAnime(rating);
        // updates the rating live data
        ratingRepository.getRatingByUserIdAndAnimeId(rating.getUserId(), rating.getAnimeId());
    }
}
