package com.harish.hk185080.chatterbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.harish.hk185080.chatterbox.data.MyData;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ScrollView rootLayout;

    private DatabaseReference mUserDatabase;
    ProgressDialog progressDialog;
    int RC_SIGN_IN = 999;
    MyData myData;
    public String token;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    @BindView(R.id.forgot_password)
    TextView _forgotPassword;
    GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;
    private ProgressDialog mRegProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        myData = new MyData();
        rootLayout = findViewById(R.id.rootlayout);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        checkGooglePlayServices();
        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]


        if (currentUser == null) {

            mAuth = FirebaseAuth.getInstance();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            ButterKnife.bind(this);
            SignInButton signInButton = findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myData.isInternetConnected(StartActivity.this)) {
                        if (checkGooglePlayServices()) {
                            signIn();
                        }
                    } else {
                        Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();

                    }
                }
            });

            final View view = findViewById(R.id.imageLogo);
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


            RotateAnimation rotate
                    = new RotateAnimation(0.0f, 1080.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            ScaleAnimation scale
                    = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);

            AnimationSet set = new AnimationSet(true);
            set.addAnimation(rotate);
            set.addAnimation(scale);
            set.addAnimation(alpha);
            set.setDuration(2000);

            view.setAnimation(set);

            _loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (myData.isInternetConnected(StartActivity.this)) {
                        if (checkGooglePlayServices()) {
                            login();
                        }
                    } else {
                        Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();

                    }
                }
            });

            _signupLink.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Start the Signup activity

                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


                }
            });
            _forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (myData.isInternetConnected(StartActivity.this)) {
                        if (checkGooglePlayServices()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                            builder.setTitle("Reset Password link will be sent to your email");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
                            View viewInflated = LayoutInflater.from(StartActivity.this).inflate(R.layout.input_alert_dialog, (ViewGroup) findViewById(android.R.id.content), false);
// Set up the input
                            final EditText input = viewInflated.findViewById(R.id.input);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                            builder.setView(viewInflated);

// Set up the buttons
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                    dialog.dismiss();
                                    final String userEmail = input.getText().toString().trim();

                                    FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("Reset", "Reset Link sent to " + userEmail);
                                                        Snackbar.make(rootLayout, "Reset Link sent to " + userEmail, Snackbar.LENGTH_LONG).show();
                                                    }

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    if (e instanceof FirebaseAuthInvalidUserException) {

                                                        Snackbar.make(rootLayout, "Provided Email ID is not Registered!", Snackbar.LENGTH_LONG).show();

                                                    }
                                                }
                                            });
                                }
                            });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                    dialog.cancel();
                                }
                            });

                            builder.show();


//                final EditText input = new EditText(StartActivity.this);
//                input.setPadding(40,0,40,0);
//                AlertDialog.Builder builder;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    builder = new AlertDialog.Builder(StartActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//                } else {
//                    builder = new AlertDialog.Builder(StartActivity.this);
//                }
//                builder
//                        .setMessage("Password reset link will be sent to your email")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // continue with sending mail
//                                final String userEmail = input.getText().toString().trim();
//
//                                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    Log.d("Reset", "Reset Link sent to " + userEmail);

//                                                }
//
//                                            }
//                                        })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        if (e instanceof FirebaseAuthInvalidUserException) {
//
//
//                                        }
//                                    }
//                                });
//
//
//
//                            }
//
//
//                        })
//                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setView(input)
//                        .show();
                        }
                    } else {
                        Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();

                    }
                }
            });
        } else {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }


        progressDialog = new ProgressDialog(StartActivity.this,
                R.style.AppThemeDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();

        // TODO: Implement your own authentication logic here.
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {


            loginUser(email, password);
        }

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            //     if (user.isEmailVerified()) {
                            String current_user_id = mAuth.getCurrentUser().getUid();
                            String deviceToken = getTokenId();

                            mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            });
//                            } else {
//                                AlertDialog.Builder builder;
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    builder = new AlertDialog.Builder(StartActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//                                } else {
//                                    builder = new AlertDialog.Builder(StartActivity.this);
//                                }
//                                builder
//                                        .setTitle("Account not Verified!")
//                                        .setMessage("Would you like to verify your Email?")
//                                        .setPositiveButton(R.string.verify_email, new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                sendVerificationEmail();
//                                            }
//                                        })
//                                        .setNegativeButton(R.string.sign_out, new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                FirebaseAuth.getInstance().signOut();
//                                            }
//                                        })
//                                        .setIcon(android.R.drawable.ic_dialog_alert)
//                                        .show();
//
//                            }
                            progressDialog.hide();
                        } else {
                            progressDialog.hide();
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithEmail:failure", task.getException());
                            onLoginFailed();

                        }

                        // ...
                    }
                });
    }

    private void sendVerificationEmail() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button

                        if (task.isSuccessful()) {
//
                            Snackbar.make(rootLayout, "Verification email sent to " + user.getEmail(), Snackbar.LENGTH_LONG).show();

                            FirebaseAuth.getInstance().signOut();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
//
                            Snackbar.make(rootLayout, "Failed to send verification email.", Snackbar.LENGTH_LONG).show();

                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void onLoginFailed() {
        Snackbar.make(rootLayout, "Login failed", Snackbar.LENGTH_LONG).show();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            //Snackbar.make(rootLayout, "Success", Snackbar.LENGTH_LONG).show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            mRegProgress = new ProgressDialog(StartActivity.this,
                    R.style.AppThemeDialog);
            mRegProgress.setIndeterminate(true);
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.setMessage("Authenticating...");
            mRegProgress.show();
            updateUI(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(final GoogleSignInAccount acct) {
        if (acct != null) {
            Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithCredential", task.getException());
                                Snackbar.make(rootLayout, "Login failed", Snackbar.LENGTH_LONG).show();
                                mRegProgress.dismiss();
                            } else {
                                Log.e("GoogleSignIn",acct.getPhotoUrl().toString());
                                String lowQualityPic = acct.getPhotoUrl().toString();
                                String highQualityPic = lowQualityPic.replace("s96-c", "s240-c");

                                registerUser(acct.getDisplayName(),lowQualityPic, highQualityPic, acct.getEmail());
                            }
                        }
                    });
        }
    }

    private void registerUser(final String displayName,final String thumb, final String profilepic, final String email) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = currentUser.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Users/" + uid)) {
                    //User Exists , No Need To add new data.
                    //Get previous data from firebase. It will take previous data as soon as possible..
                    mRegProgress.dismiss();
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();


                } else {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String deviceToken = getTokenId();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", displayName);
                    userMap.put("status", getString(R.string.default_status));
                    userMap.put("image", profilepic);
                    userMap.put("thumb_image", thumb);
                    userMap.put("device_token", deviceToken);
                    userMap.put("email", email);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mRegProgress.dismiss();

                                startActivity(new Intent(StartActivity.this, MainActivity.class));
                                finish();
                            }


                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mRegProgress.dismiss();
            }
        });


    }


    private void openMainPage() {
        Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    //  private void checkGooglePlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//
//            // show your own AlertDialog for example:
//            AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
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
//
//        }
//    }
}
