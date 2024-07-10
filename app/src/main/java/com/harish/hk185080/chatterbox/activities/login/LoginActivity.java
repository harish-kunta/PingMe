package com.harish.hk185080.chatterbox.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.home.MainActivity;
import com.harish.hk185080.chatterbox.activities.register.RegisterActivity;
import com.harish.hk185080.chatterbox.authentication.MyGoogleAuthProvider;
import com.harish.hk185080.chatterbox.interfaces.IAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;
    private SignInButton _googleButton;
    private TextView _forgotPassword;
    private ScrollView rootLayout;
    private LoginHelper loginHelper;
    private IAuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // Initialize the desired authentication provider
        // TODO : make it dynamic
        authProvider = new MyGoogleAuthProvider(this);

        initializeViews();
        setupAnimations();

        loginHelper = new LoginHelper(this, rootLayout);
        // Set up the callback
        authProvider.setAuthCallback(new IAuthProvider.AuthCallback() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                // Update UI or navigate to main activity
                loginHelper.sendToMain();
            }

            @Override
            public void onAuthFailure(Exception e) {
                // Handle authentication failure
                Log.e("SignInActivity", "Authentication failed", e);
                // Update UI to show error message
                Snackbar.make(rootLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void initializeViews() {
        rootLayout = findViewById(R.id.rootlayout);
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _googleButton = findViewById(R.id.google_sign_in_button);
        _signupLink = findViewById(R.id.link_signup);
        _forgotPassword = findViewById(R.id.forgot_password);

        _loginButton.setOnClickListener(v -> loginHelper.loginWithEmail(
                _emailText.getText().toString().trim(),
                _passwordText.getText().toString().trim()));

        _googleButton.setOnClickListener(v -> authProvider.signIn());

        _signupLink.setOnClickListener(v -> navigateToRegister());

        _forgotPassword.setOnClickListener(v -> loginHelper.showForgotPasswordDialog());
    }

    private void setupAnimations() {
        View view = findViewById(R.id.imageLogo);

        RotateAnimation rotate = new RotateAnimation(0.0f, 1080.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ScaleAnimation scale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(rotate);
        set.addAnimation(scale);
        set.addAnimation(alpha);
        set.setDuration(2000);

        view.setAnimation(set);
    }

    private void navigateToRegister() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authProvider.handleActivityResult(requestCode, resultCode, data);
    }
}
