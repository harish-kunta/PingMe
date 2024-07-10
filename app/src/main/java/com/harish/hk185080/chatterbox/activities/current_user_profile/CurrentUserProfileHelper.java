package com.harish.hk185080.chatterbox.activities.current_user_profile;

import android.app.Activity;
import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;

public class CurrentUserProfileHelper {
    private static final String TAG = "CurrentUserProfileHelper";
    private Context context;
    private Activity activity;
    private ConstraintLayout rootLayout;
    private IDataSource dataSource;

    public CurrentUserProfileHelper(Context context, ConstraintLayout rootLayout) {
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

