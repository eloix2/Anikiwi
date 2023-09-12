package com.example.anikiwi.ui.animedata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.repositories.AnimeRepository;
import com.example.anikiwi.repositories.UserRepository;

public class AnimeDataViewModel extends ViewModel {
    private MutableLiveData<Anime> animeDataLiveData;

    private AnimeRepository animeRepository;

    // Inject API service here

    public void init(String animeId) {
        if (animeDataLiveData != null) {
            return; // ViewModel already initialized
        }
        animeRepository = AnimeRepository.getInstance();
        animeDataLiveData = new MutableLiveData<>();
        animeDataLiveData = animeRepository.getAnime(animeId);

    }

    public LiveData<Anime> getAnimeData() {
        return animeDataLiveData;
    }


    public String getActiveUserId() {
        return SessionManager.getInstance().getActiveUser().getId();
    }
}
