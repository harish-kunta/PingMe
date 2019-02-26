package com.harish.hk185080.chatterbox;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;


    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;
    private RelativeLayout createGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    FirebaseUser mCurrentUser;
    String mCurrentEmail;
    private RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        rootLayout = findViewById(R.id.rootlayout);
        createGroup = findViewById(R.id.create_group_layout);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.keepSynced(true);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentEmail = mCurrentUser.getEmail();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myRef.keepSynced(true);
        if(mCurrentEmail.equals("harishtanu007@gmail.com")) {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // textView2.setText(dataSnapshot.getChildrenCount()+"");
                    getSupportActionBar().setTitle(getString(R.string.all_users, dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            getSupportActionBar().setTitle("Send Message");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mUsersList.setLayoutManager(layoutManager);
        mUsersList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

//        SharedPreferences settings = getSharedPreferences("RecyclerView", 0);
//        int item_position = settings.getInt("item_position", 0);
//        Toast.makeText(UsersActivity.this,String.valueOf(item_position),Toast.LENGTH_SHORT).show();
//        mUsersList.scrollTo(item_position,0);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateGroupActivity();
            }
        });


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users").orderByChild("name");
        startListening(query);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if (currentUser == null) {
            sendToStart();

        } else {
            mUserRef.child("online").setValue("true");

        }

    }

    private void openCreateGroupActivity() {
        Intent createGroupIntent = new Intent(UsersActivity.this, CreateGroupActivity.class);
        startActivity(createGroupIntent);
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
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        final MenuItem item = menu.findItem(R.id.menu_search_text);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setItemsVisibility(menu, item, false);
//            }
//        });
//        // Detect SearchView close
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                setItemsVisibility(menu, item, true);
//                return false;
//            }
//        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {

                    Query searchQuery = FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Users")
                            .orderByChild("name").startAt(newText).endAt(newText + "\uf8ff");
                    startListening(searchQuery);

                } else {
//                    Query query = FirebaseDatabase.getInstance()
//                            .getReference()
//                            .child("Friends")
//                            .child(mCurrent_user_id);
                    Query query = FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Users").orderByChild("name");
                    startListening(query);
                }
                //Log.d("SearchText","onQueryTextChange");
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void startListening(Query query) {

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();


        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final UserViewHolder holder, int position, Users model) {
                // Bind the Chat object to the ChatHolder

                holder.setName(model.name);
                // ...
                holder.setStatus(model.status);
                holder.setUserImage(model.thumb_image);

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!user_id.equals(mCurrentUser.getUid())) {
                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.child("Users").child(user_id).hasChild("name")) {
                                        // run some code
//                                    Intent profileIntent = new Intent(UsersActivity.this, MaterialProfileActivity.class);
//                                    profileIntent.putExtra("user_id", user_id);
//                                    startActivity(profileIntent);
                                        Intent chatIntent = new Intent(UsersActivity.this, ChatOpenActivity.class);
                                        chatIntent.putExtra("user_id", user_id);
                                        chatIntent.putExtra("user_name", model.name);
                                        startActivity(chatIntent);

                                    } else {
                                        Snackbar.make(rootLayout, "User doesnot exist", Snackbar.LENGTH_SHORT).show();
                                        Map deleteUserMap = new HashMap();
                                        deleteUserMap.put("Users/" + user_id, null);
                                        rootRef.updateChildren(deleteUserMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError == null) {
                                                    //Chat deleted successfully

                                                } else {

                                                    // Snackbar.make(rootLayout, "Something Went Wrong", Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                        else
                        {
                            Snackbar.make(rootLayout, "You cannot send message to yourself!", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                });

            }


        };
        mUsersList.setAdapter(adapter);

        adapter.startListening();

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

        public int getMyPosition() {
            return getAdapterPosition();
        }

        public void setUserImage(String thumb_image) {
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            try {
                if (!thumb_image.equals("default")) {
                    Glide
                            .with(UsersActivity.this)
                            .load(thumb_image)
                            .into(userImageView);
                } else {
                    userImageView.setImageDrawable(ContextCompat.getDrawable(UsersActivity.this, R.drawable.ic_account_circle_white_48dp));

                }
            } catch (Exception e) {
                e.printStackTrace();
                userImageView.setImageDrawable(ContextCompat.getDrawable(UsersActivity.this, R.drawable.ic_account_circle_white_48dp));
            }
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

        Intent startIntent = new Intent(UsersActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if (currentUser == null) {
            sendToStart();
        } else {
            mUserRef.child("online").setValue("true");
        }
    }


}
