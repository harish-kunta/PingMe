package com.harish.hk185080.chatterbox;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.Services.NetworkChangeReceiver;
import com.harish.hk185080.chatterbox.activities.login.StartActivity;
import com.harish.hk185080.chatterbox.data.MyData;
import com.harish.hk185080.chatterbox.utils.RevealAnimation;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static com.harish.hk185080.chatterbox.data.Constants.CONNECTIVITY_ACTION;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private FirebaseAuth mAuth;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    public static CoordinatorLayout rootLayout;
    public static View Header;
    public static AppBarLayout appBarLayout;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    private MyData myData;
    private static final String TAG = "MainActivity";
    String CHANNEL_ID = "MESSAGES";
    boolean nullvalue=false;


    TabLayout tabLayout;
    ViewPager mpager;
    Toolbar toolbar;
    FirebaseUser currentUser;
    Menu menu;
    ImageView notfound;


    private DatabaseReference mUserRef;

    GoogleSignInClient mGoogleSignInClient;
    NavigationView navigationView;
    CircleImageView circleImageView;
    public final static String AUTH_KEY_FCM = "AIzaSyA0FB_ByKW7-UIGhLzpE4E0NpROWccjwbs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    public String deviceToken;

    public void init() {

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


        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);

        }
        rootLayout = findViewById(R.id.rootlayout);
        toolbar = findViewById(R.id.toolbar);
        // toolbar.setTitleTextColor(this.getResources().getColor(R.color.invertcolor));
        setTitle("Chats");
        tabLayout = findViewById(R.id.mainTabLayout);


        mpager = findViewById(R.id.viewpagermain);

        if (mpager != null && tabLayout != null) {

            mpager.setOffscreenPageLimit(2);

            mpager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

                @Override
                public Fragment getItem(int position) {
                    switch (position % 3) {
                        case 0:

                            ChatsFragment chatsFragment = new ChatsFragment();
                            return chatsFragment;

                        case 1:

                            FavouritesFragment favouritesFragment = new FavouritesFragment();
                            return favouritesFragment;

                        case 2:

                            FriendsFragment friendsFragment = new FriendsFragment();
                            return friendsFragment;

                        default:
                            return null;
                    }
                }

                @Override
                public int getCount() {
                    return 3;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position % 3) {
                        case 0:
                            return "";
                        case 1:
                            return "";
                        case 2:
                            return "";


                    }
                    return "";
                }
            });
            mpager
                    .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                        @Override
                        public void onPageSelected(int position) {
                            // TODO Auto-generated method stub
                            switch (position) {
                                case 0:

                                    setTitle("Chats");
                                    break;
                                case 1:

                                    setTitle("Favourites");
                                    break;
                                case 2:

                                    setTitle("Friends");
                                    break;
                                default:
                                    return;
                            }

                        }

                        @Override
                        public void onPageScrolled(int arg0, float arg1, int arg2) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onPageScrollStateChanged(int pos) {
                            // TODO Auto-generated method stub

                        }
                    });
            tabLayout.setupWithViewPager(mpager);

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_chat_white_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite_border_white_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_people_outline_white_24dp);


            tabLayout.setOnTabSelectedListener(
                    new TabLayout.ViewPagerOnTabSelectedListener(mpager) {
                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            super.onTabSelected(tab);
                            RecyclerView recyclerView;
                            int numTab = tab.getPosition();
                            //  if (fab != null)
                            //    animateIn(fab);

                            switch (numTab) {
                                case 0:

                                    recyclerView = findViewById(R.id.conv_list);
                                    setTitle("Chats");
                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }

                                    break;
                                case 1:
                                    setTitle("Favourites");
                                    recyclerView = findViewById(R.id.friends_list);

                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }
                                    break;
                                case 2:
                                    setTitle("Friends");
                                    recyclerView = findViewById(R.id.conv_list);

                                    if (recyclerView != null) {
                                        recyclerView.smoothScrollToPosition(0);
                                    }
                                    break;

                            }
                            if (appBarLayout != null)
                                appBarLayout.setExpanded(true, true);

                        }

                    });


        }


        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Move to Profile Activity
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
            appBarLayout = findViewById(R.id.mainappbar);
            //notfound = (ImageView) findViewById(R.id.notfound);
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // startRevealActivity(view);
                    startActivity(new Intent(MainActivity.this, UsersActivity.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                    // startActivity(new Intent(MainActivity.this, ChatMain.class));
                }
            });
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {

                init();
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
                        Object nameobj, imageobj, statusobj, thumb_imageobj;


                        try {
                            nameobj = dataSnapshot.child("name").getValue();
                            imageobj = dataSnapshot.child("image").getValue();
                            statusobj = dataSnapshot.child("status").getValue();
                            thumb_imageobj = dataSnapshot.child("thumb_image").getValue();
                            if (nameobj == null) {
                                   Log.e("Harishtest","call");
                                    openEditPage();
                            }
                            else if(imageobj == null)
                            {

                                openEditPage();

                            }
                            else if(statusobj == null)
                            {

                                openEditPage();
                            }
                            else if(thumb_imageobj == null)
                            {

                                openEditPage();

                            }
                            else {

                                String name = nameobj.toString();


                                final String image = imageobj.toString();
                                String status = statusobj.toString();
                                String thumb_image = thumb_imageobj.toString();

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
//You must remember to remove the listener when you finish using it, also to keep track of changes you can use the ChildChange
//                        myRef.addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                                //textView2.setText(getString(R.string.friends, dataSnapshot.getChildrenCount()));
//                                textView2.setText(dataSnapshot.getChildrenCount()+"");
//                            }
//
//                            @Override
//                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });

                                if (!image.equals("default")) {
                                    Glide
                                            .with(getApplicationContext())
                                            .load(image)
                                            .into(circleImageView);

                                } else {
                                    circleImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_account_circle_white_48dp));
                                }
                            }

                        }
                        catch (Exception e)
                        {
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

    private void openEditPage() {
        Intent uploadIntent = new Intent(MainActivity.this, EditProfileActivity.class);
        uploadIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(uploadIntent);
    }

    private void sendToChat(String userId, String userName) {
        Intent chatIntent = new Intent(MainActivity.this, ChatOpenActivity.class);
        chatIntent.putExtra("user_id", userId);
        chatIntent.putExtra("user_name", userName);
        startActivity(chatIntent);
    }

    private void sendToProfile(String userId) {
        Intent profileIntent = new Intent(MainActivity.this, MaterialProfileActivity.class);
        profileIntent.putExtra("user_id", userId);
        startActivity(profileIntent);
    }

    private void openUserPage() {
        Intent settingsIntent = new Intent(MainActivity.this, MaterialSettingsActivity.class);
        ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,Header.findViewById(R.id.CimageView),"profileImage");
        startActivity(settingsIntent,optionsCompat.toBundle());
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
            addToken();
        }
    }

    private void addToken() {
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        // Log and toast
//                        String msg = token;
//                        Log.d(TAG, msg);
//                        mUserRef.child("device_token").setValue(msg);
//                    }
//                });
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

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }

    public static void sendFeedback(Context context) {
        String body = null;
        int code;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body+"."+ code+ "\n Device Brand: " + Build.BRAND +
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

    private void feedback() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }


        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.feedback_dialog, (ViewGroup) findViewById(android.R.id.content), false);
