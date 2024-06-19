package com.harish.hk185080.chatterbox.activities.user_profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.harish.hk185080.chatterbox.R;

public class UserProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_NAME = "extra_user_name";

    private TextView textViewUserProfile;
    private Button buttonSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        textViewUserProfile = findViewById(R.id.textViewUserProfile);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_USER_NAME)) {
            String userName = intent.getStringExtra(EXTRA_USER_NAME);
            textViewUserProfile.setText(userName);
            setTitle(userName); // Set action bar title to user's name
        }

        buttonSendMessage.setOnClickListener(v -> {
            // Handle sending message action
            // Example: Start a new activity or show a dialog to compose a message
        });
    }
}

