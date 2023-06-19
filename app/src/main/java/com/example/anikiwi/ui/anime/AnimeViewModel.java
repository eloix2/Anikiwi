package com.example.anikiwi.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.anikiwi.networking.Anime;

import java.util.List;

public class AnimeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<Anime>> animes;
    public AnimeViewModel() {
        animes = new MutableLiveData<>();
        animes.setValue(null);
        mText = new MutableLiveData<>();
        mText.setValue("This is anime fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Anime>> getAnimes() {
        return animes;
    }
}