package com.harish.hk185080.chatterbox.activities.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.home.MainActivity;
import com.harish.hk185080.chatterbox.activities.login.LoginActivity;
import com.harish.hk185080.chatterbox.cache.UserCache;
import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.interfaces.IUserDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

public class RegisterWithDetailsActivity extends AppCompatActivity {

    IDataSource dataSource;
    private ImageView nextButton;
    private ProgressDialog mRegProgress;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_with_details);

        // Get a reference to the ViewPager and set its adapter
        nextButton = findViewById(R.id.btn_signup);
        rootLayout = findViewById(R.id.root_layout);

        dataSource = DataSourceHelper.getDataSource();
        dataSource.getCurrentUserDetails(new IUserDetailsCallback() {
            @Override
            public void onUserDetailsFetched(User userDetails) {
                UserCache.getInstance().cacheUser(userDetails);
                if (userDetails != null) {
                    nextButton.setOnClickListener(v -> {
                        // handle form submission
                        submitForm(userDetails);
                    });
                }
            }

            @Override
            public void onUserDetailsFetchFailed(String errorMessage) {
                Snackbar.make(rootLayout, errorMessage, Snackbar.LENGTH_LONG).show();
                logoutUser();
            }
        });
    }

    private void submitForm(User user) {

        IDataSource dataSource = DataSourceHelper.getDataSource();

        User updateUser = new User.Builder(user.getUsername(), user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .build();

        mRegProgress = new ProgressDialog(getApplicationContext(), R.style.AppThemeDialog);
        mRegProgress.setIndeterminate(true);
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.setMessage("Saving user details...");
        mRegProgress.show();
        dataSource.saveDetails(updateUser, new IDataSourceCallback() {
            @Override
            public void onSuccess() {
                mRegProgress.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                mRegProgress.dismiss();
                Snackbar.make(rootLayout, errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void logoutUser() {
        dataSource.logoutUser(new IDataSourceCallback() {
            @Override
            public void onSuccess() {
                sendToStart();
            }

            @Override
            public void onFailure(String errorMessage) {
                sendToStart();
            }
        });
    }

    private void sendToStart() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


}