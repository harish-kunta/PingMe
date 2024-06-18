package com.harish.hk185080.chatterbox;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.harish.hk185080.chatterbox.data.Constants;
import com.harish.hk185080.chatterbox.data.MyData;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockedListActivity extends AppCompatActivity {
    private RecyclerView mRequestsList;

    private DatabaseReference mRequestDatabase;
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
        setContentView(R.layout.activity_blocked_list);

        rootLayout=findViewById(R.id.rootlayout);

        mToolbar = findViewById(R.id.blocked_list_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Blocked Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myData = new MyData();

        if (!myData.isInternetConnected(getApplicationContext())) {
            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
        }
        mRequestsList = findViewById(R.id.blocked_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mRequestDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(this));
        mRequestsList.addItemDecoration(new DividerItemDecoration(this,
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
            if(!thumb_image.equals("default")) {
                Glide
                        .with(getApplicationContext())
                        .load(thumb_image)
                        .into(userImageView);
            }
            else
            {
                userImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_account_circle_white_48dp));

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

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    public void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Blocking")
                .child(mCurrent_user_id)
                .orderByChild("block_type").equalTo("sent");


        FirebaseRecyclerOptions<Friends> friendOptions =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder>(friendOptions) {
            @Override
            public FriendsFragment.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FriendsFragment.FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FriendsFragment.FriendsViewHolder holder, int position, Friends model) {
                // Bind the Chat object to the ChatHolder
                holder.setDate(Constants.getFormattedDate(getApplicationContext(),model.date));

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.hasChild("name")) {

                            final String userName = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                            String userStatus=dataSnapshot.child("status").getValue().toString();

                            if (dataSnapshot.hasChild("online")) {
                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                holder.setUserOnline(userOnline);
                            }

                            holder.setName(userName);
                            holder.setUserImage(userThumb,getApplicationContext());
                            holder.setDate(userStatus);
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                              CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
//                              AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//                              builder.setTitle("Select Options");
//                              builder.setItems(options, new DialogInterface.OnClickListener() {
//                                  @Override
//                                  public void onClick(DialogInterface dialog, int which) {
//                                      // Click event for each item
//
//                                      if(which==0)
//                                      {
//                                          Intent profileIntent=new Intent(getContext(),ProfileActivity.class);
//                                          profileIntent.putExtra("user_id",list_user_id);
//                                          startActivity(profileIntent);
//
//                                      }
//                                      if(which==1)
//                                      {
                                    Intent profileIntent = new Intent(getApplicationContext(), MaterialProfileActivity.class);
                                    profileIntent.putExtra("user_id", list_user_id);
                                    startActivity(profileIntent);
//                          }

//                                  }
//                              });
//                              builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }

        };
        mRequestsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
}
