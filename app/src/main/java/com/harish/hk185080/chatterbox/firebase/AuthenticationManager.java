package com.harish.hk185080.chatterbox.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.model.User;

public class AuthenticationManager {
    private static final String TAG = "AuthenticationManager";
    private FirebaseAuth mAuth;

    public AuthenticationManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void createUser(String email, String password, IDataSourceCallback callback) {
        Log.d(TAG, "Creating user with email: " + email);
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User signed up successfully.");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                firebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "Verification email sent.");
                                        callback.onSuccess();
                                    } else {
                                        Log.e(TAG, "Failed to send verification email: " + task1.getException().getMessage());
                                        callback.onFailure(task1.getException().getMessage());
                                    }
                                });
                            } else {
                                Log.e(TAG, "Firebase user is null after creation.");
                                callback.onFailure("Firebase user is null after creation.");
                            }
                        } else {
                            Log.e(TAG, "Error creating user: " + task.getException().getMessage());
                            callback.onFailure(task.getException().getMessage());
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating user: " + e.getMessage());
                        callback.onFailure(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in createUser: " + e.getMessage(), e);
            callback.onFailure("Exception in createUser: " + e.getMessage());
        }
    }

    public void login(String email, String password, IDataSourceCallback callback) {
        Log.d(TAG, "Logging in with email: " + email);

        try {
            FirebaseRemoteConfig.getInstance().fetchAndActivate()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean emailVerificationRequired = FirebaseRemoteConfig.getInstance()
                                    .getBoolean("email_verification_required");

                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(authResult -> handleLoginSuccess(mAuth.getCurrentUser(), emailVerificationRequired, callback))
                                    .addOnFailureListener(e -> handleLoginFailure(e, callback));
                        } else {
                            Log.e(TAG, "Failed to fetch and activate remote config: " + task.getException().getMessage());
                            handleDefaultLoginFlow(email, password, callback);
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch and activate remote config: " + e.getMessage());
                        handleDefaultLoginFlow(email, password, callback);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception in login: " + e.getMessage(), e);
            callback.onFailure("Exception in login: " + e.getMessage());
        }
    }

    private void handleLoginSuccess(FirebaseUser firebaseUser, boolean emailVerificationRequired, IDataSourceCallback callback) {
        if (firebaseUser != null && (!emailVerificationRequired || firebaseUser.isEmailVerified())) {
            Log.d(TAG, "User logged in and email is verified.");
            callback.onSuccess();
        } else if (firebaseUser != null && !firebaseUser.isEmailVerified()) {
            Log.w(TAG, "User logged in but email is not verified.");
            sendVerificationEmail(firebaseUser.getEmail(), callback);
        } else {
            Log.e(TAG, "Error logging in: Firebase user is null.");
            callback.onFailure("Error logging in");
        }
    }

    private void handleLoginFailure(Exception e, IDataSourceCallback callback) {
        Log.e(TAG, "Error logging in: " + e.getMessage());
        callback.onFailure(e.getMessage());
    }

    private void handleDefaultLoginFlow(String email, String password, IDataSourceCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> handleLoginSuccess(mAuth.getCurrentUser(), true, callback))
                .addOnFailureListener(e -> handleLoginFailure(e, callback));
    }

    private void sendVerificationEmail(String userEmail, IDataSourceCallback callback) {
        mAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Verification email sent to: " + userEmail);
                        callback.onFailure("User email not verified, Verification email sent to " + userEmail);
                    } else {
                        String errorMessage = "Failed to send verification email: " + task.getException().getMessage();
                        Log.e(TAG, errorMessage);
                        callback.onFailure("Error sending verification email");
                    }
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to send verification email: " + e.getMessage());
                    callback.onFailure("Error sending verification email");
                });
    }


    public void logout(IDataSourceCallback callback) {
        Log.d(TAG, "Logging out user.");
        try {
            mAuth.signOut();
            callback.onSuccess();
            Log.d(TAG, "User logged out successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Exception in logout: " + e.getMessage(), e);
            callback.onFailure("Exception in logout: " + e.getMessage());
        }
    }

    public FirebaseUser getCurrentUser() {
        Log.d(TAG, "Fetching current Firebase user.");
        try {
            return mAuth.getCurrentUser();
        } catch (Exception e) {
            Log.e(TAG, "Exception in getCurrentUser: " + e.getMessage(), e);
            return null;
        }
    }

    public String getCurrentUserId() {
        Log.d(TAG, "Fetching current user ID.");
        try {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                Log.d(TAG, "Current user ID: " + firebaseUser.getUid());
                return firebaseUser.getUid();
            } else {
                Log.e(TAG, "No current user found.");
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in getCurrentUserId: " + e.getMessage(), e);
            return null;
        }
    }

    public void reportFailureToUploadUser(String userId) {
        // You could also add logic to delete the user from Firebase Auth if necessary.
        // For example:
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && firebaseUser.getUid().equals(userId)) {
            firebaseUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Firebase user deleted successfully after upload failure.");
                } else {
                    Log.e(TAG, "Failed to delete Firebase user after upload failure: " + task.getException().getMessage());
                }
            });
        }
    }
}


