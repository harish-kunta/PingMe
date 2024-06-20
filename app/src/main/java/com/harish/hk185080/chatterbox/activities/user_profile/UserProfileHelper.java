package com.harish.hk185080.chatterbox.activities.user_profile;

import static com.harish.hk185080.chatterbox.utils.StringResourceHelper.checkCapitalLetters;
import static com.harish.hk185080.chatterbox.utils.StringResourceHelper.isValidEmail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.login.LoginActivity;
import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.model.User;
import com.harish.hk185080.chatterbox.utils.StringResourceHelper;

public class UserProfileHelper {
    private static final String TAG = "RegisterHelper";
    private Context context;
    private Activity activity;
    private ConstraintLayout rootLayout;
    private IDataSource dataSource;

    public UserProfileHelper(Context context, ConstraintLayout rootLayout) {
        this.activity = (Activity) context;
        this.context = context;
        this.rootLayout = rootLayout;
        this.dataSource = DataSourceHelper.getDataSource();
    }

    public void sendFriendRequest(String recipientUserId) {
        // Implement logic to send friend request to recipientUserId
        dataSource.sendFriendRequest(recipientUserId, new IDataSourceCallback() {
            @Override
            public void onSuccess() {
                // Handle success (optional)
                Snackbar.make(rootLayout, "Friend request sent successfully", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
                Snackbar.make(rootLayout, "Failed to send friend request: " + errorMessage, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void acceptFriendRequest(String senderUserId) {
        // Implement logic to accept friend request from senderUserId
        dataSource.acceptFriendRequest(senderUserId, new IDataSourceCallback() {
            @Override
            public void onSuccess() {
                // Handle success (optional)
                Snackbar.make(rootLayout, "Friend request accepted", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
                Snackbar.make(rootLayout, "Failed to accept friend request: " + errorMessage, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}

