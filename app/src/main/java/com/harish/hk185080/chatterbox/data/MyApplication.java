package com.harish.hk185080.chatterbox.data;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


public class MyApplication extends Application {

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
       // AppEventsLogger.activateApp(this);
        mInstance = this;



    }


}
