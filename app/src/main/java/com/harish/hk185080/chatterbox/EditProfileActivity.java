package com.harish.hk185080.chatterbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    Button saveChanges;
    private FirebaseUser mCurrentUser;
    ImageButton addButton;
    LinearLayout rootLayout;
    private MyData myData;
    RelativeLayout loading;
    String mobile;

    EditText _statusText;
    EditText _nameText;
    EditText _mobileNumber;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private ProgressDialog mProgressDialog;
    private StorageReference mImageStorage;
    private int GALLERY_PICK = 1;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mRegProgress;
    private Toolbar mToolbar;
    String image;
    String name,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        myData = new MyData();
        rootLayout = findViewById(R.id.rootlayout);
        profileImageView = findViewById(R.id.profile_image);
        addButton = findViewById(R.id.add_image);
        saveChanges = findViewById(R.id.save_changes);
        _statusText = findViewById(R.id.status);
        _nameText = findViewById(R.id.profile_name);
        _mobileNumber=findViewById(R.id.mobile_number);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        loading = findViewById(R.id.loadingPanel);

        if (!myData.isInternetConnected(EditProfileActivity.this)) {
            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
        }

        mAuth = FirebaseAuth.getInstance();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mToolbar = findViewById(R.id.edit_profile_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Profile");


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
//        saveChanges.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveChanges();
//            }
//        });

        mRegProgress = new ProgressDialog(EditProfileActivity.this,
                R.style.AppThemeDialog);
        mRegProgress.setIndeterminate(true);
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.setMessage("Loading User Data...");
        mRegProgress.show();
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object nameobj,statusobj,imageobj,mobileobj;

                nameobj=dataSnapshot.child("name").getValue();
                mobileobj= dataSnapshot.child("mobile").getValue();
                statusobj=dataSnapshot.child("status").getValue();
                if(nameobj!=null)
                {
                    name = nameobj.toString();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                if(statusobj!=null)
                {
                    status = statusobj.toString();
                }

                if (dataSnapshot.hasChild("image")) {
                    image = dataSnapshot.child("image").getValue().toString();
                }
                else
                {
                    image="default";
                }





                    if (dataSnapshot.hasChild("mobile")) {
                        mobile = dataSnapshot.child("mobile").getValue().toString();
                    }
                    //String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                    //mName.setText(name);
                    _nameText.setText(name);
                    _statusText.setText(status);
                    _mobileNumber.setText(mobile);

                    if (!image.equals("default")) {
                        loading.setVisibility(View.VISIBLE);
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.ic_account_circle_white_48dp)
                                .error(R.drawable.ic_account_circle_white_48dp)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .priority(Priority.HIGH)
                                .dontAnimate()
                                .dontTransform();
                        Glide
                                .with(getApplicationContext())
                                .load(image)
                                .apply(options)
                                .into(profileImageView);
                        loading.setVisibility(View.GONE);
                    } else {
                        loading.setVisibility(View.GONE);
                        profileImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_account_circle_white_48dp));

                    }
                    mRegProgress.dismiss();
                }

                @Override
                public void onCancelled (DatabaseError databaseError){

                }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void saveChanges() {

        mRegProgress.show();

        String status = _statusText.getText().toString();
        String name = _nameText.getText().toString();
        String mobile=_mobileNumber.getText().toString();
        if(name.isEmpty())
        {
            Snackbar.make(rootLayout,"Name field cannot be empty!",Snackbar.LENGTH_SHORT).show();
            mRegProgress.dismiss();
        }
        else if(status.isEmpty())
        {
            Snackbar.make(rootLayout,"Status field cannot be empty!",Snackbar.LENGTH_SHORT).show();
            mRegProgress.dismiss();
        }
        else {
            uploadName(name, status, mobile,image);
        }



    }

    private void uploadStatus(String status, final String mobile,String image) {
        mUserDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uploadMobile(mobile,image);
                } else {
                    Snackbar.make(rootLayout, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadMobile(String mobile,String image) {
        mUserDatabase.child("mobile").setValue(mobile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uploadImage(image);

                } else {
                    Snackbar.make(rootLayout, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void uploadImage(String image) {
        mUserDatabase.child("image").setValue(image).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mUserDatabase.child("thumb_image").setValue(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mRegProgress.dismiss();
                                finish();
                            } else {
                                Snackbar.make(rootLayout, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Snackbar.make(rootLayout, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void uploadName(String name, final String status, final String mobile,String image) {
        mUserDatabase.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uploadStatus(status,mobile,image);
                } else {
                    Snackbar.make(rootLayout, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToProfile() {
        //FirebaseAuth.getInstance().signOut();
        Intent startIntent = new Intent(EditProfileActivity.this, MaterialSettingsActivity.class);
        startActivity(startIntent);
        finish();
    }

    private void addImage() {
        if (myData.isInternetConnected(EditProfileActivity.this)) {
            chooseImage();
        } else {
            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_button:
                saveChanges();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);

        }
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    mProgressDialog = new ProgressDialog(EditProfileActivity.this);
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
                                                                                .into(profileImageView);
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
    public void onBackPressed() {

    }
}
