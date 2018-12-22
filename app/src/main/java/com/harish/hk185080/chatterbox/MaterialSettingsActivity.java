package com.harish.hk185080.chatterbox;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.harish.hk185080.chatterbox.data.MyData;
import com.harish.hk185080.chatterbox.utils.PhotoFullPopupWindow;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MaterialSettingsActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private ImageView mDisplayImage;
    private TextView mStatus;
    private TextView mEmail;
    private TextView mMobileTextView;
    private Button mLogout;
    private LinearLayout statusLayout;
    LinearLayout mEmailLayout, mMobileLayout;
    private MyData myData;
    private int GALLERY_PICK = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    static final int REQUEST_TAKE_PHOTO = 6;
    private String mCurrentPhotoPath;
    FabSpeedDial fabSpeedDial;
    String image;

    private Button mChangeStatus;
    private CoordinatorLayout rootLayout;
    RelativeLayout loading;
    // private Button mImageButton;
    // private Button mChangeName;
    // private Toolbar mToolBar;
    CollapsingToolbarLayout ctl;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private DatabaseReference mRootRef;
    String email, mobile;


    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;
    GoogleSignInClient mGoogleSignInClient;
    AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_material_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myData = new MyData();
        ctl = findViewById(R.id.toolbar_layout);
        fabSpeedDial = findViewById(R.id.fabSpeedDial);


        appBarLayout = findViewById(R.id.app_bar);

        rootLayout = findViewById(R.id.rootlayout);
        mMobileTextView=findViewById(R.id.settings_user_mobile);
        mDisplayImage = findViewById(R.id.settings_display_image);
        //mName = findViewById(R.id.settings_user_name);
        mStatus = findViewById(R.id.settings_status);
        mEmail = findViewById(R.id.settings_user_email);
        mLogout = findViewById(R.id.settings_user_log_out);
        mEmailLayout = findViewById(R.id.settings_email_layout);
        mMobileLayout = findViewById(R.id.settings_mobile_layout);

        statusLayout = findViewById(R.id.settings_status_layout);
        loading = findViewById(R.id.loadingPanel);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        startListening();


        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_status = mStatus.getText().toString();
                Intent changeStatusIntent = new Intent(getApplicationContext(), StatusActivity.class);
                changeStatusIntent.putExtra("status_value", current_status);
                startActivity(changeStatusIntent);
            }
        });


        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                switch (menuItem.getItemId()) {
                    case R.id.change_status:
                        String current_status = mStatus.getText().toString();
                        Intent changeStatusIntent = new Intent(getApplicationContext(), StatusActivity.class);
                        changeStatusIntent.putExtra("status_value", current_status);
                        startActivity(changeStatusIntent);
                        break;
                    case R.id.change_name:
                        String current_name = ctl.getTitle().toString();
                        Intent changeNameIntent = new Intent(MaterialSettingsActivity.this, AccountNameActivity.class);
                        changeNameIntent.putExtra("account_name_value", current_name);
                        startActivity(changeNameIntent);
                        break;
                    case R.id.choose_image:
                        if (myData.isInternetConnected(MaterialSettingsActivity.this)) {
                            chooseImage();
                        } else {
                            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.take_photo:
                        takeHighQualityPhoto();
                        break;
                    case R.id.remove_image:
                        if (myData.isInternetConnected(MaterialSettingsActivity.this)) {
                            removeImage();
                        } else {
                            Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                        }

                        break;

                }
                return true;
            }
        });


    }


    private void logout() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MaterialSettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MaterialSettingsActivity.this);
        }
        builder
                .setMessage("Are you sure you want to Log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with logout
                        signOut();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
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

//    private void removeImage() {
//        AlertDialog.Builder builder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Dialog_Alert);
//        } else {
//            builder = new AlertDialog.Builder(getApplicationContext());
//        }
//        builder
//                .setMessage("Are you sure you want to Remove Image?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with logout
//                        mProgressDialog = new ProgressDialog(getApplicationContext());
//                        mProgressDialog.setTitle("Removing Image....");
//                        mProgressDialog.setMessage("Please wait while we Remove your Image...");
//                        mProgressDialog.setCanceledOnTouchOutside(false);
//                        mProgressDialog.show();
//                        Map profile_image_delete_hashmap = new HashMap();
//                        profile_image_delete_hashmap.put("image", "default");
//                        profile_image_delete_hashmap.put("thumb_image", "default");
//
//                        mUserDatabase.updateChildren(profile_image_delete_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    mProgressDialog.dismiss();
//                                    mDisplayImage.setImageResource(R.drawable.ic_account_circle_white_48dp);
//                                }
//                            }
//                        });
//
//
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//
//
//    }


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

        }
    }

    private void startListening() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        String current_uid = mCurrentUser.getUid();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data...");
        mProgressDialog.setMessage("please wait while we load user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                if (dataSnapshot.hasChild("email")) {
                    email = dataSnapshot.child("email").getValue().toString();
                    mEmailLayout.setVisibility(View.VISIBLE);
                } else {
                    mEmailLayout.setVisibility(View.GONE);
                }
                if (dataSnapshot.hasChild("mobile")) {
                    mMobileLayout.setVisibility(View.VISIBLE);
                    mobile = dataSnapshot.child("mobile").getValue().toString();
                    mMobileTextView.setText(mobile);
                } else {
                    mMobileLayout.setVisibility(View.GONE);
                }

                //mName.setText(name);
                ctl.setTitle(name);
                mStatus.setText(status);
                mEmail.setText(email);

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
                            .into(mDisplayImage);
                    loading.setVisibility(View.GONE);
                } else {
                    loading.setVisibility(View.GONE);
                    mDisplayImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_account_circle_white_48dp));

                }
                mProgressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!image.equals("default")) {
                    new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, v, image, null);
                }
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

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

    private void sendToStart() {

        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.choose_image:
                if (myData.isInternetConnected(MaterialSettingsActivity.this)) {
                    chooseImage();
                } else {
                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                }
                return true;
            case R.id.take_photo:
                takeHighQualityPhoto();
                return true;
            case R.id.remove_image:
                if (myData.isInternetConnected(MaterialSettingsActivity.this)) {
                    removeImage();
                } else {
                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.change_image_menu, menu);
//        return true;
//    }


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

                    mProgressDialog = new ProgressDialog(MaterialSettingsActivity.this);
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
                                                                                .into(mDisplayImage);
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
            builder = new AlertDialog.Builder(MaterialSettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MaterialSettingsActivity.this);
        }
        builder
                .setMessage("Are you sure you want to Remove Image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with logout
                        mProgressDialog = new ProgressDialog(MaterialSettingsActivity.this);
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
                                    mDisplayImage.setImageResource(R.drawable.ic_account_circle_white_48dp);
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
}
