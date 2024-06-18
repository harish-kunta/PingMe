package com.harish.hk185080.chatterbox.interfaces;

public interface IOnUploadProfileImageListener {
    void onSuccess(String imageUrl);
    void onFailure(String errorMessage);
}
