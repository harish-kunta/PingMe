package com.harish.hk185080.chatterbox.NewMainActivtiy;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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
import com.harish.hk185080.chatterbox.AboutActivity;
import com.harish.hk185080.chatterbox.CallBackInterface;
import com.harish.hk185080.chatterbox.ChatOpenActivity;
import com.harish.hk185080.chatterbox.ChatsFragment;
import com.harish.hk185080.chatterbox.Conv;
import com.harish.hk185080.chatterbox.MainActivity;
import com.harish.hk185080.chatterbox.MaterialProfileActivity;
import com.harish.hk185080.chatterbox.MaterialSettingsActivity;
import com.harish.hk185080.chatterbox.NewSettingsActivty;
import com.harish.hk185080.chatterbox.PopularUsersActivity;
import com.harish.hk185080.chatterbox.PrivacyPolicyActivity;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.Request_Activity;
import com.harish.hk185080.chatterbox.Services.NetworkChangeReceiver;
import com.harish.hk185080.chatterbox.StartActivity;
import com.harish.hk185080.chatterbox.UsersActivity;
import com.harish.hk185080.chatterbox.adapter.AdapterListInbox;
import com.harish.hk185080.chatterbox.data.DataGenerator;
import com.harish.hk185080.chatterbox.data.MyData;
import com.harish.hk185080.chatterbox.model.Inbox;
import com.harish.hk185080.chatterbox.utils.Tools;
import com.harish.hk185080.chatterbox.widget.LineItemDecoration;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static com.harish.hk185080.chatterbox.data.Constants.CONNECTIVITY_ACTION;


public class BottomNavigationShifting extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,MaterialSearchBar.OnSearchActionListener {

    private TextView mTextMessage;
    private BottomNavigationView navigation;
    private int current_selected_idx = -1;
    FirebaseRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private LinearLayout no_message_layout;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    GoogleSignInClient mGoogleSignInClient;
    CircleImageView circleImageView;
    MaterialSearchBar searchBar;
    private DatabaseReference mUsersDatabase;
    private View search_bar;
    private RecyclerView recyclerView;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private AdapterListInbox mAdapter;
    private Toolbar toolbar;
    DrawerLayout drawer;
    private NavigationView navigationView;
    FloatingActionButton fab;
    TextView text;
    private ImageButton buttonNavigation;
    private static View Header;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUserDatabase;
    FirebaseUser currentUser;
    private MyData myData;
    private DatabaseReference mUserRef;
    private static final String TAG = "MainActivity";
    private Query mConvDatabase;
    private SparseBooleanArray selected_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_shifting);
        // initToolbar();
        initNotification();
        initNavigation();
        init();
        initComponent();
        initRecyclerView();
    }

    private void initNotification() {
        //Firebase notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        String userId;
        String notificationType;
        String userName;
        if (extras != null) {
            userId = extras.getString("user_id");
            notificationType = extras.getString("type");
            userName = extras.getString("user_name");
            if (notificationType != null) {
                if (notificationType.equals("request")) {
                    sendToProfile(userId);
                } else if (notificationType.equals("message")) {
                    sendToChat(userId, userName);
                }
            }

        }
        myData = new MyData();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        Fabric.with(this, new Crashlytics());
        fab = findViewById(R.id.fab);
        text = findViewById(R.id.header);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UsersActivity.class));
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Header = navigationView.getHeaderView(0);
            final TextView textView = Header.findViewById(R.id.name);


            final TextView textView2 = Header.findViewById(R.id.score);
            circleImageView = Header.findViewById(R.id.CimageView);


            if (mAuth.getCurrentUser() != null) {
                mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            }
            Header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUserPage();
                }
            });

            intentFilter = new IntentFilter();
            intentFilter.addAction(CONNECTIVITY_ACTION);
            receiver = new NetworkChangeReceiver();

            //textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            final String current_uid = mCurrentUser.getUid();

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
            mUserDatabase.keepSynced(true);
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {


                        String name = dataSnapshot.child("name").getValue().toString();
                        final String image = dataSnapshot.child("image").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                        textView.setText(name);
                        //textView1.setText(status);
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid);
                        myRef.keepSynced(true);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // textView2.setText(dataSnapshot.getChildrenCount()+"");
                                textView2.setText(getString(R.string.friends, dataSnapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if (!image.equals("default")) {
                            Glide
                                    .with(getApplicationContext())
                                    .load(image)
                                    .into(circleImageView);

                        } else {
                            circleImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_account_circle_white_48dp));
                        }
                    } catch (Exception e) {
                        //sendToStart();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {
            sendToStart();
        }
    }

    private void initNavigation() {
        buttonNavigation = findViewById(R.id.bt_menu);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);

        }

    }


    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_message_layout=findViewById(R.id.no_message_layout);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);
       // mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrentUser.getUid()).orderByChild("timestamp");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrentUser.getUid());
        mConvDatabase.keepSynced(true);
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUser.getUid());
        mMessageDatabase.keepSynced(true);
        Query conversationQuery = mConvDatabase.orderByChild("timestamp");
        actionModeCallback = new ActionModeCallback();
        selected_items = new SparseBooleanArray();


        FirebaseRecyclerOptions<Conv> convOptions =
                new FirebaseRecyclerOptions.Builder<Conv>()
                        .setQuery(conversationQuery, Conv.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Conv, ChatsFragment.ConvViewHolder>(convOptions) {
            @NonNull
            @Override
            public ChatsFragment.ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_inbox, parent, false);

                return new ChatsFragment.ConvViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatsFragment.ConvViewHolder holder, int position, @NonNull final Conv model) {

                final String list_user_id = getRef(position).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_id);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        String time=dataSnapshot.child("time").getValue().toString();
                        holder.setMessage(data, model.isSeen());
                        holder.setTime(getFormattedDate(getApplicationContext(),Long.parseLong(time)));

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


                mUsersDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            try {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                if (dataSnapshot.hasChild("online")) {

                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    holder.setUserOnline(userOnline);

                                }

                                holder.setName(userName);
                                holder.setUserImage(userThumb, getApplicationContext());

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (getSelectedItemCount() > 0) {
                                            enableActionMode(holder.getAdapterPosition());
                                        } else {
                                            Intent chatIntent = new Intent(getApplicationContext(), ChatOpenActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);

                                        }
                                    }
                                });
                                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        enableActionMode(holder.getAdapterPosition());
                                        return false;
                                    }
                                });

                                toggleCheckedIcon(holder, position);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mConvDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            no_message_layout.setVisibility(View.VISIBLE);
                        }
                        else {
                            no_message_layout.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > dy) { // up
                    animatefab(false);
                    animateSearchBar(false);
                }
                if (dx < dy) { // down
                    animatefab(true);
                    animateSearchBar(true);
                }
            }
        });

