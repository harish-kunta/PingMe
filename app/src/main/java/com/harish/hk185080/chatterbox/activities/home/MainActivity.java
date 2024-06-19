package com.harish.hk185080.chatterbox.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private IDataSource dataSource;
    private ActivityMainBinding binding;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Set the ActionBar
        setSupportActionBar(binding.toolbar);

        rootLayout = binding.rootLayout;
        BottomNavigationView bottomNav = binding.navView;

        // Set up NavController and AppBarConfiguration
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chats, R.id.navigation_profile).build();

        // Connect NavController to BottomNavigationView
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
                int id = item.getItemId();

                if (id == R.id.menu_chats) {
                    navController.navigate(R.id.navigation_chats);
                    return true;
                } else if (id == R.id.menu_settings) {
                    navController.navigate(R.id.navigation_profile);
                    return true;
                }
                return false;
            }
        });

        dataSource = DataSourceHelper.getDataSource();

        try {
            dataSource.getCurrentUserDetails(new IUserDetailsCallback() {
                @Override
                public void onUserDetailsFetched(User userDetails) {
                    if (userDetails == null || userDetails.getFullName() == null || userDetails.getEmail() == null) {
                        Log.w(TAG, "User details are null or incomplete, redirecting to RegisterWithDetailsActivity");
                        openSaveDetailsActivity();
                        return;
                    }

                    if (userDetails.getFullName().isEmpty() || userDetails.getEmail().isEmpty()) {
                        Log.w(TAG, "User full name or email is empty, redirecting to RegisterWithDetailsActivity");
                        openSaveDetailsActivity();
                        return;
                    }

                    UserCache.getInstance().cacheUser(userDetails);
                }

                @Override
                public void onUserDetailsFetchFailed(String errorMessage) {
                    Log.e(TAG, "Error fetching user details: " + errorMessage);
                    Log.e(TAG, "Redirecting to LoginActivity");
                    Snackbar.make(rootLayout, errorMessage, Snackbar.LENGTH_LONG).show();
                    logoutUser();
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void openSaveDetailsActivity() {
        Intent intent = new Intent(this, RegisterWithDetailsActivity.class);
        startActivity(intent);
        finish();
    }

    private void logoutUser() {
        try {
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
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void sendToStart() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleException(Exception e) {
        Log.e(TAG, "Exception occurred: " + e.getMessage(), e);
        Snackbar.make(rootLayout, "An error occurred: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }
}