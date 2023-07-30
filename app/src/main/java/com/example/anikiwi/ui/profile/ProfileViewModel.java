package com.example.anikiwi.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        //to get name of the user authenticated in the session
        //Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        //para hacer el sign out, además de volver al login
        //FirebaseAuth.getInstance().signOut();
        mText.setValue("This is profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}