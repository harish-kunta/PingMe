package com.harish.hk185080.chatterbox.interfaces;

import android.net.Uri;

import com.harish.hk185080.chatterbox.model.User;

import java.util.List;

public interface IDataSource {

    void createUser(User user, String password, IDataSourceCallback callback);
}
