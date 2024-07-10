package com.harish.hk185080.chatterbox.interfaces;

import android.content.Intent;

import com.google.firebase.auth.FirebaseUser;

public interface IAuthProvider {
    void signIn();
    void signOut();
    void handleActivityResult(int requestCode, int resultCode, Intent data);

    interface AuthCallback {
        void onAuthSuccess(FirebaseUser user);
        void onAuthFailure(Exception e);
    }

    void setAuthCallback(AuthCallback callback);
}
