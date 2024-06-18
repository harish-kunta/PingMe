package com.harish.hk185080.chatterbox.interfaces;

public interface IDataSourceCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
