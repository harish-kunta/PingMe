package com.harish.hk185080.chatterbox;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.harish.hk185080.chatterbox.activities.login.StartActivity;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private TextView mEmail;
    private TextView mLogout;
    private ImageButton mSettingsBack;
    private Button mChangeStatus;
    private ScrollView rootLayout;
    // private Button mImageButton;
    // private Button mChangeName;
    // private Toolbar mToolBar;
    private int GALLERY_PICK = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private DatabaseReference mRootRef;
    String email;


    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;
    GoogleSignInClient mGoogleSignInClient;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rootLayout=findViewById(R.id.container);
        mDisplayImage = findViewById(R.id.settings_display_image);
        mName = findViewById(R.id.settings_user_name);
        mStatus = findViewById(R.id.settings_status);
        mEmail = findViewById(R.id.settings_user_email);
        mLogout=findViewById(R.id.settings_user_log_out);
        mSettingsBack=findViewById(R.id.settings_back_button);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_name = mName.getText().toString();
                Intent changeNameIntent = new Intent(SettingsActivity.this, AccountNameActivity.class);
                changeNameIntent.putExtra("account_name_value", current_name);
                startActivity(changeNameIntent);
            }
        });
        mStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_status = mStatus.getText().toString();
                Intent changeStatusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                changeStatusIntent.putExtra("status_value", current_status);
                startActivity(changeStatusIntent);
            }
        });
        mDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent changeImageIntent = new Intent(SettingsActivity.this, SettingsImageActivity.class);

                startActivity(changeImageIntent);
                // finish();


            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        mSettingsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // v.startAnimation(AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.image_click));

                finish();
            }
        });
        startListening();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_remove_image) {
            removeImage();
        }
        return true;
    }


    private void logout() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SettingsActivity.this);
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

    private void removeImage() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SettingsActivity.this);
        }
        builder
                .setMessage("Are you sure you want to Remove Image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with logout
                        mProgressDialog = new ProgressDialog(SettingsActivity.this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
//            Uri imageUri = data.getData();
//
//
//            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
//
//        }
//        try {
//
//
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                if (resultCode == RESULT_OK) {
//
//                    mProgressDialog = new ProgressDialog(SettingsActivity.this);
//                    mProgressDialog.setTitle("Uploading Image....");
//                    mProgressDialog.setMessage("Please wait while we upload and process the image");
//                    mProgressDialog.setCanceledOnTouchOutside(false);
//                    mProgressDialog.show();
//                    Uri resultUri = result.getUri();
//                    File thumb_filepath = new File(resultUri.getPath());
//
//                    String current_user_id = mCurrentUser.getUid();
//
//
//                    Bitmap thumb_bitmap = new Compressor(this)
//                            .setMaxWidth(200)
//                            .setMaxHeight(200)
//                            .setQuality(75)
//                            .compressToBitmap(thumb_filepath);
//
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                    final byte[] thumb_byte = byteArrayOutputStream.toByteArray();
//
//
//                    final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
//                    final StorageReference thumb_filepath_image = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");
//
//                    filepath.putFile(resultUri)
//                            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                                @Override
//                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                    if (!task.isSuccessful()) {
//                                        throw task.getException();
//                                    }
//
//                                    // Continue with the task to get the download URL
//                                    return filepath.getDownloadUrl();
//                                }
//                            })
//                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull final Task<Uri> task) {
//                                    if (task.isSuccessful()) {
//                                        Uri downloadUri = task.getResult();
//                                        //final String download_url = task.getResult().getDownloadUrl().toString();
//                                        final String download_url = downloadUri.toString();
//                                        UploadTask uploadTask = thumb_filepath_image.putBytes(thumb_byte);
//                                        uploadTask
//                                                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                                                    @Override
//                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                                        if (!task.isSuccessful()) {
//                                                            throw task.getException();
//                                                        }
//
//                                                        // Continue with the task to get the download URL
//                                                        return thumb_filepath_image.getDownloadUrl();
//                                                    }
//                                                })
//                                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Uri> innertask) {
//                                                        Uri thumbdownloadUri = innertask.getResult();
//                                                        String thumb_downloadUrl = thumbdownloadUri.toString();
//                                                        //String thumb_downloadUrl=thumb_task.getResult().getUploadSessionUri().toString();
//                                                        if (task.isSuccessful()) {
//                                                            Map update_hashmap = new HashMap();
//                                                            update_hashmap.put("image", download_url);
//                                                            update_hashmap.put("thumb_image", thumb_downloadUrl);
//
//                                                            mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                    if (task.isSuccessful()) {
//                                                                        mProgressDialog.dismiss();
//                                                                    }
//                                                                }
//                                                            });
//                                                        } else {
//                                                            mProgressDialog.hide();
//                                                            Snackbar.make(rootLayout,"error in uploading thumbnail..",Snackbar.LENGTH_LONG).show();
//                                                        }
//                                                    }
//                                                });
//
//                                    } else {
//                                        Snackbar.make(rootLayout,"error in uploading image....",Snackbar.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    Exception error = result.getError();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onActivityResult(requestCode, resultCode, data);
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
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                if (dataSnapshot.hasChild("email")) {
                    email = dataSnapshot.child("email").getValue().toString();
                    mEmail.setVisibility(View.VISIBLE);
                } else {
                    mEmail.setVisibility(View.GONE);
                }

                mName.setText(name);
                mStatus.setText(status);
                mEmail.setText(email);

                if (!image.equals("default")) {
                    //Picasso.get().load(image).placeholder(R.drawable.ic_user_image).into(mDisplayImage);

//                    Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_white_48dp).into(mDisplayImage, new Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_white_48dp).into(mDisplayImage);
//                        }
//                    });
                    Glide
                            .with(getApplicationContext())
                            .load(image)
                            .into(mDisplayImage);
                }
                else
                {
                   // Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_account_circle_white_48dp).into(mDisplayImage);
                    mDisplayImage.setImageDrawable(ContextCompat.getDrawable(SettingsActivity.this,R.drawable.ic_account_circle_white_48dp));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

        Intent startIntent = new Intent(SettingsActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
