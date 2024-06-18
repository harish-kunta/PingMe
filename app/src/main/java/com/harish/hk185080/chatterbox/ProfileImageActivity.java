package com.harish.hk185080.chatterbox;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.data.MyData;

public class ProfileImageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    public ImageView profileImage;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mUserDatabase, mFriendsDatabase;
    String image;
    String display_name;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private RelativeLayout rootLayout;
    private MyData myData;
    private Menu profileImageMenu;
    private boolean downloadState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        rootLayout = findViewById(R.id.rootlayout);
        final RelativeLayout loading = findViewById(R.id.loadingPanel);
        myData = new MyData();
        final String user_id = getIntent().getStringExtra("user_id");

        profileImage = findViewById(R.id.profile_image);
        mToolbar = findViewById(R.id.profile_image_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        downloadState=false;

        //downloadState = 0; // setting state
        //invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                display_name = dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();

                if (!image.equals("default")) {

                    Glide
                            .with(getApplicationContext())
                            .load(image)
                            .into(profileImage);
                    loading.setVisibility(View.GONE);
                } else {
                    loading.setVisibility(View.GONE);
                    profileImage.setImageDrawable(ContextCompat.getDrawable(ProfileImageActivity.this, R.drawable.ic_account_circle_white_48dp));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendsDatabase.keepSynced(true);
        mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {

                   // Toast.makeText(ProfileImageActivity.this, "Both are Friends", Toast.LENGTH_SHORT).show();
                    //downloadState = 1; // setting state
                    //profileImageMenu.getMenu().removeItem(R.id.download_image);
                    //invalidateOptionsMenu();
                    //getMenu().removeItem(R.id.item_name);
                   downloadState=true;
                   invalidateOptionsMenu();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
//            case R.id.download_image:
//                if (myData.isInternetConnected(ProfileImageActivity.this)) {
//                    requestPermission();
//                } else {
//                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
//                }
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            Log.d("ProfileImageActivity", "Permission");
        } else {
            downloadFile(image);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile_image_menu, menu);
        profileImageMenu = menu;
        if(!downloadState) {
            if (profileImageMenu != null) {
                profileImageMenu.findItem(R.id.download_image)
                        .setVisible(false);
                profileImageMenu.findItem(R.id.download_image)
                        .setEnabled(false);
            }
        }
        else
        {
            if (profileImageMenu != null) {
                profileImageMenu.findItem(R.id.download_image)
                        .setVisible(true);
                profileImageMenu.findItem(R.id.download_image)
                        .setEnabled(true);
            }
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile(image);
        }
    }

    public void downloadFile(String image) {
        try {


//            File direct = new File(Environment.getExternalStorageDirectory()
//                    + "/ChatterBox");
//
//            if (!direct.exists()) {
//                direct.mkdirs();
//            }

            DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(image);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle(display_name)
                    .setDescription("Ping Me Files")
                    .setDestinationInExternalPublicDir("/PingMeFiles", display_name + ".jpg");

            mgr.enqueue(request);

            // Open Download Manager to view File progress
            Snackbar.make(rootLayout, "Downloading...", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(rootLayout, "Error in downloading Image", Snackbar.LENGTH_LONG).show();
        }
    }
}
