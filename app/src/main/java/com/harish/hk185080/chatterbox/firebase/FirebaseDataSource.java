package com.harish.hk185080.chatterbox.firebase;


import static com.harish.hk185080.chatterbox.model.FirebaseConstants.CHATS;
import static com.harish.hk185080.chatterbox.model.FirebaseConstants.PROFILE_IMAGES;
import static com.harish.hk185080.chatterbox.model.FirebaseConstants.USERS;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.interfaces.IOnUploadProfileImageListener;
import com.harish.hk185080.chatterbox.interfaces.IUserDetailsCallback;
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
                            //save the user details in the database
                            uploadUser(user, new IDataSourceCallback() {
                                @Override
                                public void onSuccess() {
                                    callback.onSuccess();
                                }

                                @Override
                                public void onFailure(String errorMessage) {

                                }
                            });
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


    public void uploadUser(User user, IDataSourceCallback callback) {
        //TODO: verify the error message and change it to on complete listener if error message is too long
        usersDb.child(firebaseUser.getUid()).setValue(user).addOnSuccessListener(unused -> callback.onSuccess()).addOnFailureListener(e -> {

        });
    }

    @Override
    public void getCurrentUserDetails(IUserDetailsCallback callback) {
        if (mAuth.getCurrentUser() != null) {
            getUserDetails(mAuth.getCurrentUser().getUid(), new IUserDetailsCallback() {
                @Override
                public void onUserDetailsFetched(User userDetails) {
                    callback.onUserDetailsFetched(userDetails);
                }

                @Override
                public void onUserDetailsFetchFailed(String errorMessage) {
                    callback.onUserDetailsFetchFailed(errorMessage);
                }
            });
        } else {
            callback.onUserDetailsFetchFailed("User details not found");
        }
    }

    @Override
    public void getUserDetails(String userId, IUserDetailsCallback callback) {
        DatabaseReference userRef = usersDb.child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addUserDetails(snapshot, userId, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void saveDetails(User user, IDataSourceCallback callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (user.getProfilePictureURL() != null) {
                //TODO: update this
                uploadProfileImage(null, new IOnUploadProfileImageListener() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        uploadUser(user, callback, imageUrl);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.onFailure(errorMessage);
                    }
                });
            } else {
                uploadUser(user, callback, null);
            }
        } else {
            callback.onFailure("Error saving the user details");
        }
    }

    public void uploadUser(User user, IDataSourceCallback callback, String imageUrl) {

        //TODO: verify the error message and change it to on complete listener if error message is too long
        usersDb.child(firebaseUser.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void addUserDetails(DataSnapshot snapshot, String userId, IUserDetailsCallback callback) {
        if (snapshot.exists()) {
            User user = snapshot.getValue(User.class);
            callback.onUserDetailsFetched(user);
        } else {
            callback.onUserDetailsFetchFailed("User not found");
        }
    }


    @Override
    public void logoutUser(IDataSourceCallback callback) {
        mAuth.signOut();
        callback.onSuccess();
    }

    @Override
    public void uploadProfileImage(Uri imageUri, IOnUploadProfileImageListener listener) {
        if (imageUri != null) {
            String userId = getCurrentUserId();
            if (userId != null) {
                StorageReference imageRef = storageReference.child(PROFILE_IMAGES).child(userId + ".jpg");
                imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    // Get image URL after successful upload
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        listener.onSuccess(imageUrl);
                    }).addOnFailureListener(e -> {
                        listener.onFailure(e.getMessage());
                    });
                }).addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
            } else {
                listener.onFailure("Error uploading the user profile picture");
            }
        }
    }

    @Override
    public void login(String email, String password, IDataSourceCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            if (mAuth.getCurrentUser().isEmailVerified()) {
                // user is signed in and email is verified
                callback.onSuccess();
            } else {
                // user is signed in but email is not verified
                // callback.onFailure("Please verify your email to login");
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Redirect the user to the OTP verification page
                            callback.onFailure("User email not verified, Verification email sent to " + firebaseUser.getEmail());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onFailure("Error sending verification email");
                        }
                    });
                } else {
                    callback.onFailure("Error logging in");
                }
            }
        }).addOnFailureListener(e -> {
            callback.onFailure(e.getMessage());
        });
    }
}
