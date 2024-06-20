package com.harish.hk185080.chatterbox.fragments.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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

        Button button = rootView.findViewById(R.id.temp_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Communicate navigation request to MainActivity
                ViewPager2 viewPager = requireActivity().findViewById(R.id.view_pager);
                viewPager.setCurrentItem(4, true);
            }
        });


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.contacts_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_search) {
                    NavHostFragment.findNavController(ContactsFragment.this).navigate(R.id.navigation_search);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        contactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        contactsViewModel.getContacts().observe(getViewLifecycleOwner(), contactsList -> {
            contactsAdapter.setContactsList(contactsList);
        });
    }
}
