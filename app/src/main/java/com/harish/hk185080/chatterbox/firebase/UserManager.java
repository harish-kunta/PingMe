package com.harish.hk185080.chatterbox.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.interfaces.IUserContactDetailsCallback;
import com.harish.hk185080.chatterbox.interfaces.IUserDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String TAG = "UserManager";
    private DatabaseReference usersDb;
    private DatabaseReference contactsDb;

    public UserManager() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersDb = firebaseDatabase.getReference("users");
        usersDb.keepSynced(true);
        contactsDb = firebaseDatabase.getReference("contacts");
        contactsDb.keepSynced(true);
        Log.d(TAG, "UserManager initialized.");
    }

    public void uploadUser(User user, String userId, IDataSourceCallback callback) {
        Log.d(TAG, "Uploading user details for user ID: " + userId);
        try {
            usersDb.child(userId).setValue(user).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User details uploaded successfully.");
                callback.onSuccess();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to upload user details: " + e.getMessage());
                callback.onFailure(e.getMessage());
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in uploadUser: " + e.getMessage(), e);
            callback.onFailure("Exception in uploadUser: " + e.getMessage());
        }
    }

    public void getUserDetails(String userId, IUserDetailsCallback callback) {
        Log.d(TAG, "Fetching user details for user ID: " + userId);
        try {
            DatabaseReference userRef = usersDb.child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            Log.d(TAG, "User details found for user ID: " + userId);
                            callback.onUserDetailsFetched(user);
                        } else {
                            Log.e(TAG, "User data is null for user ID: " + userId);
                            callback.onUserDetailsFetchFailed("User data is null");
                        }
                    } else {
                        Log.e(TAG, "User not found for user ID: " + userId);
                        callback.onUserDetailsFetchFailed("User not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to fetch user details: " + error.getMessage());
                    callback.onUserDetailsFetchFailed(error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in getUserDetails: " + e.getMessage(), e);
            callback.onUserDetailsFetchFailed("Exception in getUserDetails: " + e.getMessage());
        }
    }

    public void fetchContactsForCurrentUser(String currentUserId, IUserContactDetailsCallback callback) {
        Log.d(TAG, "Fetching contacts for user ID: " + currentUserId);
        try {
            contactsDb.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<User> contactsList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User contact = dataSnapshot.getValue(User.class); // Assuming contact names are stored as keys
                        contactsList.add(contact);
                    }
                    callback.onUserDetailsFetched(contactsList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to fetch contacts: " + error.getMessage());
                    callback.onUserDetailsFetchFailed(error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in fetchContactsForCurrentUser: " + e.getMessage(), e);
            callback.onUserDetailsFetchFailed("Exception in fetchContactsForCurrentUser: " + e.getMessage());
        }
    }

    public void searchUsersByName(String name, String currentUserId, IUserContactDetailsCallback callback, int limit) {
        Log.d(TAG, "Searching users with name: " + name);
        try {
            usersDb.orderByChild("fullName").startAt(name).endAt(name + "\uf8ff").limitToFirst(limit).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<User> userList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        // Exclude current user's profile
                        if (!dataSnapshot.getKey().equals(currentUserId)) {
                            userList.add(user);
                        }
                    }
                    callback.onUserDetailsFetched(userList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to search users: " + error.getMessage());
                    callback.onUserDetailsFetchFailed(error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in searchUsersByName: " + e.getMessage(), e);
            callback.onUserDetailsFetchFailed("Exception in searchUsersByName: " + e.getMessage());
        }
    }

}


