package com.harish.hk185080.chatterbox;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.data.Constants;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;

    private String mCurrent_user_id;

    private View mMainView;
    Dialog myDialog;
    Toolbar toolbar;

    private LinearLayout no_friends_layout;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mMainView.findViewById(R.id.friends_list);

        toolbar = mMainView.findViewById(R.id.toolbar);
        // toolbar.setTitleTextColor(this.getResources().getColor(R.color.invertcolor));


        mAuth = FirebaseAuth.getInstance();


        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFriendsList.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        no_friends_layout = mMainView.findViewById(R.id.no_friends_layout);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        startListening();
    }
    private void showPopup(final String userName, String userThumb, String status, final String user_id, View v) {
        TextView userNameText;
        CircleImageView userImageView;
        TextView userStatusText;
        LinearLayout addFriend,sendMessage,addFavourite,userLayout;


        myDialog = new Dialog(v.getContext());

        myDialog.setContentView(R.layout.custompopup);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
        userNameText=myDialog.findViewById(R.id.user_profile_name);
        userImageView=myDialog.findViewById(R.id.user_profile_image);
        userStatusText=myDialog.findViewById(R.id.user_profile_status);
        addFavourite=myDialog.findViewById(R.id.add_favourite);
        addFriend=myDialog.findViewById(R.id.add_friend);
        sendMessage=myDialog.findViewById(R.id.send_message);
        userLayout=myDialog.findViewById(R.id.user_layout);

        userNameText.setText(userName);
        userStatusText.setText(status);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getContext(), ChatOpenActivity.class);
                chatIntent.putExtra("user_id", user_id);
                chatIntent.putExtra("user_name", userName);
                startActivity(chatIntent);
                myDialog.dismiss();
            }
        });
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getContext(), MaterialProfileActivity.class);
                profileIntent.putExtra("user_id",user_id);
                startActivity(profileIntent);
            }
        });

        try {
            if (!userThumb.equals("default")) {
                Glide.with(getContext())
                        .load(userThumb)
                        .into(userImageView);
                //Picasso.get().load(thumb_image).placeholder(R.drawable.ic_account_circle_white_48dp).into(userImageView);
            } else {

                userImageView.setImageResource(R.drawable.ic_account_circle_white_48dp);

            }
        } catch (Exception e) {
            e.printStackTrace();
            userImageView.setImageResource(R.drawable.ic_account_circle_white_48dp);

        }



    }
    public void startListening() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friends")
                .child(mCurrent_user_id);


        FirebaseRecyclerOptions<Friends> friendOptions =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(friendOptions) {
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FriendsViewHolder holder, int position, Friends model) {
                // Bind the Chat object to the ChatHolder

                holder.setDate(Constants.getFormattedDate(getContext(), model.date));

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("name")) {

                            final String userName = dataSnapshot.child("name").getValue().toString();
                            final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                            final String status=dataSnapshot.child("status").getValue().toString();


                            if (dataSnapshot.hasChild("online")) {
                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                holder.setUserOnline(userOnline);
                            }

                            holder.setName(userName);
                            CircleImageView userImageView=holder.setUserImage(userThumb, getContext());
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
                                    Intent chatIntent = new Intent(getContext(), ChatOpenActivity.class);
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
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }

        };
        mFriendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    no_friends_layout.setVisibility(View.VISIBLE);
                }
                else {
                    no_friends_layout.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        CircleImageView userImageView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView userNameView = mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);

        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public CircleImageView setUserImage(String thumb_image, Context ctx) {
            userImageView = mView.findViewById(R.id.user_single_image);
            try {
                if (!thumb_image.equals("default")) {
                    Glide.with(ctx)
                            .load(thumb_image)
                            .into(userImageView);
                    //Picasso.get().load(thumb_image).placeholder(R.drawable.ic_account_circle_white_48dp).into(userImageView);
                } else {

                    userImageView.setImageResource(R.drawable.ic_account_circle_white_48dp);

                }
            } catch (Exception e) {
                e.printStackTrace();
                userImageView.setImageResource(R.drawable.ic_account_circle_white_48dp);

            }


            return userImageView;
        }



        public void setUserOnline(String online_status) {
            View userOnlineView = mView.findViewById(R.id.user_single_online);
            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);
            } else {

                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

    }


}
