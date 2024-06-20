package com.harish.hk185080.chatterbox.fragments.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.firebase.UserManager;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.interfaces.IUserContactDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<List<User>> searchResultsLiveData;
    private IDataSource dataSource;

    public SearchViewModel() {
        searchResultsLiveData = new MutableLiveData<>();
        dataSource = DataSourceHelper.getDataSource();
    }

    public LiveData<List<User>> getSearchResults() {
        return searchResultsLiveData;
    }

    public void searchUsersByName(String name) {
        dataSource.searchUsersByName(name, new IUserContactDetailsCallback() {
            @Override
            public void onUserDetailsFetched(List<User> userDetails) {
                searchResultsLiveData.setValue(userDetails);
            }
            @Override
            public void onUserDetailsFetchFailed(String errorMessage) {
                searchResultsLiveData.setValue(null);
            }
        }, 5);
    }
}

