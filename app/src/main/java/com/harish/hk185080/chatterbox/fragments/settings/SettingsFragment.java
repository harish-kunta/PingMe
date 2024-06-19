package com.harish.hk185080.chatterbox.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.home.MainActivity;
import com.harish.hk185080.chatterbox.adapter.SettingsAdapter;
import com.harish.hk185080.chatterbox.fragments.settings.account_settings.AccountSettingsFragment;
import com.harish.hk185080.chatterbox.model.SettingsItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;
    private List<SettingsItem> settingsItemList;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        settingsItemList = new ArrayList<>();
        settingsItemList.add(new SettingsItem("Account Settings", R.drawable.baseline_person_outline_24));
        settingsItemList.add(new SettingsItem("Chat Settings", R.drawable.ic_baseline_chat_24));
        //settingsItemList.add(new SettingsItem("Appearance Settings", R.drawable.ic_appearance));

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

    private void navigateToSettingsFragment(String title) {
        // Communicate navigation request to MainActivity
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToSettingsFragment(title);
        }
    }
}
