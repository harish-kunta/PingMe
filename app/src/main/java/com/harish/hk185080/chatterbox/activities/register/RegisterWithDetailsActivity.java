package com.harish.hk185080.chatterbox.activities.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.home.MainActivity;
import com.harish.hk185080.chatterbox.activities.login.LoginActivity;
import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.interfaces.IUserDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

public class RegisterWithDetailsActivity extends AppCompatActivity {
    private EditText nameText, userStatusText, mobileText;
    IDataSource dataSource;
    private AppCompatButton saveDetailsButton;
    private ProgressDialog mRegProgress;
    private ScrollView rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_with_details);
        initializeViews();

        // Get a reference to the ViewPager and set its adapter
        dataSource = DataSourceHelper.getDataSource();
        dataSource.getCurrentUserDetails(new IUserDetailsCallback() {
            @Override
            public void onUserDetailsFetched(User userDetails) {
                if (userDetails != null) {
                    saveDetailsButton.setOnClickListener(v -> {
                        // Get user input from the EditText fields
                        String name = nameText.getText().toString().trim();
                        String userStatus = userStatusText.getText().toString().trim();
                        String mobile = mobileText.getText().toString().trim();
                        // handle form submission
                        submitForm(userDetails, name, userStatus, mobile);
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

    // Initialize UI elements
    private void initializeViews() {
        rootLayout = findViewById(R.id.rootlayout);
        nameText = findViewById(R.id.input_name);
        userStatusText = findViewById(R.id.input_email);
        mobileText = findViewById(R.id.input_mobile);
        saveDetailsButton = findViewById(R.id.btn_save_details);
    }

    private void submitForm(User user, String name, String userStatus, String mobile) {

        IDataSource dataSource = DataSourceHelper.getDataSource();

        User updateUser = new User.Builder(name, user.getEmail())
                .phoneNumber(mobile)
                .bio(userStatus)
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