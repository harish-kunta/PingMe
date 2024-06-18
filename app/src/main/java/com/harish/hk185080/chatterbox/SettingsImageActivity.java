package com.harish.hk185080.chatterbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SettingsImageActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    public ImageView profileImage;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private int GALLERY_PICK = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private Uri mHighQualityImageUri = null;
    static final int REQUEST_TAKE_PHOTO = 6;
    private Bitmap mImageBitmap;


    private String mCurrentPhotoPath;


    private StorageReference mImageStorage;
    private RelativeLayout rootLayout;
    private MyData myData;
    RelativeLayout loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_image);

        rootLayout = findViewById(R.id.rootlayout);
        myData = new MyData();
        profileImage = findViewById(R.id.change_profile_image);
        mToolbar = findViewById(R.id.image_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        loading = findViewById(R.id.loadingPanel);
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String image = dataSnapshot.child("image").getValue().toString();


                if (!image.equals("default")) {
                    //Picasso.get().load(image).placeholder(R.drawable.ic_user_image).into(mDisplayImage);

                    loading.setVisibility(View.VISIBLE);
                    Glide
                            .with(getApplicationContext())
                            .load(image)
                            .into(profileImage);
                    loading.setVisibility(View.GONE);
                } else {
                    loading.setVisibility(View.GONE);
                    profileImage.setImageDrawable(ContextCompat.getDrawable(SettingsImageActivity.this, R.drawable.ic_account_circle_white_48dp));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
//            case R.id.choose_image:
//                if (myData.isInternetConnected(SettingsImageActivity.this)) {
//                    chooseImage();
//                } else {
//                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
//                }
//                return true;
//            case R.id.take_photo:
//                takeHighQualityPhoto();
//                return true;
//            case R.id.remove_image:
//                if (myData.isInternetConnected(SettingsImageActivity.this)) {
//                    removeImage();
//                } else {
//                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
//                }
//
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void takeHighQualityPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void chooseImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);


        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);
    }


    private void removeImage() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SettingsImageActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SettingsImageActivity.this);
        }
        builder
                .setMessage("Are you sure you want to Remove Image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with logout
                        mProgressDialog = new ProgressDialog(SettingsImageActivity.this);
                        mProgressDialog.setTitle("Removing Image....");
                        mProgressDialog.setMessage("Please wait while we Remove your Image...");
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();
                        Map profile_image_delete_hashmap = new HashMap();
                        profile_image_delete_hashmap.put("image", "default");
                        profile_image_delete_hashmap.put("thumb_image", "default");

                        mUserDatabase.updateChildren(profile_image_delete_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mProgressDialog.dismiss();
                                    profileImage.setImageResource(R.drawable.ic_account_circle_white_48dp);
                                }
                            }
                        });


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private void setPic() {
        // Get the dimensions of the View


        int targetW = profileImage.getWidth();
        int targetH = profileImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        profileImage.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.change_image_menu, menu);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
