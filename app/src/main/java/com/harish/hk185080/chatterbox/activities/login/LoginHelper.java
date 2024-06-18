package com.harish.hk185080.chatterbox.activities.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.harish.hk185080.chatterbox.activities.home.MainActivity;
import com.harish.hk185080.chatterbox.R;

public class LoginHelper {
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            updateUI(user);
                        } else {
                            onLoginFailed();
                        }
                    } else {
                        onLoginFailed();
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

    private void updateUI(FirebaseUser user) {
        String currentUserId = user.getUid();
        String deviceToken = null; // Set your device token logic here if needed

        // Example: Saving device token to Firebase Realtime Database
        // mUserDatabase.child(currentUserId).child("device_token").setValue(deviceToken)
        //         .addOnSuccessListener(aVoid -> sendToMain());

        sendToMain();
    }

    private void sendToMain() {
        context.startActivity(new Intent(context, MainActivity.class));
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

            // Example: Sending password reset email
//             mAuth.sendPasswordResetEmail(email)
//                     .addOnCompleteListener(task -> {
//                         if (task.isSuccessful()) {
//                             Snackbar.make(rootLayout, "Reset Link sent to " + email, Snackbar.LENGTH_LONG).show();
//                         } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
//                             Snackbar.make(rootLayout, "Provided Email ID is not Registered!", Snackbar.LENGTH_LONG).show();
//                         }
//                     });
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

