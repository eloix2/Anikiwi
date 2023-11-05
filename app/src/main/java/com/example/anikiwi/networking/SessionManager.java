package com.example.anikiwi.networking;

public class SessionManager {
    private static SessionManager instance;
    private static User activeUser;

    // Private constructor to prevent instantiation
    private SessionManager() {}

    /**
     * Gets the SessionManager instance.
     * @return the SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Sets the active user.
     * @param user the user to set as active
     */
    public void setActiveUser(User user) {
        activeUser = user;
    }

    /**
     * Gets the active user.
     * @return the active user
     */
    public User getActiveUser() {
        return activeUser;
    }

    /**
     * Clears the active user.
     */
    public void clearActiveUser() {
        activeUser = null;
    }
}


