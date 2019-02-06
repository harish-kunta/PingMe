package com.harish.hk185080.chatterbox;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.harish.hk185080.chatterbox.data.MyData;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name)
    EditText _nameText;
    //    @BindView(R.id.input_address)
//    EditText _addressText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_mobile)
    EditText _mobileText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;
    private MyData myData;
    private ScrollView rootLayout;
    public String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        myData = new MyData();
        rootLayout = findViewById(R.id.rootlayout);
        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myData.isInternetConnected(RegisterActivity.this))
                    if (checkGooglePlayServices()) {
                        signup();
                        //signupnew();
                    } else
                        Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();

            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


    }

    private void signupnew() {
        Intent i=new Intent(RegisterActivity.this,UploadImageActivity.class);
        startActivity(i);
    }


    private void registerUser(final String displayName, final String email, String password, final String mobile) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            String deviceToken = getTokenId();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", displayName);
                            userMap.put("status", getString(R.string.default_status));
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", deviceToken);
                            userMap.put("mobile", mobile);
                            userMap.put("email", email);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // sendVerificationEmail();
//                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
//                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(mainIntent);
//                                        AlertDialog.Builder builder;
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                            builder = new AlertDialog.Builder(RegisterActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//                                        } else {
//                                            builder = new AlertDialog.Builder(RegisterActivity.this);
//                                        }
//                                        builder
//                                                .setMessage("Verification Email was sent to your account")
//                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        login();
//                                                    }
//                                                })
//                                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                                .show();
                                        //FirebaseAuth.getInstance().signOut();
                                        //login();
//                                        finish();
                                        Snackbar.make(rootLayout, "Account Created successfully", Snackbar.LENGTH_LONG).show();
                                        onSignupSuccess();
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            mRegProgress.dismiss();

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Snackbar.make(rootLayout, "Password is too weak!", Snackbar.LENGTH_LONG).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Snackbar.make(rootLayout, "Invalid Credentials!", Snackbar.LENGTH_LONG).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Snackbar.make(rootLayout, "User with this email already exist!", Snackbar.LENGTH_LONG).show();
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                                Snackbar.make(rootLayout, "Authentication failed.", Snackbar.LENGTH_LONG).show();
                            }

                        }

                        // ...
                    }
                });
    }

    private void sendVerificationEmail() {
        try {
            final FirebaseUser user = mAuth.getCurrentUser();
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            // Re-enable button

                            if (task.isSuccessful()) {
                                Snackbar.make(rootLayout, "Verification email sent to " + user.getEmail(), Snackbar.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Snackbar.make(rootLayout, "Failed to send verification email.", Snackbar.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            login();

        }
    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }


        mRegProgress = new ProgressDialog(RegisterActivity.this,
                R.style.AppThemeDialog);
        mRegProgress.setIndeterminate(true);
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.setMessage("Creating Account...");
        mRegProgress.show();

        String name = _nameText.getText().toString();
        //String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
        registerUser(name, email, password, mobile);

    }


    public void onSignupSuccess() {
        //loginUser(_emailText.getText().toString(),_passwordText.getText().toString());
        //setResult(RESULT_OK, null);
        //finish();
        mRegProgress.dismiss();
        Intent uploadIntent = new Intent(RegisterActivity.this, UploadImageActivity.class);
        uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(uploadIntent);
        finish();
    }


    private void onLoginFailed() {
        Intent mainIntent = new Intent(RegisterActivity.this, StartActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    public void onSignupFailed() {
        Snackbar.make(rootLayout, "Login failed", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        //String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

//        if (address.isEmpty()) {
//            _addressText.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            _addressText.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

//        if (mobile.isEmpty() || mobile.length() != 10) {
//            _mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
//        if (mobile.isEmpty() || mobile.length()!=10) {
//            _mobileText.setError("contains 10 digits");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//        }


        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    private void login() {
        // Finish the registration screen and return to the Login activity
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public String getTokenId() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        Log.d(TAG, token);

                    }
                });
        return token;
    }

    private boolean checkGooglePlayServices() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, GooglePlayServicesUtil.getErrorString(status));

            // need to update google play services.
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            dialog.show();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
//            // set the message
//            builder.setMessage("This app use google play services only for optional features")
//                    .setTitle("Do you want to update?"); // set a title
//
//            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    // User clicked OK button
//
//                    final String appPackageName = "com.google.android.gms";
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                    }catch (android.content.ActivityNotFoundException anfe) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                    }
//                }
//            });
//            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    // User cancelled the dialog
//                }
//            });
//            AlertDialog dialog = builder.create();
//            dialog.show();

            return false;
        } else {
            Log.i(TAG, GooglePlayServicesUtil.getErrorString(status));
            // google play services is updated.
            return true;
        }
    }

}
