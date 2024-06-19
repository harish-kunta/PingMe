package com.harish.hk185080.chatterbox.fragments.contacts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IUserContactDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

import java.util.List;

public class ContactsViewModel extends ViewModel {

    private final MutableLiveData<List<User>> contactsLiveData;
    private IDataSource dataSource;

    public ContactsViewModel() {
        contactsLiveData = new MutableLiveData<>();
        dataSource = DataSourceHelper.getDataSource();
        fetchContacts();
    }

    public LiveData<List<User>> getContacts() {
        return contactsLiveData;
    }

    private void fetchContacts() {
        dataSource.fetchContactsForCurrentUser(new IUserContactDetailsCallback() {
            @Override
            public void onUserDetailsFetched(List<User> userDetails) {
                contactsLiveData.setValue(userDetails);
            }
            @Override
            public void onUserDetailsFetchFailed(String errorMessage) {}
        });


    }
}
