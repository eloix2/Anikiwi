package com.example.anikiwi.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.RetrofitClient;
import com.example.anikiwi.networking.RetryQueue;
import com.example.anikiwi.networking.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    //singleton
    private static UserRepository instance;

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        //Log.d("AnimeRepository", "getInstance: " + instance.toString());
        return instance;
    }

    private UserRepository() {
        // TODO: Inicializar el user activo?
        //user = new MutableLiveData<>();
    }

    public static void createUserInDatabase(String displayName, String email) {
        //TODO: DONE - Recuerda tener en cuenta que la api esta siempre caida al inicio
        APIs api = RetrofitClient.getInstance().getApis();
        User user = new User(displayName, email);
        Call<User> call = api.createUserInDatabase(user);
        RetryQueue<User> retryQueue = new RetryQueue<User>() {
            @Override
            protected void handleSuccess(User response) {
                Log.d("UserRepository", "User created: " + response);
                // Handle success
            }

            @Override
            protected void handleConflictError() {
                Log.d("UserRepository", "User creation failed: Conflict - User already exists");
                // Handle conflict error
            }

            @Override
            protected void handleOtherError(int errorCode) {
                Log.d("UserRepository", "User creation failed: Error code " + errorCode);
                // Handle other errors
            }

            @Override
            protected void handleFailure(String errorMessage) {
                Log.d("UserRepository", "User creation failed: " + errorMessage);
                // Handle failure
            }
        };
        retryQueue.enqueue(call);
    }

    public void wakeUp() {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<String> call = api.wakeUp();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("UserRepository", "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("UserRepository", "onFailure: " + t.getMessage());
            }
        });
    }
}
