package com.harish.hk185080.chatterbox;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.harish.hk185080.chatterbox.activities.login.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {
    private Toolbar mToolbar;


    private RecyclerView mUsersList;
    private FloatingActionButton done;
    private EditText _groupName;
    Map<String, String> timestamp;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    ArrayList<String> multiselect_list = new ArrayList<String>();
    FirebaseUser mCurrentUser;
    String mCurrentEmail;
    private RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mToolbar = findViewById(R.id.create_group_appbar);
        setSupportActionBar(mToolbar);
        rootLayout = findViewById(R.id.rootlayout);
        done = findViewById(R.id.fab);
        _groupName = findViewById(R.id.group_name);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.keepSynced(true);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentEmail = mCurrentUser.getEmail();


        getSupportActionBar().setTitle(getString(R.string.create_group));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mUsersList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        multiselect_list.add(mCurrentUser.getUid());

        _groupName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() <=5 || s.toString().trim().length() >=16 || multiselect_list.size() <= 2) {
                    done.setVisibility(View.GONE);
                } else {
                    done.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup(_groupName.getText().toString());
            }
        });
    }

    private void createGroup(String groupName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");

        DatabaseReference group_push = mDatabase.push();
        final String push_id = group_push.getKey();

        timestamp=ServerValue.TIMESTAMP;

        Map timemap = new HashMap();


        timemap.put("timestamp", timestamp);





        Map usersmap = new HashMap();
        for (String s : multiselect_list) {
            usersmap.put(s, timemap);
        }


        Map groupMap = new HashMap();
        groupMap.put("group_name", groupName);
        groupMap.put("created_by", uid);
        groupMap.put("timestamp", timestamp);
        groupMap.put("image", "default");
        groupMap.put("participants", usersmap);
        groupMap.put("thumb_image", "default");

        Map groupLocationMap = new HashMap();
        groupLocationMap.put(push_id, groupMap);

        mDatabase.updateChildren(groupLocationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    for (String s : multiselect_list) {
                        mUsersDatabase.child(s).child("groups").child(push_id).child("timestamp").setValue(timestamp);
                    }
                }
            }
        });
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
//
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
            protected void onBindViewHolder(final UserViewHolder holder, final int position, final Users model) {
                // Bind the Chat object to the ChatHolder

                holder.setName(model.name);
                // ...
                holder.setStatus(model.status);
                holder.setUserImage(model.thumb_image);

                final String user_id = getRef(position).getKey();


                if (multiselect_list.contains(user_id)) {
                    holder.selected();
                    holder.setUserImage("select");
                } else {
                    holder.unSelected();
                    holder.setUserImage(model.thumb_image);
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!multiselect_list.contains(user_id)) {
                            multiselect_list.add(user_id);
                            holder.itemView.findViewById(R.id.user_layout).setBackgroundColor(getResources().getColor(R.color.colorTransBg));
                            holder.setUserImage("select");
                        } else {
                            multiselect_list.remove(user_id);
                            holder.itemView.findViewById(R.id.user_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
                            holder.setUserImage(model.thumb_image);
                        }

                        if(multiselect_list.size()>1 && _groupName.getText().toString().trim().length()>5)
                        {
                            done.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            done.setVisibility(View.GONE);
                        }

//                        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot snapshot) {
//                                if (snapshot.child("Users").child(user_id).hasChild("name")) {
//                                    // run some code
//                                    Intent profileIntent = new Intent(CreateGroupActivity.this, MaterialProfileActivity.class);
//                                    profileIntent.putExtra("user_id", user_id);
//                                    profileIntent.putExtra("position", String.valueOf(holder.getMyPosition()));
//                                    startActivity(profileIntent);
//                                } else {
//                                    Snackbar.make(rootLayout, "User doesnot exist", Snackbar.LENGTH_SHORT).show();
//                                    Map deleteUserMap = new HashMap();
//                                    deleteUserMap.put("Users/" + user_id, null);
//                                    rootRef.updateChildren(deleteUserMap, new DatabaseReference.CompletionListener() {
//                                        @Override
//                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                            if (databaseError == null) {
//                                                //Chat deleted successfully
//
//                                            } else {
//
//                                                // Snackbar.make(rootLayout, "Something Went Wrong", Snackbar.LENGTH_LONG).show();
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });


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

        public void selected() {
            RelativeLayout user_layout = mView.findViewById(R.id.user_layout);
            user_layout.setBackgroundColor(getResources().getColor(R.color.colorTransBg));
        }

        public void unSelected() {
            RelativeLayout user_layout = mView.findViewById(R.id.user_layout);
            user_layout.setBackgroundColor(getResources().getColor(R.color.transparent));
        }


        public int getMyPosition() {
            return getAdapterPosition();
        }

        public void setUserImage(String thumb_image) {
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            try {
                if (thumb_image.equals("select")) {
                    userImageView.setImageDrawable(ContextCompat.getDrawable(CreateGroupActivity.this, R.drawable.ic_check_circle_pink_24dp));

                } else {
                    if (!thumb_image.equals("default")) {
                        Glide
                                .with(CreateGroupActivity.this)
                                .load(thumb_image)
                                .into(userImageView);
                    } else {
                        userImageView.setImageDrawable(ContextCompat.getDrawable(CreateGroupActivity.this, R.drawable.ic_account_circle_white_48dp));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                userImageView.setImageDrawable(ContextCompat.getDrawable(CreateGroupActivity.this, R.drawable.ic_account_circle_white_48dp));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(CreateGroupActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }


}
