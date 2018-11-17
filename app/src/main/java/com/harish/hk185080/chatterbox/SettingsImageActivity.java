package com.harish.hk185080.chatterbox;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;


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
            case R.id.choose_image:
                if (myData.isInternetConnected(SettingsImageActivity.this)) {
                    chooseImage();
                } else {
                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                }
                return true;
            case R.id.take_photo:
                takeHighQualityPhoto();
                return true;
            case R.id.remove_image:
                if (myData.isInternetConnected(SettingsImageActivity.this)) {
                    removeImage();
                } else {
                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                }

                return true;
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

        //img.setImageURI(imageUri);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //setPic();
            Uri imageUri = Uri.fromFile(new File(mCurrentPhotoPath));
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);

        }
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    mProgressDialog = new ProgressDialog(SettingsImageActivity.this);
                    mProgressDialog.setTitle("Uploading Image....");
                    mProgressDialog.setMessage("Please wait while we upload and process the image");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    Uri resultUri = result.getUri();
                    File thumb_filepath = new File(resultUri.getPath());

                    String current_user_id = mCurrentUser.getUid();


                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                    final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                    final StorageReference thumb_filepath_image = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                    filepath.putFile(resultUri)
                            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return filepath.getDownloadUrl();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull final Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        final Uri downloadUri = task.getResult();
                                        //final String download_url = task.getResult().getDownloadUrl().toString();
                                        final String download_url = downloadUri.toString();
                                        UploadTask uploadTask = thumb_filepath_image.putBytes(thumb_byte);
                                        uploadTask
                                                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            throw task.getException();
                                                        }

                                                        // Continue with the task to get the download URL
                                                        return thumb_filepath_image.getDownloadUrl();
                                                    }
                                                })
                                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> innertask) {
                                                        Uri thumbdownloadUri = innertask.getResult();
                                                        String thumb_downloadUrl = thumbdownloadUri.toString();
                                                        //String thumb_downloadUrl=thumb_task.getResult().getUploadSessionUri().toString();
                                                        if (task.isSuccessful()) {
                                                            Map update_hashmap = new HashMap();
                                                            update_hashmap.put("image", download_url);
                                                            update_hashmap.put("thumb_image", thumb_downloadUrl);

                                                            mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mProgressDialog.dismiss();
                                                                        loading.setVisibility(View.VISIBLE);
                                                                        Glide
                                                                                .with(getApplicationContext())
                                                                                .load(download_url)
                                                                                .into(profileImage);
                                                                        loading.setVisibility(View.GONE);
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            mProgressDialog.hide();
                                                            Snackbar.make(rootLayout, "error in uploading thumbnail..", Snackbar.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        Snackbar.make(rootLayout, "error in uploading....", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
