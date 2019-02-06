package com.harish.hk185080.chatterbox;

import android.content.Intent;
import android.os.Bundle;
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
import com.harish.hk185080.chatterbox.welcomeScreen.Popular;

import de.hdodenhof.circleimageview.CircleImageView;

public class PopularUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;


    private DatabaseReference mUsersDatabase, mUserRef;

    private FirebaseAuth mAuth;
    private RecyclerView mPopularList;

    FirebaseUser mCurrentUser;
    private RelativeLayout rootLayout;
    private DatabaseReference mPopularDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_users);
        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        rootLayout = findViewById(R.id.rootlayout);
        mPopularList = findViewById(R.id.users_list);
        getSupportActionBar().setTitle("Popular Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUsersDatabase.keepSynced(true);
        mPopularList.setHasFixedSize(true);
        mPopularList.setLayoutManager(new LinearLayoutManager(this));
        mPopularList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if (currentUser == null) {
            sendToStart();

        } else {
            mUserRef.child("online").setValue("true");

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

    public void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Popular");


        FirebaseRecyclerOptions<Popular> friendOptions =
                new FirebaseRecyclerOptions.Builder<Popular>()
                        .setQuery(query, Popular.class)
                        .build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Popular, FriendsFragment.FriendsViewHolder>(friendOptions) {
            @Override
            public FriendsFragment.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FriendsFragment.FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FriendsFragment.FriendsViewHolder holder, int position, Popular model) {
                // Bind the Chat object to the ChatHolder

                //holder.setDate(Constants.getFormattedDate(getApplicationContext(), model.date));

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("name")) {

                            final String userName = dataSnapshot.child("name").getValue().toString();
                            final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                            final String status = dataSnapshot.child("status").getValue().toString();


                            if (dataSnapshot.hasChild("online")) {
                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                holder.setUserOnline(userOnline);
                            }

                            holder.setName(userName);
                            holder.setDate(status);
                            CircleImageView userImageView = holder.setUserImage(userThumb, getApplication());
//                            userImageView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    showPopup(userName,userThumb,status,list_user_id,v);
//                                }
//                            });

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
                                    Intent chatIntent = new Intent(getApplicationContext(), MaterialProfileActivity.class);
                                    //ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(PopularUsersActivity.this,findViewById(R.id.user_single_image),"profileImage");
                                    chatIntent.putExtra("user_id", list_user_id);
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
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }

        };
        mPopularList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUsersDatabase.child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(PopularUsersActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


}
