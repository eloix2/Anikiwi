package com.example.anikiwi.repositories;

import android.util.Log;

import com.example.anikiwi.networking.APIs;
import com.example.anikiwi.networking.RetrofitClient;
import com.example.anikiwi.networking.RetryQueue;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.networking.StatisticsResponse;
import com.example.anikiwi.networking.User;

import org.json.JSONException;
import org.json.JSONObject;

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
        return instance;
    }

    private UserRepository() {
        // Inicializamos el sessionManager para settear el user activo
        sessionManager = SessionManager.getInstance();
    }

    /**
     * Creates a user in the database.
     * @param displayName the user's display name
     * @param email the user's email
     * @param onUserCreatedListener the OnUserCreatedListener
     */
    public static void createUserInDatabase(String displayName, String email, OnUserCreatedListener onUserCreatedListener) {
        APIs api = RetrofitClient.getInstance().getApis();
        User user = new User(displayName, email);
        Call<User> call = api.createUserInDatabase(user);
        RetryQueue<User> retryQueue = new RetryQueue<User>() {
            @Override
            protected void handleSuccess(User response) {
                Log.d("UserRepository", "User created: " + response);
                // Assign the response to the active user
                sessionManager.setActiveUser(response);
                // Call the onUserCreatedListener
                onUserCreatedListener.onUserCreated(response);
            }

            @Override
            protected void handleConflictError(JSONObject errorBody) {
                Log.d("UserRepository", "User creation failed: Conflict - User already exists");
                // Assign the errorBody to the active user
                try {
                    // Attempt to retrieve values from the errorBody
                    String id = errorBody.getString("_id");
                    String username = errorBody.getString("username");
                    String email = errorBody.getString("email");

                    // Create a User object with the retrieved values
                    User existingUser = new User(id, username, email);

                    // Notify the onUserCreatedListener that the user already exists
                    onUserCreatedListener.onUserCreated(existingUser);

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle the JSONException, log an error, or provide user feedback as needed
                }
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

    public void getUserStatistics(String userId, final OnStatisticsResponseListener listener) {
        APIs api = RetrofitClient.getInstance().getApis();
        Call<StatisticsResponse> call = api.getUserStatistics(userId);

        call.enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                if (response.isSuccessful()) {
                    StatisticsResponse statistics = response.body();
                    listener.onSuccess(statistics);
                } else {
                    listener.onError(); // You can pass an error message or handle it as needed
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                listener.onError(); // Handle failure
            }
        });
    }

    // Listener interface for callback
    public interface OnStatisticsResponseListener {
        void onSuccess(StatisticsResponse statistics);

        void onError();
    }

    /**
     * Interface for the OnUserCreatedListener.
     * Lets the caller know when the user is created in the database
     */
    public interface OnUserCreatedListener {
        void onUserCreated(User user);
    }
}
