package com.harish.hk185080.chatterbox.interfaces;

import com.harish.hk185080.chatterbox.model.User;

public interface IUserDetailsCallback {
    void onUserDetailsFetched(User userDetails);
    void onUserDetailsFetchFailed(String errorMessage);
}
