package com.harish.hk185080.chatterbox;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.Network.ApiUtils;
import com.harish.hk185080.chatterbox.Network.FirebaseMessage;
import com.harish.hk185080.chatterbox.Network.MessageData;
import com.harish.hk185080.chatterbox.Network.NotifyData;
import com.harish.hk185080.chatterbox.data.MyData;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.NetworkPolicy;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class ProfileActivity extends AppCompatActivity {

    private ImageView  mProfileImage;
    private TextView mProfileName,mProfileStatus, mProfileEmail;
    private TextView mProfileSendReqBtn, mDeclineButton;
    private ImageButton mProfileBack;
    private ImageButton mDownloadImageButton;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFavouriteDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    LikeButton likeButton;
    String filepath;
    private MyData myData;
    public String token;

    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgressDialog;
    private DatabaseReference mFriendRequestDatabase;
    private int mCurrent_state = 0;

    //0 = not Friends
    //1 = request received
    //2 = request sent
    //3 = friends
    private boolean mFavouriteState;
    String image;
    String display_name;
    String email;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private ScrollView rootLayout;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        rootLayout = findViewById(R.id.rootlayout);
        myData = new MyData();

        user_id = getIntent().getStringExtra("user_id");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

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
        mProfileEmail = findViewById(R.id.user_profile_email);
        mProfileStatus = findViewById(R.id.user_profile_status);
        mProfileSendReqBtn = findViewById(R.id.user_profile_send_request);
        mDeclineButton = findViewById(R.id.user_profile_decline_request);
        mProfileBack = findViewById(R.id.profile_back_button);
        likeButton = findViewById(R.id.like_button);
        //mDownloadImageButton=findViewById(R.id.downloadImageButton);

        mCurrent_state = 0;

        mFavouriteState = false;

        mDeclineButton.setVisibility(View.GONE);
        mDeclineButton.setEnabled(false);
        likeButton.setVisibility(View.GONE);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data...");
        mProgressDialog.setMessage("please wait while we load user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                if (dataSnapshot.hasChild("device_token")) {
                    token = dataSnapshot.child("device_token").getValue().toString();
                }
                if (dataSnapshot.hasChild("email")) {
                    email = dataSnapshot.child("email").getValue().toString();
                    mProfileEmail.setVisibility(View.VISIBLE);
                } else {
                    mProfileEmail.setVisibility(View.GONE);
                }
                image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                mProfileEmail.setText(email);
                if(!image.equals("default")) {
                    Glide
                            .with(getApplicationContext())
                            .load(image)
                            .into(mProfileImage);
                }
                else {
                    mProfileImage.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ic_account_circle_white_48dp));

                }

                //Friends List / Request feature
                if (user_id.equals(mCurrentUser.getUid())) {

                    mProfileSendReqBtn.setVisibility(View.GONE);
                    likeButton.setVisibility(View.GONE);

                } else {
                    mProfileSendReqBtn.setVisibility(View.VISIBLE);
                    if (mCurrent_state == 3) {
                        likeButton.setVisibility(View.VISIBLE);
                    }
                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        mFavouriteDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {
                    likeButton.setLiked(true);
                    mFavouriteState = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mFriendRequestDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {
                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                    if (req_type.equals("received")) {

                        mCurrent_state = 1;
                        mProfileSendReqBtn.setText("Accept Friend Request");
                        mDeclineButton.setVisibility(View.VISIBLE);
                        mDeclineButton.setEnabled(true);
                        likeButton.setVisibility(View.GONE);


                    } else if (req_type.equals("sent")) {
                        mCurrent_state = 2;
                        mProfileSendReqBtn.setText("Cancel Friend Request");
                        mDeclineButton.setVisibility(View.GONE);
                        mDeclineButton.setEnabled(false);
                        likeButton.setVisibility(View.GONE);

                    }
                    mProgressDialog.dismiss();
                } else {
                    mFriendDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user_id)) {
                                mCurrent_state = 3;
                                mProfileSendReqBtn.setText("Unfriend this Person");

                                mDeclineButton.setVisibility(View.GONE);
                                mDeclineButton.setEnabled(false);
                                likeButton.setVisibility(View.VISIBLE);

                            }
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mProgressDialog.dismiss();
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        likeButton.setOnLikeListener(new OnLikeListener() {
//            @Override
//            public void liked(final LikeButton likeButton) {
//                Map favouriteMap = new HashMap();
//                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
//
//                favouriteMap.put("Favourites/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
//
//                mRootRef.updateChildren(favouriteMap, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//
//                        if (databaseError == null) {
//                            likeButton.setLiked(true);
//                        } else {
//                            String error = databaseError.getMessage();
//                            Log.e("Profile Activity", error);
//
//                        }
//
//                    }
//                });
//            }

//
//            @Override
//            public void unLiked(final LikeButton likeButton) {
//                if (myData.isInternetConnected(ProfileActivity.this)) {
//                    Map favouriteMap = new HashMap();
//                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
//
//                    favouriteMap.put("Favourites/" + mCurrentUser.getUid() + "/" + user_id, null);
//                    mRootRef.updateChildren(favouriteMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//
//                            if (databaseError == null) {
//                                likeButton.setLiked(false);
//                            } else {
//                                String error = databaseError.getMessage();
//                                Log.e("Profile Activity", error);
//                            }
//
//                        }
//                    });
//                } else {
//                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
//                }
//            }
//        });

        mDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myData.isInternetConnected(ProfileActivity.this)) {
                    Map deleteRequestMap = new HashMap();
                    deleteRequestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                    deleteRequestMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid(), null);


                    mRootRef.updateChildren(deleteRequestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Snackbar.make(rootLayout, "Request Declined Succesfully", Snackbar.LENGTH_LONG).show();
                                finish();

                            } else {
                                String error = databaseError.getMessage();
                                Log.e("Profile Activity", error);
                            }
                        }
                    });

                } else {
                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        mProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                // v.startAnimation(AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.image_click));

            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileImageIntent = new Intent(ProfileActivity.this, ProfileImageActivity.class);
                profileImageIntent.putExtra("user_id", user_id);
                startActivity(profileImageIntent);
            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myData.isInternetConnected(ProfileActivity.this)) {

                    mProfileSendReqBtn.setEnabled(false);
                    //  likeButton.setVisibility(View.GONE);
//                          Not Friends State
                    if (mCurrent_state==0) {

//                        DatabaseReference newNotificationRef = mRootRef.child("notifications").child(user_id).push();
//                        String newnotificationId = newNotificationRef.getKey();


                        HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", mCurrentUser.getUid());
                        notificationData.put("type", "request");


                        Map requestMap = new HashMap();
                        requestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id + "/request_type", "sent");
                        requestMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid() + "/request_type", "received");
                        // requestMap.put("notifications/" + user_id + "/" + newnotificationId, notificationData);
                        mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {

                                    Snackbar.make(rootLayout, "There was some error in sending request", Snackbar.LENGTH_LONG).show();
                                }
                                mProfileSendReqBtn.setEnabled(true);

                                mCurrent_state = 2;
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                sendNotification("Friend Request", display_name + " has sent you friend request");
                            }
                        });


                    }

                    // Cancel request State
                    if (mCurrent_state==2) {
                        mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mProfileSendReqBtn.setEnabled(true);
                                        mCurrent_state = 0;
                                        mProfileSendReqBtn.setText("Send Friend Request");
                                        mDeclineButton.setVisibility(View.GONE);
                                        mDeclineButton.setEnabled(false);


                                    }
                                });
                            }
                        });
                    }

                    // Request Received State
                    if (mCurrent_state==1) {
                        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                        Map friendsMap = new HashMap();
                        friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
                        friendsMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);

                        friendsMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                        friendsMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid(), null);

                        mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                if (databaseError == null) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = 3;
                                    mProfileSendReqBtn.setText("Unfriend this Person");

                                    mDeclineButton.setVisibility(View.GONE);
                                    mDeclineButton.setEnabled(false);

                                } else {
                                    String error = databaseError.getMessage();
                                    Log.e("Profile Activity", error);

                                }

                            }
                        });


                    }


                    //-------------------UN Friend---------------------------

                    if (mCurrent_state==3) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(ProfileActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(ProfileActivity.this);
                        }
                        builder
                                .setMessage("Are you sure you want to unfriend this person?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        Map unFriendMap = new HashMap();
                                        unFriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                                        unFriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(), null);


                                        mRootRef.updateChildren(unFriendMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                                if (databaseError == null) {

                                                    mCurrent_state = 0;
                                                    mProfileSendReqBtn.setText("Send Friend Request");

                                                    mDeclineButton.setVisibility(View.GONE);
                                                    mDeclineButton.setEnabled(false);

                                                    Map removeFavouriteMap = new HashMap();
                                                    removeFavouriteMap.put("Favourites/" + mCurrentUser.getUid() + "/" + user_id, null);
                                                    removeFavouriteMap.put("Favourites/" + user_id + "/" + mCurrentUser.getUid(), null);

                                                    mRootRef.updateChildren(removeFavouriteMap, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        if(databaseError==null)
                                                        {
                                                            likeButton.setVisibility(View.GONE);
                                                        }
                                                        }});
                                                } else {
                                                    String error = databaseError.getMessage();
                                                    Log.e("Profile Activity", error);
                                                }
                                                mProfileSendReqBtn.setEnabled(true);

                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mProfileSendReqBtn.setEnabled(true);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }

                } else {
                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
//        mDownloadImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                requestPermission();
//            }
//        });
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            downloadFile(image);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile(image);
        }
    }

    public void downloadFile(String image) {
        try {


            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/PingMe");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(image);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle(display_name)
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/PingMeFiles", display_name + ".jpg");

            mgr.enqueue(request);

            // Open Download Manager to view File progress
            Snackbar.make(rootLayout, "Downloading...", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(rootLayout, "Error in downloading Image", Snackbar.LENGTH_LONG).show();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if (currentUser == null) {
            sendToStart();

        } else {
            mUserRef.child("online").setValue("true");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(ProfileActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void sendNotification(String title, String body) {
        NotifyData notifydata = new NotifyData(title, body, "HANDLE_REQUEST");
        MessageData messageData = new MessageData(mCurrentUser.getUid(), display_name, "request");
        FirebaseMessage firebaseMessage = new FirebaseMessage(token, notifydata, messageData);
        ApiUtils.sendNotificationService()
                .sendMessage(firebaseMessage)
                .enqueue(new retrofit2.Callback<FirebaseMessage>() {
                    @Override
                    public void onResponse(Call<FirebaseMessage> call, retrofit2.Response<FirebaseMessage> response) {
                        if (response.code() == 200) {


                        } else if (response.code() == 400) {

                        } else if (response.code() == 500) {
                            Log.d("One_login_call", "Server Error");
                            // Toast.makeText(controllerActivity, "Server Error", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d("One_login_call", "SOT API call failed");
                            //Toast.makeText(controllerActivity, "SOT API call failed", Toast.LENGTH_SHORT).show();

                        }
                        Log.d("One_login_Response", response.toString());
                        //closeProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<FirebaseMessage> call, Throwable throwable) {

                    }
                });

    }
}
