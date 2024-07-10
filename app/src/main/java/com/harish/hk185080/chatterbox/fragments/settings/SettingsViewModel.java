package com.harish.hk185080.chatterbox.fragments.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IUserDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private IDataSource dataSource;

    public SettingsViewModel() {
        dataSource = DataSourceHelper.getDataSource();
        fetchUserDetails();
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    private void fetchUserDetails() {
        dataSource.getCurrentUserDetails(new IUserDetailsCallback() {
            @Override
            public void onUserDetailsFetched(User user) {
                // Update LiveData with fetched user details
                userLiveData.setValue(user);
            }

            @Override
            public void onUserDetailsFetchFailed(String errorMessage) {
                // Handle failure to fetch user details
            }
        });
    }
}