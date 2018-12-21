package com.harish.hk185080.chatterbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;

public class ChangePasswordActivity extends AppCompatActivity {
    private LinearLayout rootLayout;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;
    private String TAG = "CHANGE PASSWORD";


    EditText _currentPassword;

    EditText _newPassword;

    EditText _reEnterNewPassword;

    Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        rootLayout = findViewById(R.id.rootlayout);

        mToolbar = findViewById(R.id.change_password_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _currentPassword=findViewById(R.id.input_current_password);
        _newPassword=findViewById(R.id.input_new_password);
        _reEnterNewPassword=findViewById(R.id.reinput_new_password);

        changePasswordButton=findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changePassword() {

        if (!validate()) {
            return;
        }

        mRegProgress = new ProgressDialog(ChangePasswordActivity.this,
                R.style.AppThemeDialog);
        mRegProgress.setIndeterminate(true);
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.setMessage("Creating Account...");
        mRegProgress.show();


        String currentPassword = _currentPassword.getText().toString();
        String newPassword = _newPassword.getText().toString();
        String reEnterNewPassword = _reEnterNewPassword.getText().toString();

        changePasswordFirebase(currentPassword,reEnterNewPassword);


    }

    private void changePasswordFirebase(String currentPassword, final String reEnterNewPassword) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userEmail=user.getEmail();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(userEmail, currentPassword);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(reEnterNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mRegProgress.dismiss();
                                        Snackbar.make(rootLayout,"Password Changed Successfully",Snackbar.LENGTH_SHORT).show();
                                        sendToSettingsActivity();
                                    } else {
                                        mRegProgress.dismiss();
                                        Log.d(TAG, "Error password not updated");
                                        Snackbar.make(rootLayout,"Error password not updated",Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            mRegProgress.dismiss();
                            Log.d(TAG, "Error auth failed");
                            Snackbar.make(rootLayout,"Please Check Current Password",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendToSettingsActivity() {
        finish();
    }



    public boolean validate() {
        boolean valid = true;

        String currentPassword = _currentPassword.getText().toString();
        String newPassword = _newPassword.getText().toString();
        String reEnterNewPassword = _reEnterNewPassword.getText().toString();


//        if (mobile.isEmpty() || mobile.length() != 10) {
//            _mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//
// }
        if (currentPassword.isEmpty() || currentPassword.length() < 4 || currentPassword.length() > 10) {
            _currentPassword.setError("between 4 and 10 alphanumeric characters");
            Snackbar.make(rootLayout, "between 4 and 10 alphanumeric characters", Snackbar.LENGTH_LONG).show();
            valid = false;
        } else {
            _currentPassword.setError(null);
        }


        if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 10) {
            _newPassword.setError("between 4 and 10 alphanumeric characters");
            Snackbar.make(rootLayout, "between 4 and 10 alphanumeric characters", Snackbar.LENGTH_LONG).show();
            valid = false;
        } else {
            _newPassword.setError(null);
        }
//        if (mobile.isEmpty() || mobile.length()!=10) {
//            _mobileText.setError("contains 10 digits");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//        }


        if (reEnterNewPassword.isEmpty() || reEnterNewPassword.length() < 4 || reEnterNewPassword.length() > 10 || !(reEnterNewPassword.equals(newPassword))) {
            _reEnterNewPassword.setError("Password Do not match");
            Snackbar.make(rootLayout, "Password Do not match", Snackbar.LENGTH_LONG).show();
            valid = false;
        } else {
            _reEnterNewPassword.setError(null);
        }

        return valid;
    }
}