//        mConvDatabase.keepSynced(true);

//
//        List<Inbox> items = DataGenerator.getInboxData(this, mConvDatabase, new CallBackInterface() {
//            @Override
//            public void jobDone(List<Inbox> data,List<String> uidGroup) {
//
//                Log.v("tejtest", data.size() + "");
//
//
//                mAdapter = new AdapterListInbox(getApplicationContext(), data,uidGroup);
//                recyclerView.setAdapter(mAdapter);
//                mAdapter.setOnClickListener(new AdapterListInbox.OnClickListener() {
//                    @Override
//                    public void onItemClick(View view, Inbox obj, int pos) {
//                        if (mAdapter.getSelectedItemCount() > 0) {
//                            enableActionMode(pos);
//                        } else {
//                            // read the inbox which removes bold from the row
////                            Inbox inbox = mAdapter.getItem(pos);
////                            Toast.makeText(getApplicationContext(), "Read: ", Toast.LENGTH_SHORT).show();
//
//                            Intent chatIntent = new Intent(getApplicationContext(), ChatOpenActivity.class);
//                            chatIntent.putExtra("user_id", uidGroup.get(pos));
//                           // chatIntent.putExtra("user_name", userName);
//                            startActivity(chatIntent);
//
//                        }
//                    }
//
//                    @Override
//                    public void onItemLongClick(View view, Inbox obj, int pos) {
//                        enableActionMode(pos);
//                    }
//                });
//

