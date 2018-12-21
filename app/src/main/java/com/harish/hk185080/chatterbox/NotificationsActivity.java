package com.harish.hk185080.chatterbox;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.harish.hk185080.chatterbox.data.MyData;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rootLayout = findViewById(R.id.rootlayout);

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
        mNotificationsList.setLayoutManager(new LinearLayoutManager(this));
        mNotificationsList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
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
        startListening();
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
                // ...
                holder.setDate(model.body);

                final String user_id = model.from_user;



                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        if(holder!=null)
                        {
                            holder.setUserImage(userThumb,getApplication());
                        }
                        else
                        {
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

        Intent startIntent = new Intent(NotificationsActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


}
