package com.example.anikiwi.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnimeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AnimeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is anime fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}