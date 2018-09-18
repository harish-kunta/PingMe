package com.harish.hk185080.chatterbox;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harish.hk185080.chatterbox.data.MyData;

public class ProfileNewActivity extends AppCompatActivity {

    private MyData myData;
    private CoordinatorLayout rootLayout;
    private int mCurrent_state=0;

    //0 = not Friends
    //1 = request received
    //2 = request sent
    //3 = friends

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFavouriteDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mFriendRequestDatabase;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private FirebaseUser mCurrentUser;

    String user_id;

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileEmail;
    private TextView mProfileSendReqBtn, mDeclineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        rootLayout = findViewById(R.id.rootlayout);
        myData = new MyData();

        user_id= getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFavouriteDatabase = FirebaseDatabase.getInstance().getReference().child("Favourites");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mProfileImage = findViewById(R.id.user_profile_image);
       mProfileName = findViewById(R.id.user_profile_name);
//        mProfileEmail = findViewById(R.id.user_profile_email);
//        mProfileStatus = findViewById(R.id.user_profile_status);
//        mProfileSendReqBtn = findViewById(R.id.user_profile_send_request);
//        mDeclineButton = findViewById(R.id.user_profile_decline_request);
//        mProfileBack = findViewById(R.id.profile_back_button);
//        likeButton = findViewById(R.id.like_button);
    }
}