// Set up the input
        final EditText input = viewInflated.findViewById(R.id.feedback_title);
        final EditText messageInput = viewInflated.findViewById(R.id.feedback_description);

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(viewInflated)
                .setTitle("Please provide your valuable feedback here")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        //Dismiss once everything is OK.
                        if (myData.isInternetConnected(MainActivity.this)) {
                            final String inputString = input.getText().toString();
                            final String messageInputString = messageInput.getText().toString();
                            if (!TextUtils.isEmpty(inputString) && !TextUtils.isEmpty(messageInputString)) {
                                dialog.dismiss();
                                final String feedbackTitle = input.getText().toString().trim();
                                String feedbackDescription = messageInput.getText().toString().trim();
                                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                emailIntent.setType("text/plain");
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"harishtanu007@gmail.com"});
                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, feedbackTitle);
                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, feedbackDescription);


                                emailIntent.setType("message/rfc822");

                                try {
                                    startActivity(Intent.createChooser(emailIntent,
                                            "Send email using..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Snackbar.make(rootLayout, "No email clients installed.", Snackbar.LENGTH_LONG).show();
                                }
                            } else if (TextUtils.isEmpty(inputString)) {
                                Snackbar.make(rootLayout, "Title cannot be Empty", Snackbar.LENGTH_LONG).show();
                            } else if (TextUtils.isEmpty(messageInputString)) {
                                Snackbar.make(rootLayout, "Description cannot be Empty", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });
        dialog.show();

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.myaccount) {
            openUserPage();
//            if (Profile.getCurrentProfile() != null) {
//                Intent intent = new Intent(this, User.class);
//                intent.putExtra("id", Profile.getCurrentProfile().getId());
//                startActivity(intent);
//            } else {
//
//                if (MainActivity.springFloatingActionMenu != null) {
//                    MainActivity.springFloatingActionMenu.setVisibility(View.INVISIBLE);
//                }
//                Snackbar.make(MainActivity.rootLayout, "Please Login", Snackbar.LENGTH_LONG)
//                        .setAction("Login", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(MainActivity.this, Login.class));
//                            }
//                        }).show();
//            }
            return false;
        } else if (id == R.id.share) {
            shareApp();
            return false;
//
//            String text = "Check out new app Questo : \n " + "https://play.google.com/store/apps/details?id=com.tdevelopers.questo";
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
//            BottomSheet sheet = BottomSheetHelper.shareAction(this, shareIntent).title("Share App").build();
//            sheet.show();


//        } else if (id == R.id.invite) {
//
////            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
////                    .setMessage("Questo App Invite")
////                    .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
////                    .setCallToActionText(getString(R.string.invitation_cta))
////                    .build();
////
////
////            startActivityForResult(intent, REQUEST_INVITE);
//            return false;
//        }
        } else if (id == R.id.aboutus) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return false;
        } else if (id == R.id.app_settings) {
            startActivity(new Intent(MainActivity.this, NewSettingsActivty.class));
            return false;
        }
        else if (id == R.id.privacy_policy) {
            startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
            return false;
        }
        //else if (id == R.id.favourites) {
