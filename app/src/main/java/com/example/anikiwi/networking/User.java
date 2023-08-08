package com.example.anikiwi.networking;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("username")
    String username;
    @SerializedName("email")
    String email;

    // Constructor
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
