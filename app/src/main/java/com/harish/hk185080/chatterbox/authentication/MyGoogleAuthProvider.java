package com.harish.hk185080.chatterbox.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.interfaces.IAuthProvider;

public class MyGoogleAuthProvider implements IAuthProvider {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private Activity activity;
    private AuthCallback callback;

    public MyGoogleAuthProvider(Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();

        // Initialize One Tap client
        oneTapClient = Identity.getSignInClient(activity);

        // Configure One Tap sign-in
        signInRequest = new BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(activity.getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();
    }

    @Override
    public void signIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(activity, result -> {
                    try {
                        activity.startIntentSenderForResult(result.getPendingIntent().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("GoogleAuthProvider", "Google Sign-In failed", e);
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.e("GoogleAuthProvider", "Google Sign-In failed", e);
                });
    }

    @Override
    public void signOut() {
        mAuth.signOut();
        oneTapClient.signOut().addOnCompleteListener(activity, task -> {
            // Update UI or navigate to sign-in activity
        });
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken);
                }
            } catch (ApiException e) {
                Log.e("GoogleAuthProvider", "Google Sign-In failed", e);
                if (callback != null) {
                    callback.onAuthFailure(e);
                }
            }
        }
    }

    @Override
    public void setAuthCallback(AuthCallback callback) {
        this.callback = callback;
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (callback != null) {
                            callback.onAuthSuccess(user);
                        }
                    } else {
                        Log.e("GoogleAuthProvider", "signInWithCredential:failure", task.getException());
                        if (callback != null) {
                            callback.onAuthFailure(task.getException());
                        }
                    }
                });
    }
}




