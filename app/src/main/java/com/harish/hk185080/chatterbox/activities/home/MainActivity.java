package com.harish.hk185080.chatterbox.activities.home;

import static com.harish.hk185080.chatterbox.data.Constants.CONNECTIVITY_ACTION;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.AboutActivity;
import com.harish.hk185080.chatterbox.ChatOpenActivity;
import com.harish.hk185080.chatterbox.ChatsFragment;
import com.harish.hk185080.chatterbox.EditProfileActivity;
import com.harish.hk185080.chatterbox.FavouritesFragment;
import com.harish.hk185080.chatterbox.FriendsFragment;
import com.harish.hk185080.chatterbox.MaterialProfileActivity;
import com.harish.hk185080.chatterbox.MaterialSettingsActivity;
import com.harish.hk185080.chatterbox.NewSettingsActivty;
import com.harish.hk185080.chatterbox.PopularUsersActivity;
import com.harish.hk185080.chatterbox.PrivacyPolicyActivity;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.Request_Activity;
import com.harish.hk185080.chatterbox.Services.NetworkChangeReceiver;
import com.harish.hk185080.chatterbox.UsersActivity;
import com.harish.hk185080.chatterbox.activities.login.LoginActivity;
import com.harish.hk185080.chatterbox.data.MyData;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterHelper {

    private static final String TAG = "RegisterHelper";
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserRef;

    public RegisterHelper() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void updateUserStatus(Context context) {
        if (mCurrentUser != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            String deviceToken = FirebaseInstanceId.getInstance().getToken();
            if (!TextUtils.isEmpty(deviceToken)) {
                mUserRef.child("device_token").setValue(deviceToken);
            }
        }
    }

    public void signOut(Context context) {
        mAuth.signOut();
        if (mCurrentUser != null) {
            mUserRef.child("device_token").setValue(null);
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
        Intent startIntent = new Intent(context, LoginActivity.class);
        context.startActivity(startIntent);
        // Ensure MainActivity is finished to prevent going back to it on back press
        ((MainActivity) context).finish();
    }

    public void sendFeedback(Context context) {
        String body = null;
        int code;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "." + code + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error getting package info: " + e.getMessage());
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"harishtanu007@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from Ping Me app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    public void updateDeviceToken() {
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(deviceToken) && mUserRef != null) {
            mUserRef.child("device_token").setValue(deviceToken);
        }
    }

    public void updateUserUI(final Context context, final FirebaseUser currentUser, final NavigationView navigationView,
                             final TextView textView, final TextView textView2, final CircleImageView circleImageView) {
        if (currentUser != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
            mUserRef.keepSynced(true);
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Update UI here based on dataSnapshot
                    // Example: update navigation drawer header, user name, image, etc.
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read user data.", databaseError.toException());
                }
            });
        }
    }
}
