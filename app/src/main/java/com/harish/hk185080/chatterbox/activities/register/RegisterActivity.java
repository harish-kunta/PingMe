package com.harish.hk185080.chatterbox.activities.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.login.StartActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameText, emailText, mobileText, passwordText, reEnterPasswordText;
    private Button signupButton;
    private TextView loginLink;
    private ScrollView rootLayout;
    private RegisterHelper registerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Set the layout for the activity

        initializeViews(); // Initialize UI elements
        registerHelper = new RegisterHelper(this, rootLayout); // Helper class for registration logic

        // Set onClickListener for the signup button
        signupButton.setOnClickListener(v -> {
            // Get user input from the EditText fields
            String name = nameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String mobile = mobileText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            String confirmPassword = reEnterPasswordText.getText().toString().trim();

            // Initiate the signup process using the RegisterHelper
            registerHelper.signup(name, email, mobile, password, confirmPassword);
        });

        // Set onClickListener for the login link
        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    // Initialize UI elements
    private void initializeViews() {
        nameText = findViewById(R.id.input_name);
        emailText = findViewById(R.id.input_email);
        mobileText = findViewById(R.id.input_mobile);
        passwordText = findViewById(R.id.input_password);
        reEnterPasswordText = findViewById(R.id.input_reEnterPassword);
        signupButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);
        rootLayout = findViewById(R.id.rootlayout);
    }

    // Navigate to the login activity
    private void navigateToLogin() {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out); // Apply transition animation
    }
}