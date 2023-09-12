package com.example.anikiwi.networking;

public class SessionManager {
    private static SessionManager instance;
    private static User activeUser;

    // Private constructor to prevent instantiation
    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setActiveUser(User user) {
        activeUser = user;
    }

    public User getActiveUser() {
        return activeUser;
    }
}

