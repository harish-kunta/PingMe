package com.harish.hk185080.chatterbox.fragments.settings.account_settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.welcome.WelcomeActivity;
import com.harish.hk185080.chatterbox.adapter.SettingsAdapter;
import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.model.SettingsItem;

import java.util.ArrayList;
import java.util.List;

public class AccountSettingsFragment extends Fragment {

    IDataSource dataSource;
    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;
    private List<SettingsItem> settingsItemList;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        dataSource = DataSourceHelper.getDataSource();
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Account Settings");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        settingsItemList = new ArrayList<>();
        settingsItemList.add(new SettingsItem("Log Out", R.drawable.baseline_logout_24));
        settingsItemList.add(new SettingsItem("Delete Account", R.drawable.ic_delete));
        settingsItemList.add(new SettingsItem("Change Password", R.drawable.baseline_password_24));

        settingsAdapter = new SettingsAdapter(settingsItemList, new SettingsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SettingsItem item) {
                // Handle item click for account settings actions
                handleAccountSettingClick(item);
            }
        });

        recyclerView.setAdapter(settingsAdapter);

        return view;
    }

    private void handleAccountSettingClick(SettingsItem item) {
        // Implement actions based on clicked item (e.g., log out, delete account, change password)

        // Example: Log out
        if (item.getTitle().equals("Log Out")) {
            // Perform logout logic here
            setupLogOut();
        }
    }

    private void setupLogOut() {
        AlertDialog.Builder builderSingle = new MaterialAlertDialogBuilder(getContext(), R.style.MyRoundedMaterialDialog);
        builderSingle.setTitle("Are you sure you want to logout?");
        builderSingle.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataSource.logoutUser(new IDataSourceCallback() {
                    @Override
                    public void onSuccess() {
                        sendToStart();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        //Snackbar.make(rootLayout, errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        builderSingle.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    private void sendToStart() {
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}

