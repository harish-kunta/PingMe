package com.harish.hk185080.chatterbox;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.harish.hk185080.chatterbox.Network.ApiUtils;
import com.harish.hk185080.chatterbox.Network.FirebaseApi;
import com.harish.hk185080.chatterbox.Network.FirebaseClient;
import com.harish.hk185080.chatterbox.Network.FirebaseMessage;
import com.harish.hk185080.chatterbox.Network.MessageData;
import com.harish.hk185080.chatterbox.Network.NotificationResponse;
import com.harish.hk185080.chatterbox.Network.NotifyData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class ChatOpenActivity extends AppCompatActivity {

    private String mChatUser;
    private String userName;

    private Toolbar mChatToolbar;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    RelativeLayout mTopBar;
    String image;


    private DatabaseReference mRootRef;
    private String mCurrentUserId;
    private String mCurrentUser;
    private DatabaseReference mUserRef;

    // private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();

    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 1;
    StorageReference mImageStorage;
    //Chat Search
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    public String token;

    //New Solution
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";
    public CoordinatorLayout rootLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_open);
        rootLayout=findViewById(R.id.rootlayout);
        mChatToolbar = findViewById(R.id.users_new_chat_appbar);


        setSupportActionBar(mChatToolbar);



       ActionBar actionBar = getSupportActionBar();
       actionBar.setDisplayHomeAsUpEnabled(true);
       actionBar.setDisplayShowCustomEnabled(true);

//        getSupportActionBar().setTitle("Requests");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mCurrentUser=mAuth.getCurrentUser().getDisplayName();

        mChatUser = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");

//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.cancelAll();


        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(ChatOpenActivity.this).edit();
        prefEditor.putString("ChatOpenUserID", mChatUser);
//        prefEditor.putBoolean("MYBOOLLABEL", true);
//        prefEditor.putInt("MYINTLABEL", 99);
        prefEditor.apply();


        // getSupportActionBar().setTitle(mChatUserName);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        // custom action bar items


        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mTitleView = findViewById(R.id.chat_display_name);
        mLastSeenView = findViewById(R.id.chat_last_seen);
        mProfileImage = findViewById(R.id.custom_bar_image);
        mTopBar = findViewById(R.id.chat_top_bar);

        // mChatAddBtn=findViewById(R.id.chat_add_button);
        mChatSendBtn = findViewById(R.id.send);
        mChatMessageView = findViewById(R.id.input);


//        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent galleryIntent= new Intent();
//                galleryIntent.setType("image/*");
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
//            }
//        });

        mAdapter = new MessageAdapter(messagesList, this,rootLayout);


        mMessagesList = (RecyclerView) findViewById(R.id.chatopenrv);
        mRefreshLayout = findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

//        mMessagesList.addOnItemTouchListener(
//                new RecyclerItemClickListener(ChatActivity.this, mMessagesList ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//                        // do whatever
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        // do whatever
//                        Toast.makeText(ChatActivity.this,"Long Press",Toast.LENGTH_SHORT).show();
//                    }
//                })
//        );
        mMessagesList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) scrollToBottom();
            }

            private void scrollToBottom() {
                mLinearLayout.smoothScrollToPosition(mMessagesList, null, mAdapter.getItemCount());
            }
        });

        //Image Storage
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mRootRef.child("messages").child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mChatUser)) {

                    mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
                    mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(mChatUser)) {
                                Map chatAddMap = new HashMap();
                                chatAddMap.put("seen", false);
                                chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                                Map chatUserMap = new HashMap();
                                chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                                chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);

                                mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Log.d("Chat_Log", databaseError.getMessage().toString());
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadMessages();


        mTitleView.setText(userName);
        mTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileActivity();
            }
        });


        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                 image= dataSnapshot.child("image").getValue().toString();
                if(dataSnapshot.hasChild("device_token")) {
                    token = dataSnapshot.child("device_token").getValue().toString();
                }
                //Snackbar.make(rootLayout,token,Snackbar.LENGTH_LONG).show();
                if(!image.equals("default")) {
                    Glide
                            .with(getApplicationContext())
                            .load(image)
                            .into(mProfileImage);
                }
                else {
                    mProfileImage.setImageDrawable(ContextCompat.getDrawable(ChatOpenActivity.this, R.drawable.ic_account_circle_white_48dp));

                }
                if (online.equals("true")) {
                    mLastSeenView.setText("Online");
                } else {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());


                    mLastSeenView.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();
            }
        });
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_search_text:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.chat_menu, menu);
//        return true;
//    }

    @Override
    public void finish() {
        super.finish();

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.menu_search_text);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_white_24dp));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(edtSeach.getText().toString());
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_white_24dp));

            isSearchOpened = true;
        }
    }

    private void sendToStart() {

        Intent startIntent = new Intent(ChatOpenActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    private void doSearch(String text) {
        Toast.makeText(ChatOpenActivity.this, "Sorry! It will available in next update :)", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(ChatOpenActivity.this).edit();
        prefEditor.putString("ChatOpenUserID", "");
//        prefEditor.putBoolean("MYBOOLLABEL", true);
//        prefEditor.putInt("MYINTLABEL", 99);
        prefEditor.apply();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            final String push_id = user_message_push.getKey();

            StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        //String download_url= task.getResult().getDownloadUrl().toString();
                        String download_url = task.getResult().getUploadSessionUri().toString();
                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);


                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        mChatMessageView.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d("Chat_Log", databaseError.getMessage().toString());
                                }
                            }
                        });


                    }
                }
            });
        }

    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)) {

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if (itemPos == 1) {

                    mLastKey = messageKey;

                }


                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(8, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if (itemPos == 1) {

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size() - 1);

                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void sendMessage() {

        String message = mChatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();
            String push_id = user_message_push.getKey();


            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Chat_Log", databaseError.getMessage().toString());
                    }
                }
            });

            sendNotification(mCurrentUser,message);

        }
    }

    private void sendNotification(String title,String body) {
        NotifyData notifydata = new NotifyData(title,body,"HANDLE_NOTIFICATION");
        MessageData messageData=new MessageData(mCurrentUserId,mCurrentUser,"message");
        FirebaseMessage firebaseMessage=new FirebaseMessage(token,notifydata,messageData);
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

    public void profileActivity() {
        Intent profileIntent = new Intent(ChatOpenActivity.this, ProfileActivity.class);
        profileIntent.putExtra("user_id", mChatUser);
        startActivity(profileIntent);
    }



}