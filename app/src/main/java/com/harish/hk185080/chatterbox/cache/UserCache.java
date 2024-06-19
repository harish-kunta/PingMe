package com.harish.hk185080.chatterbox.cache;

import com.harish.hk185080.chatterbox.model.User;

public class UserCache {
    private static UserCache instance;
    private User currentUser;

    private UserCache() {
        // Private constructor to enforce singleton pattern
    }

    public static UserCache getInstance() {
        if (instance == null) {
            instance = new UserCache();
        }
        return instance;
    }

    public void cacheUser(User user) {
        currentUser = user;
    }

    public User getCachedUser() {
        return currentUser;
    }

    public boolean isUserCached() {
        return currentUser != null;
    }
}



