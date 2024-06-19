package com.harish.hk185080.chatterbox.firebase;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.harish.hk185080.chatterbox.interfaces.IOnUploadProfileImageListener;

public class ProfileImageManager {
    private static final String TAG = "ProfileImageManager";
    private StorageReference storageReference;

    public ProfileImageManager() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("profile_images");
        Log.d(TAG, "ProfileImageManager initialized.");
    }

    public void uploadProfileImage(Uri imageUri, String userId, IOnUploadProfileImageListener listener) {
        if (imageUri != null && userId != null) {
            Log.d(TAG, "Uploading profile image for user ID: " + userId);
            StorageReference imageRef = storageReference.child(userId + ".jpg");
            try {
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Log.d(TAG, "Profile image uploaded successfully. URL: " + uri.toString());
                                        listener.onSuccess(uri.toString());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to get profile image URL: " + e.getMessage());
                                        listener.onFailure(e.getMessage());
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to upload profile image: " + e.getMessage());
                            listener.onFailure(e.getMessage());
                        });
            } catch (Exception e) {
                Log.e(TAG, "Exception in uploadProfileImage: " + e.getMessage(), e);
                listener.onFailure("Exception in uploadProfileImage: " + e.getMessage());
            }
        } else {
            Log.w(TAG, "Image URI or User ID is null.");
            listener.onFailure("Image URI or User ID is null");
        }
    }
}


