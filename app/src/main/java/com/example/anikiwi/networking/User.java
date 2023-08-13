package com.example.anikiwi.networking;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    String id;
    @SerializedName("username")
    String username;
    @SerializedName("email")
    String email;

    // Constructor

    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
}
