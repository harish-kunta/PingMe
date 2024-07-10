package com.harish.hk185080.chatterbox.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.user_profile.UserProfileActivity;
import com.harish.hk185080.chatterbox.adapter.SettingsAdapter;
import com.harish.hk185080.chatterbox.model.SettingsItem;
import com.harish.hk185080.chatterbox.model.User;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;
    private List<SettingsItem> settingsItemList;

    private SettingsViewModel settingsViewModel;
    private TextView usernameTextView;
    private TextView statusTextView;
    private ImageView mUserImage;
    private LinearLayout mUserProfileView;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");

        mUserProfileView = view.findViewById(R.id.user_profile_view);
        usernameTextView = view.findViewById(R.id.username);
        statusTextView = view.findViewById(R.id.userStatus);
        mUserImage = view.findViewById(R.id.userImage);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        settingsItemList = new ArrayList<>();
        settingsItemList.add(new SettingsItem("Account Settings", R.drawable.baseline_person_outline_24));
        settingsItemList.add(new SettingsItem("Chat Settings", R.drawable.ic_baseline_chat_24));

        // Observe changes in user details and update UI
        settingsViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                usernameTextView.setText(user.getFullName());
                statusTextView.setText(user.getBio());
                setUserImage(getContext(), user.getProfilePictureURL());
                mUserProfileView.setOnClickListener(v -> {
                    navigateToUserProfile(user);
                });
            }
        });

        settingsAdapter = new SettingsAdapter(settingsItemList, new SettingsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SettingsItem item) {
                // Handle item click to navigate to appropriate settings fragment
                navigateToSettingsFragment(item.getTitle());
            }
        });



        recyclerView.setAdapter(settingsAdapter);

        return view;
    }

    private void navigateToUserProfile(User user) {
        Intent intent = new Intent(requireContext(), UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.EXTRA_USER_NAME, user.getFullName());
        intent.putExtra(UserProfileActivity.EXTRA_USER_ID, user.getUserID());
        startActivity(intent);
    }

    private void navigateToSettingsFragment(String title) {
        ViewPager2 viewPager = requireActivity().findViewById(R.id.view_pager);
        viewPager.setCurrentItem(3, true);
    }

    public void setUserImage(Context context, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            // If the image value is null, load a default placeholder image
            Glide.with(context)
                    .load(R.drawable.card_view_place_holder_image)
                    .into(mUserImage);
        } else {
            // If the image value is not null, load the actual image using Glide
            Glide.with(context)
                    .load(imageUrl)
                    .into(mUserImage);
        }
    }
}
