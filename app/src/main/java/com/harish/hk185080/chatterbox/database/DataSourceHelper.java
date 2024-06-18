package com.harish.hk185080.chatterbox.database;


import com.harish.hk185080.chatterbox.firebase.FirebaseDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;

public class DataSourceHelper {
    public static final boolean userEncryption = false;

    private static IDataSource instance;

    private DataSourceHelper() {
        // Private constructor to enforce singleton pattern
    }

    public static IDataSource getDataSource() {
        if (instance == null) {
            instance = new FirebaseDataSource();
        }
        return instance;
    }

    public static boolean shouldEncryptUser() {
        return userEncryption;
    }
}
