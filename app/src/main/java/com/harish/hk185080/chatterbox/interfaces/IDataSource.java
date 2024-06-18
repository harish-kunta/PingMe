package com.harish.hk185080.chatterbox.interfaces;

import com.harish.hk185080.chatterbox.model.User;


public interface IDataSource {

    void createUser(User user, String password, IDataSourceCallback callback);
}
