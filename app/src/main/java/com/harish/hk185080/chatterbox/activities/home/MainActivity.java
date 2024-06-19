package com.harish.hk185080.chatterbox.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.login.LoginActivity;
import com.harish.hk185080.chatterbox.activities.register.RegisterWithDetailsActivity;
import com.harish.hk185080.chatterbox.cache.UserCache;
import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.databinding.ActivityMainBinding;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.interfaces.IUserDetailsCallback;
import com.harish.hk185080.chatterbox.model.User;

public class MainActivity extends FragmentActivity {
    private static String TAG = "MainActivity";
    IDataSource dataSource;
    private ActivityMainBinding binding;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rootLayout = binding.rootLayout;
        BottomNavigationView navView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_chats, R.id.navigation_settings).build();
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //navController.navigate(ProfileFragmentDirections.actionGlobalHomeFragment(currentUser));
        NavigationUI.setupWithNavController(binding.navView, navController);

        dataSource = DataSourceHelper.getDataSource();
        dataSource.getCurrentUserDetails(new IUserDetailsCallback() {
            @Override
            public void onUserDetailsFetched(User userDetails) {
                if (userDetails.getUserID() == null || userDetails.getUserID().isEmpty() || userDetails.getFullName() == null || userDetails.getFullName().isEmpty() || userDetails.getEmail() == null || userDetails.getEmail().isEmpty()) {
                    // add a log statement
                    Log.w(TAG, "User details doesn't exist in the database, redirecting to RegisterWithDetailsActivity");
                    openSaveDetailsActivity();
                }

                UserCache.getInstance().cacheUser(userDetails);
            }

            @Override
            public void onUserDetailsFetchFailed(String errorMessage) {
                Log.e(TAG, "Error fetching user details: " + errorMessage);
                Log.e(TAG, "User details not found, redirecting to LoginActivity");
                Snackbar.make(rootLayout, errorMessage, Snackbar.LENGTH_LONG).show();
                logoutUser();
            }
        });
    }

    private void openSaveDetailsActivity() {
        Intent intent = new Intent(getApplicationContext(), RegisterWithDetailsActivity.class);
        startActivity(intent);
        finish();
    }

    private void logoutUser() {
        dataSource.logoutUser(new IDataSourceCallback() {
            @Override
            public void onSuccess() {
                sendToStart();
            }

            @Override
            public void onFailure(String errorMessage) {
                sendToStart();
            }
        });
    }

    private void sendToStart() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
