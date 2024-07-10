package com.harish.hk185080.chatterbox.activities.current_user_profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.harish.hk185080.chatterbox.R;

public class CurrentUserProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_NAME = "extra_user_name";
    public static final String EXTRA_USER_ID = "extra_user_id";
    private static final String TAG = "CurrentUserProfileActivity";

    private TextView textViewUserProfile;
    private ImageView buttonSendMessage;
    private ImageView buttonAddUser;
    private ConstraintLayout rootLayout;
    private CurrentUserProfileHelper userProfileHelper;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_profile);
        rootLayout = findViewById(R.id.rootlayout);

        textViewUserProfile = findViewById(R.id.display_name);
        buttonSendMessage = findViewById(R.id.message_icon);
        buttonAddUser = findViewById(R.id.add_user_icon);
        userProfileHelper = new CurrentUserProfileHelper(this, rootLayout); // Helper class for registration logic

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_USER_NAME) && intent.hasExtra(EXTRA_USER_ID)) {
            String userName = intent.getStringExtra(EXTRA_USER_NAME);
            userId = intent.getStringExtra(EXTRA_USER_ID);
            textViewUserProfile.setText(userName);
            setTitle(userName); // Set action bar title to user's name
        }

        buttonSendMessage.setOnClickListener(v -> {
            // Handle sending message action
            // Example: Start a new activity or show a dialog to compose a message
        });

        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if user is not null
                if (userId == null) {
                    // log that user id is null
                    Log.e(TAG, "User ID is null");
                    return;
                }
                userProfileHelper.sendFriendRequest(userId);
            }
        });
    }
}

