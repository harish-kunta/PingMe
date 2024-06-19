package com.harish.hk185080.chatterbox.fragments.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.harish.hk185080.chatterbox.databinding.FragmentProfileBinding;

public class ContactsFragment extends Fragment {

    private FragmentProfileBinding binding;

    private CoordinatorLayout rootLayout;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContactsViewModel contactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rootLayout = binding.rootLayout;

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}