//            if (Profile.getCurrentProfile() != null)
//                startActivity(new Intent(MainActivity.this, Favorite_Activity.class));
//            else {
//                if (MainActivity.springFloatingActionMenu != null) {
//                    MainActivity.springFloatingActionMenu.setVisibility(View.INVISIBLE);
//                }
//                Snackbar.make(MainActivity.rootLayout, "Please Login", Snackbar.LENGTH_LONG)
//                        .setAction("Login", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(MainActivity.this, Login.class));
//                            }
//                        }).show();
//            }

        //}
        else if (id == R.id.logout_button) {
            logout();
            return false;
        } else if (id == R.id.allUsers) {
            //openAllUsers();
            openPopularUsers();
            return false;
//            if (Profile.getCurrentProfile() != null) {
//                startActivity(new Intent(MainActivity.this, ChatMain.class));
//            } else {
//
//                if (MainActivity.springFloatingActionMenu != null) {
//                    MainActivity.springFloatingActionMenu.setVisibility(View.INVISIBLE);
//                }
//                Snackbar.make(MainActivity.rootLayout, "Please Login", Snackbar.LENGTH_LONG)
//                        .setAction("Login", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(MainActivity.this, Login.class));
//                            }
//                        }).show();
//            }
        } else if (id == R.id.myRequests) {
            Intent i = new Intent(MainActivity.this, Request_Activity.class);
            startActivity(i);
            return false;

        }
//        else if (id == R.id.how) {
//            //startActivity(new Intent(MainActivity.this, Introduction.class));
//            return false;
//        }
        else if (id == R.id.feedback) {

            sendFeedback(getApplicationContext());

            //startActivity(new Intent(MainActivity.this, Introduction.class));
            return false;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return false;
    }


    private void logout() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
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

    private void openAllUsers() {
        Intent allUsersIntent = new Intent(MainActivity.this, UsersActivity.class);
        startActivity(allUsersIntent);
    }

    private void openPopularUsers() {
        Intent popularUsersIntent = new Intent(MainActivity.this, PopularUsersActivity.class);
        startActivity(popularUsersIntent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void startRevealActivity(View v) {
        //calculates the center of the View v you are passing
        int revealX = (int) (v.getX() + v.getWidth() / 2);
        int revealY = (int) (v.getY() + v.getHeight() / 2);

        //create an intent, that launches the second activity and pass the x and y coordinates
        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        //just start the activity as an shared transition, but set the options bundle to null
        ActivityCompat.startActivity(this, intent, null);

        //to prevent strange behaviours override the pending transitions
        overridePendingTransition(0, 0);
    }


}
