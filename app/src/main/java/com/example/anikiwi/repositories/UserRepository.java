package com.example.anikiwi.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.RetrofitClient;
import com.example.anikiwi.networking.RetryQueue;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.networking.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    //singleton
    private static UserRepository instance;
    private static SessionManager sessionManager;

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        //Log.d("AnimeRepository", "getInstance: " + instance.toString());
        return instance;
    }

    private UserRepository() {
        // Inicializamos el sessionManager para settear el user activo
        sessionManager = SessionManager.getInstance();
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
                // Assign the response to the active user
                sessionManager.setActiveUser(response);
            }

            @Override
            protected void handleConflictError(JSONObject errorBody) {
                Log.d("UserRepository", "User creation failed: Conflict - User already exists");
                // Handle conflict error
                // Assign the errorBody to the active user
                try {
                    // Attempt to retrieve values from the errorBody
                    String id = errorBody.getString("_id");
                    String username = errorBody.getString("username");
                    String email = errorBody.getString("email");

                    // Create a User object with the retrieved values
                    User existingUser = new User(id, username, email);

                    // Set the active user
                    SessionManager.getInstance().setActiveUser(existingUser);

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle the JSONException, log an error, or provide user feedback as needed
                }
                //sessionManager.setActiveUser(user);

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
