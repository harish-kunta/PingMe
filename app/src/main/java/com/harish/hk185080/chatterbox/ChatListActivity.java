package com.harish.hk185080.chatterbox;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.harish.hk185080.chatterbox.utils.RevealAnimation;
import com.squareup.picasso.NetworkPolicy;


import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    private String mCurrent_user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mToolbar=findViewById(R.id.users_list_appbar);
        setSupportActionBar(mToolbar);

        Intent intent = this.getIntent();   //get the intent to recieve the x and y coords, that you passed before

        RelativeLayout rootLayout =  findViewById(R.id.root_layout); //there you have to get the root layout of your second activity
       // mRevealAnimation = new RevealAnimation(rootLayout, intent, this);

        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        getSupportActionBar().setTitle("New Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersList=findViewById(R.id.users_chat_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mUsersList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mCurrent_user_id=mAuth.getCurrentUser().getUid();

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
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void startListening(){
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(mCurrent_user_id)
                .limitToLast(50);

        FirebaseRecyclerOptions<Friends> friendOptions =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends,FriendsFragment.FriendsViewHolder>(friendOptions) {
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
                holder.setDate(model.date);

                final String list_user_id=getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("name"))
                        {

                            final String userName = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("image").getValue().toString();


                            if (dataSnapshot.hasChild("online")) {
                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                holder.setUserOnline(userOnline);
                            }

                            holder.setName(userName);
                            holder.setUserImage(userThumb,getApplicationContext());

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
                                    Intent chatIntent = new Intent(ChatListActivity.this, ChatOpenActivity.class);
                                    chatIntent.putExtra("user_id", list_user_id);
                                    chatIntent.putExtra("user_name", userName);
                                    startActivity(chatIntent);
//                          }

//                                  }
//                              });
//                              builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled (DatabaseError databaseError){

                    }

                });

            }

        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public class UserViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UserViewHolder(View itemView)
        {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }
        public void setUserImage(String thumb_image) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Glide
                    .with(ChatListActivity.this)
                    .load(thumb_image)
                    .into(userImageView);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        startListening();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if(currentUser==null)
        {   sendToStart();

        }
        else
        {
            mUserRef.child("online").setValue("true");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }
    private void sendToStart() {

        Intent startIntent=new Intent(ChatListActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

}
