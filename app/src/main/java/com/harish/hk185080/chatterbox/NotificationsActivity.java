package com.harish.hk185080.chatterbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.activities.login.StartActivity;
import com.harish.hk185080.chatterbox.data.MyData;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView mNotificationsList;

    private DatabaseReference mNotificationDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;

    private String mCurrent_user_id;
    private DatabaseReference mUserRef;
    private Toolbar mToolbar;
    private MyData myData;
    private RelativeLayout rootLayout;
    private LinearLayout noNotificationsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rootLayout = findViewById(R.id.rootlayout);
        noNotificationsLayout = findViewById(R.id.no_notifications);
        mToolbar = findViewById(R.id.notifications_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myData = new MyData();

        if (!myData.isInternetConnected(NotificationsActivity.this)) {
            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
        }
        mNotificationsList = findViewById(R.id.notifications_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications").child(mCurrent_user_id);
        mNotificationDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mNotificationsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mNotificationsList.setLayoutManager(layoutManager);
        mNotificationsList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        startListening();
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


    public class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image) {
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            if (!thumb_image.equals("default")) {
                Glide
                        .with(NotificationsActivity.this)
                        .load(thumb_image)
                        .into(userImageView);
            } else {
                userImageView.setImageDrawable(ContextCompat.getDrawable(NotificationsActivity.this, R.drawable.ic_account_circle_white_48dp));

            }
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

    private void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("notifications").child(mCurrent_user_id);

        FirebaseRecyclerOptions<Notifications> options =
                new FirebaseRecyclerOptions.Builder<Notifications>()
                        .setQuery(query, Notifications.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Notifications, NotificationsViewHolder>(options) {
            @Override
            public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new NotificationsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final NotificationsViewHolder holder, int position, final Notifications model) {
                // Bind the Chat object to the ChatHolder
                holder.setName(model.title);
                holder.setDate(getFormattedDate(getApplicationContext(),model.timestamp));
                // ...
                holder.setBody(model.body);


                final String user_id = model.from_user;


                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        if (holder != null) {
                            holder.setUserImage(userThumb, getApplication());
                        } else {
                            Toast.makeText(NotificationsActivity.this, "null", Toast.LENGTH_SHORT).show();
                        }
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(NotificationsActivity.this, MaterialProfileActivity.class);
                                profileIntent.putExtra("user_id", user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });


            }
        };

        mNotificationsList.setAdapter(adapter);
        adapter.startListening();
        mNotificationDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    noNotificationsLayout.setVisibility(View.VISIBLE);
                } else {
                    noNotificationsLayout.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }
    public String getFormattedDate(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday";
        } else if (now.get(Calendar.DAY_OF_WEEK) == smsTime.get(Calendar.DAY_OF_WEEK)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd/MM/yy", smsTime).toString();
        }
    }

    private void sendToStart() {

        Intent startIntent = new Intent(NotificationsActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


}