//

     //       }
      //  });

        // Log.e("Inbox",Long.toString(items.size()));

        //set data and list adapter



    }
    private void toggleCheckedIcon(ChatsFragment.ConvViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.setLytImageVisibility(View.GONE);
            holder.setLytCheckedVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.setLytCheckedVisibility(View.GONE);
            holder.setLytImageVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }
    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    public void toggleItemSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        adapter.notifyItemChanged(pos);
    }
    private void toggleSelection(int position) {

        toggleItemSelection(position);
        int count = getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }
    public int getSelectedItemCount() {
        return selected_items.size();
    }
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inbox");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.white);
    }

    private void initComponent() {
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);
        //searchBar.inflateMenu(R.menu.main);
        //searchBar.setText("Hello World!");
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        search_bar = (View) findViewById(R.id.search_bar);
        mTextMessage = (TextView) findViewById(R.id.search_text);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);
        NestedScrollView nested_content = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    //animateNavigation(false);
                    animateSearchBar(false);
                    animateText(false);
                }
                if (scrollY > oldScrollY) { // down
                    //animateNavigation(true);
                    animateSearchBar(true);
                    animateText(true);
                }
            }
        });


        // display image
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_1), R.drawable.image_8);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_2), R.drawable.image_9);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_3), R.drawable.image_15);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_4), R.drawable.image_14);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_5), R.drawable.image_12);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_6), R.drawable.image_2);
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image_7), R.drawable.image_5);

        ((ImageButton) findViewById(R.id.bt_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    boolean isSearchBarHide = false;
    boolean isfabHide = false;
    boolean isTextHide = false;
    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * search_bar.getHeight()) : 0;
        search_bar.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void animatefab(final boolean hide) {
        if (isfabHide && hide || !isfabHide && !hide) return;
        isfabHide = hide;
        int moveY = hide ? (2 * fab.getHeight()) : 0;
        fab.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }
    private void animateText(final boolean hide) {
        if (isTextHide && hide || !isTextHide && !hide) return;
        isTextHide = hide;
        int moveY = hide ? -(2 * text.getHeight()) : 0;
        text.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.myaccount) {
            openUserPage();
            return false;
        } else if (id == R.id.share) {
            shareApp();
            return false;
        } else if (id == R.id.aboutus) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return false;
        } else if (id == R.id.app_settings) {
            startActivity(new Intent(getApplicationContext(), NewSettingsActivty.class));
            return false;
        } else if (id == R.id.privacy_policy) {
            startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
            return false;
        } else if (id == R.id.logout_button) {
            logout();
            return false;
        } else if (id == R.id.allUsers) {
            openPopularUsers();
            return false;
        } else if (id == R.id.myRequests) {
            Intent i = new Intent(getApplicationContext(), Request_Activity.class);
            startActivity(i);
            return false;
        } else if (id == R.id.feedback) {
            sendFeedback(getApplicationContext());
            return false;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return false;
    }

    private void sendToStart() {
        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    public static void sendFeedback(Context context) {
        String body = null;
        int code;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "." + code + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"harishtanu007@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from Ping Me app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.disableSearch();
                break;
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(BottomNavigationShifting.this, R.color.blue_grey_700);
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteInboxes();
                mode.finish();
                return true;
            }
            return false;
        }

        private void deleteInboxes() {
            List<Integer> selectedItemPositions =getSelectedItems();
            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                removeData(selectedItemPositions.get(i));
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelections();
            actionMode = null;
            Tools.setSystemBarColor(BottomNavigationShifting.this, R.color.white);
        }
    }
    public void removeData(int position) {
        //adapter.remove(position);
        resetCurrentIndex();
    }
    public void clearSelections() {
        selected_items.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                finish();
            }
        }


    }

    private void openPopularUsers() {
        Intent popularUsersIntent = new Intent(getApplicationContext(), PopularUsersActivity.class);
        startActivity(popularUsersIntent);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void logout() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getApplicationContext());
        }
        builder
                .setMessage("Are you sure you want to Log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with logout
                        signOut();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            mUserRef.child("device_token").setValue(null);
                            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
                            FirebaseAuth.getInstance().signOut();
                            sendToStart();
                        }


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Ping Me");
            String sAux = "\nCheck out new app  PingMe\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.harish.hk185080.chatterbox\n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openUserPage() {
        Intent settingsIntent = new Intent(getApplicationContext(), MaterialSettingsActivity.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Header.findViewById(R.id.CimageView), "profileImage");
        startActivity(settingsIntent, optionsCompat.toBundle());
    }

    private void sendToChat(String userId, String userName) {
        Intent chatIntent = new Intent(getApplicationContext(), ChatOpenActivity.class);
        chatIntent.putExtra("user_id", userId);
        chatIntent.putExtra("user_name", userName);
        startActivity(chatIntent);
    }

    private void sendToProfile(String userId) {
        Intent profileIntent = new Intent(getApplicationContext(), MaterialProfileActivity.class);
        profileIntent.putExtra("user_id", userId);
        startActivity(profileIntent);
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
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

}
