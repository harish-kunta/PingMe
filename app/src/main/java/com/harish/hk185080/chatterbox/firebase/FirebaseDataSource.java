package com.harish.hk185080.chatterbox.firebase;


import static com.harish.hk185080.chatterbox.model.FirebaseConstants.CHATS;
import static com.harish.hk185080.chatterbox.model.FirebaseConstants.USERS;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.model.User;


public class FirebaseDataSource implements IDataSource {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DatabaseReference usersDb;
    private DatabaseReference chatsDb;
    private FirebaseDatabase firebaseDatabase;


    public FirebaseDataSource() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = firebaseDatabase.getReference();
        mDatabase.keepSynced(true);
        firebaseUser = mAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        usersDb = firebaseDatabase.getReference(USERS);
        usersDb.keepSynced(true);
        chatsDb = firebaseDatabase.getReference(CHATS);
        chatsDb.keepSynced(true);
    }

    public String getCurrentUserId() {
        if (firebaseUser == null)
            firebaseUser = mAuth.getCurrentUser();
        return firebaseUser.getUid();
    }


    @Override
    public void createUser(User user, String password, IDataSourceCallback callback) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Redirect the user to the OTP verification page
                            callback.onSuccess();
                        }
                    });
                } else {
                    callback.onFailure(task.getException().getMessage());
                }
            } else {
                callback.onFailure("Error creating the user");
            }
        });
    }
}
