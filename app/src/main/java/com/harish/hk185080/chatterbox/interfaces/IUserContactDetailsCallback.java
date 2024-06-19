package com.harish.hk185080.chatterbox.interfaces;

import com.harish.hk185080.chatterbox.model.User;

import java.util.List;

public interface IUserContactDetailsCallback {
    void onUserDetailsFetched(List<User> userDetails);
    void onUserDetailsFetchFailed(String errorMessage);
}
