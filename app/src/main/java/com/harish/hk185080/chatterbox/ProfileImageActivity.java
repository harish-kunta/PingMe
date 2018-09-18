package com.harish.hk185080.chatterbox;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.harish.hk185080.chatterbox.data.MyData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class ProfileImageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    public ImageView profileImage;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mUserDatabase;
    String image;
    String display_name;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private RelativeLayout rootLayout;
    private MyData myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        rootLayout=findViewById(R.id.rootlayout);
        final RelativeLayout loading=findViewById(R.id.loadingPanel);
        myData=new MyData();
        final String user_id = getIntent().getStringExtra("user_id");

        profileImage=findViewById(R.id.profile_image);
        mToolbar = findViewById(R.id.profile_image_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                display_name= dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();

                if (!image.equals("default")) {

                    Glide
                            .with(getApplicationContext())
                            .load(image)
                            .into(profileImage);
                    loading.setVisibility(View.GONE);
                }
                else
                {   loading.setVisibility(View.GONE);
                    profileImage.setImageDrawable(ContextCompat.getDrawable(ProfileImageActivity.this,R.drawable.ic_account_circle_white_48dp));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            case R.id.download_image:
                if(myData.isInternetConnected(ProfileImageActivity.this)) {
                    requestPermission();
                }
                else
                {
                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            downloadFile(image);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile_image_menu, menu);
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile(image);
        }
    }
    public void downloadFile(String image) {
        try {


            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/ChatterBox");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(image);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/ChatterBoxFiles", display_name + ".jpg");

            mgr.enqueue(request);

            // Open Download Manager to view File progress
            Snackbar.make(rootLayout,"Downloading...",Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Snackbar.make(rootLayout,"Error in downloading Image",Snackbar.LENGTH_LONG).show();
        }
    }
}
