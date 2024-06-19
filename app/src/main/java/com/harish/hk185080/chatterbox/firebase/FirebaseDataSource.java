package com.harish.hk185080.chatterbox.firebase;

import android.net.Uri;
import android.util.Log;

import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.interfaces.IOnUploadProfileImageListener;
import com.harish.hk185080.chatterbox.interfaces.IUserContactDetailsCallback;
import com.harish.hk185080.chatterbox.interfaces.IUserDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

public class FirebaseDataSource implements IDataSource {
    private static final String TAG = "FirebaseDataSource";
    private AuthenticationManager authManager;
    private UserManager userManager;
    private ProfileImageManager profileImageManager;

    public FirebaseDataSource() {
        authManager = new AuthenticationManager();
        userManager = new UserManager();
        profileImageManager = new ProfileImageManager();
        Log.d(TAG, "FirebaseDataSource initialized.");
    }

    @Override
    public void createUser(User user, String password, IDataSourceCallback callback) {
        Log.d(TAG, "createUser called.");
        try {
            authManager.createUser(user, password, new IDataSourceCallback() {
                @Override
                public void onSuccess() {
                    try {
                        String userId = authManager.getCurrentUserId();
                        if (userId != null) {
                            tryUploadUser(user, userId, callback, 3);
                        } else {
                            Log.e(TAG, "Error retrieving user ID after creation");
                            callback.onFailure("Error retrieving user ID after creation");
                        }
                    } catch (Exception e) {
                        handleException("Exception while retrieving user ID or uploading user", e, callback);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Failed to create user: " + errorMessage);
                    callback.onFailure(errorMessage);
                }
            });
        } catch (Exception e) {
            handleException("Exception in createUser", e, callback);
        }
    }

    @Override
    public void login(String email, String password, IDataSourceCallback callback) {
        Log.d(TAG, "login called.");
        authManager.login(email, password, callback);
    }

    @Override
    public void fetchContactsForCurrentUser(IUserContactDetailsCallback callback) {
        Log.d(TAG, "getCurrentUserDetails called.");
        try {
            String userId = authManager.getCurrentUserId();
            if (userId != null) {
                userManager.fetchContactsForCurrentUser(userId, callback);
            } else {
                Log.e(TAG, "No current user found.");
                callback.onUserDetailsFetchFailed("No current user found");
            }
        } catch (Exception e) {
            handleException("Exception while getting current user details", e, callback);
        }
    }

    @Override
    public void logoutUser(IDataSourceCallback callback) {
        Log.d(TAG, "logoutUser called.");
        authManager.logout(callback);
    }

    @Override
    public void getCurrentUserDetails(IUserDetailsCallback callback) {
        Log.d(TAG, "getCurrentUserDetails called.");
        try {
            String userId = authManager.getCurrentUserId();
            if (userId != null) {
                userManager.getUserDetails(userId, callback);
            } else {
                Log.e(TAG, "No current user found.");
                callback.onUserDetailsFetchFailed("No current user found");
            }
        } catch (Exception e) {
            handleException("Exception while getting current user details", e, callback);
        }
    }

    @Override
    public void getUserDetails(String userId, IUserDetailsCallback callback) {
        Log.d(TAG, "getUserDetails called for user ID: " + userId);
        try {
            userManager.getUserDetails(userId, callback);
        } catch (Exception e) {
            handleException("Exception while getting user details for user ID: " + userId, e, callback);
        }
    }

    @Override
    public void saveDetails(User user, IDataSourceCallback callback) {
        Log.d(TAG, "saveDetails called.");
        try {
            String userId = authManager.getCurrentUserId();
            if (userId != null) {
                if (user.getProfilePictureURL() != null) {
                    profileImageManager.uploadProfileImage(null, userId, new IOnUploadProfileImageListener() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            // TODO : user.setProfilePictureURL(imageUrl); // Assuming you set the profile picture URL here
                            userManager.uploadUser(user, userId, callback);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e(TAG, "Failed to upload profile image: " + errorMessage);
                            callback.onFailure(errorMessage);
                        }
                    });
                } else {
                    userManager.uploadUser(user, userId, callback);
                }
            } else {
                Log.e(TAG, "Error saving the user details: userId is null.");
                callback.onFailure("Error saving the user details");
            }
        } catch (Exception e) {
            handleException("Exception while saving user details", e, callback);
        }
    }

    @Override
    public void uploadProfileImage(Uri imageUri, IOnUploadProfileImageListener listener) {
        Log.d(TAG, "uploadProfileImage called.");
        try {
            String userId = authManager.getCurrentUserId();
            if (userId != null) {
                profileImageManager.uploadProfileImage(imageUri, userId, listener);
            } else {
                Log.e(TAG, "Error uploading the user profile picture: userId is null.");
                listener.onFailure("Error uploading the user profile picture, userId is null.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception while uploading profile image: " + e.getMessage(), e);
            listener.onFailure("Exception while uploading profile image: " + e.getMessage());
        }
    }

    private void tryUploadUser(User user, String userId, IDataSourceCallback callback, int retryCount) {
        Log.d(TAG, "Attempting to upload user details for user ID: " + userId);
        userManager.uploadUser(user, userId, new IDataSourceCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "User details uploaded successfully.");
                callback.onSuccess();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to upload user details: " + errorMessage);
                if (retryCount > 0) {
                    Log.d(TAG, "Retrying to upload user details. Attempts remaining: " + (retryCount - 1));
                    tryUploadUser(user, userId, callback, retryCount - 1);
                } else {
                    Log.e(TAG, "Max retry attempts reached. Reporting failure.");
                    reportFailureToUploadUser(userId);
                    callback.onFailure("Failed to upload user details after multiple attempts.");
                }
            }
        });
    }

    private void reportFailureToUploadUser(String userId) {
        // Implement logic to report failure, e.g., log to a monitoring system or notify an admin.
        Log.e(TAG, "Reporting failure to upload user details for user ID: " + userId);
        authManager.reportFailureToUploadUser(userId);
    }

    private void handleException(String message, Exception exception, IDataSourceCallback callback) {
        Log.e(TAG, message + ": " + exception.getMessage(), exception);
        callback.onFailure(message + ": " + exception.getMessage());
    }

    private void handleException(String message, Exception exception, IUserDetailsCallback callback) {
        Log.e(TAG, message + ": " + exception.getMessage(), exception);
        callback.onUserDetailsFetchFailed(message + ": " + exception.getMessage());
    }

    private void handleException(String message, Exception exception, IUserContactDetailsCallback callback) {
        Log.e(TAG, message + ": " + exception.getMessage(), exception);
        callback.onUserDetailsFetchFailed(message + ": " + exception.getMessage());
    }
}
