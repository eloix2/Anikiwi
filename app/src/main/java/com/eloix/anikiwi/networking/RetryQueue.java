package com.eloix.anikiwi.networking;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetryQueue<T> {
    private static final int MAX_RETRIES = 15;
    private Queue<Call<T>> requestQueue;

    public RetryQueue() {
        requestQueue = new LinkedList<>();
    }

    public void enqueue(Call<T> call) {
        requestQueue.add(call);
        processQueue();
    }

    private void processQueue() {
        if (!requestQueue.isEmpty()) {
            Call<T> call = requestQueue.poll();
            if (call != null) {
                processCall(call, 0);
            }
        }
    }

    private void processCall(final Call<T> call, final int retries) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    handleSuccess(response.body());
                } else if (response.code() == 409) {
                    JSONObject errorBody = null;
                    try {
                        errorBody = new JSONObject(response.errorBody().string());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    handleConflictError(errorBody);
                } else {
                    handleOtherError(response.code());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (t instanceof SocketTimeoutException && retries < MAX_RETRIES) {
                    Log.d("RetryQueue", "onFailure: Timeout - Retrying...");
                    processCall(call.clone(), retries + 1);
                } else {
                    handleFailure(t.getMessage());
                }
            }
        });
    }

    protected void handleSuccess(T response) {
        // To be implemented by subclasses
    }

    protected void handleConflictError(JSONObject errorBody) {
        // To be implemented by subclasses
    }

    protected void handleOtherError(int errorCode) {
        // To be implemented by subclasses
    }

    protected void handleFailure(String errorMessage) {
        // To be implemented by subclasses
    }
}
