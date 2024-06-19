package com.harish.hk185080.chatterbox.fragments.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.user_profile.UserProfileActivity;
import com.harish.hk185080.chatterbox.adapter.ContactsAdapter;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerViewContacts;
    private ContactsAdapter contactsAdapter;
    private ContactsViewModel contactsViewModel;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerViewContacts = rootView.findViewById(R.id.recyclerViewContacts);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(requireContext()));
        contactsAdapter = new ContactsAdapter(contact -> {
            // Handle item click (open UserProfileActivity)
            Intent intent = new Intent(requireContext(), UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.EXTRA_USER_NAME, contact.getFullName());
            startActivity(intent);
        });
        recyclerViewContacts.setAdapter(contactsAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        contactsViewModel.getContacts().observe(getViewLifecycleOwner(), contactsList -> {
            contactsAdapter.setContactsList(contactsList);
        });
    }
}
