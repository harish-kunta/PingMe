package com.harish.hk185080.chatterbox.activities.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.home.MainActivity;
import com.harish.hk185080.chatterbox.activities.register.RegisterWithDetailsActivity;
import com.harish.hk185080.chatterbox.cache.UserCache;
import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.interfaces.IUserDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

public class LoginHelper {
    private static String TAG = "LoginActivity";
    private Context context;
    private ScrollView rootLayout;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    public LoginHelper(Context context, ScrollView rootLayout) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void loginWithEmail(String email, String password) {
        if (!validate(email, password)) {
            return;
        }

        progressDialog = new ProgressDialog(context, R.style.AppThemeDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getString(R.string.authenticating));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        IDataSource dataSource = DataSourceHelper.getDataSource();
        dataSource.login(email, password, new IDataSourceCallback() {
            @Override
            public void onSuccess() {

                dataSource.getCurrentUserDetails(new IUserDetailsCallback() {
                    @Override
                    public void onUserDetailsFetched(User userDetails) {
                        progressDialog.dismiss();
                        if (userDetails == null) {
                            Log.w(TAG, "User details are null, redirecting to RegisterWithDetailsActivity");
                            openSaveDetailsActivity();
                        } else {
                            if (userDetails.getFullName() == null || userDetails.getFullName().isEmpty()) {
                                Log.w(TAG, "User full name is null or empty");
                            }
                            if (userDetails.getEmail() == null || userDetails.getEmail().isEmpty()) {
                                Log.w(TAG, "User email is null or empty");
                            }

                            // Check other conditions if needed

                            if (userDetails.getFullName() == null || userDetails.getFullName().isEmpty()
                                    || userDetails.getEmail() == null || userDetails.getEmail().isEmpty()) {
                                Log.w(TAG, "Redirecting to RegisterWithDetailsActivity due to missing details");
                                openSaveDetailsActivity();
                            } else {
                                sendToMain();
                            }
                        }
                    }

                    @Override
                    public void onUserDetailsFetchFailed(String errorMessage) {
                        progressDialog.dismiss();
                        Log.w(TAG, "User details doesn't exist in the database, redirecting to RegisterWithDetailsActivity");
                        openSaveDetailsActivity();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
                Snackbar.make(rootLayout, errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(rootLayout, context.getString(R.string.enter_valid_email), Snackbar.LENGTH_LONG).show();
            valid = false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            Snackbar.make(rootLayout, context.getString(R.string.password_length_error), Snackbar.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

    public void sendToMain() {
        context.startActivity(new Intent(context, MainActivity.class));
        if (context instanceof LoginActivity) {
            ((LoginActivity) context).finish();
        }
    }

    private void openSaveDetailsActivity() {
        Intent intent = new Intent(context, RegisterWithDetailsActivity.class);
        context.startActivity(intent);
        if (context instanceof LoginActivity) {
            ((LoginActivity) context).finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.reset_password));
        builder.setView(R.layout.input_alert_dialog);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // Process input and send password reset email
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
            dialog.cancel();
        });
        builder.show();
    }

    private void onLoginFailed() {
        Snackbar.make(rootLayout, context.getString(R.string.login_failed), Snackbar.LENGTH_LONG).show();
    }

}

