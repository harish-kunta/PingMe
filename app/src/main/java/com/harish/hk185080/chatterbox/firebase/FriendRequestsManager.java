package com.harish.hk185080.chatterbox.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;

public class FriendRequestsManager {
    private static final String TAG = "FriendRequestsManager";
    private static final String USERS_DB = "users";
    private static final String FRIEND_REQUESTS_DB = "friendRequests";

    private DatabaseReference usersDb;
    private DatabaseReference contactsDb;

    public FriendRequestsManager() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersDb = firebaseDatabase.getReference(USERS_DB);
        usersDb.keepSynced(true);
        contactsDb = firebaseDatabase.getReference("contacts");
        contactsDb.keepSynced(true);
        Log.d(TAG, "FriendRequestsManager initialized.");
    }

    public void sendFriendRequest(String currentUserId, String targetUserId, IDataSourceCallback callback) {
        DatabaseReference friendRequestsRef = usersDb.child(targetUserId).child(FRIEND_REQUESTS_DB).child(currentUserId);
        friendRequestsRef.setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Friend request sent successfully to user ID: " + targetUserId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to send friend request to user ID: " + targetUserId + ": " + e.getMessage());
                    callback.onFailure(e.getMessage());
                });
    }

    public void acceptFriendRequest(String currentUserId, String requesterUserId, IDataSourceCallback callback) {
        // Add requesterUserId to currentUserId's contacts
        DatabaseReference currentContactsRef = contactsDb.child(currentUserId).child(requesterUserId);
        currentContactsRef.setValue(true)
                .addOnSuccessListener(aVoid -> {
                    // Add currentUserId to requesterUserId's contacts
                    DatabaseReference requesterContactsRef = contactsDb.child(requesterUserId).child(currentUserId);
                    requesterContactsRef.setValue(true)
                            .addOnSuccessListener(aVoid1 -> {
                                // Remove friend request from requester's node
                                DatabaseReference friendRequestRef = usersDb.child(currentUserId).child(FRIEND_REQUESTS_DB).child(requesterUserId);
                                friendRequestRef.removeValue()
                                        .addOnSuccessListener(aVoid2 -> {
                                            Log.d(TAG, "Friend request accepted successfully from user ID: " + requesterUserId);
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Failed to remove friend request from user ID: " + requesterUserId + ": " + e.getMessage());
                                            callback.onFailure(e.getMessage());
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to add current user to requester's contacts: " + e.getMessage());
                                callback.onFailure(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add requester to current user's contacts: " + e.getMessage());
                    callback.onFailure(e.getMessage());
                });
    }
}

