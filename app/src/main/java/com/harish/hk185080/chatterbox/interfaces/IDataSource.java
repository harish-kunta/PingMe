package com.harish.hk185080.chatterbox.interfaces;

import android.net.Uri;

import com.harish.hk185080.chatterbox.model.User;


public interface IDataSource {

    void createUser(User user, String password, IDataSourceCallback callback);

    void logoutUser(IDataSourceCallback iDataSourceCallback);

    void getCurrentUserDetails(IUserDetailsCallback callback);

    void getUserDetails(String userId, IUserDetailsCallback callback);

    void saveDetails(User user, IDataSourceCallback callback);

    void uploadProfileImage(Uri imageUri, IOnUploadProfileImageListener listener);

    void login(String email, String password, IDataSourceCallback callback);

    void fetchContactsForCurrentUser(IUserContactDetailsCallback iUserContactDetailsCallback);

    void searchUsersByName(String name, IUserContactDetailsCallback iUserContactDetailsCallback, int i);
}
