package com.harish.hk185080.chatterbox.fragments.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.user_profile.UserProfileActivity;
import com.harish.hk185080.chatterbox.adapter.UserSearchAdapter;
import com.harish.hk185080.chatterbox.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private UserSearchAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = view.findViewById(R.id.searchView);
        RecyclerView recyclerViewSearchResults = view.findViewById(R.id.recyclerViewSearchResults);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchAdapter = new UserSearchAdapter(user -> {
            // Handle item click (open UserProfileActivity)
            Intent intent = new Intent(requireContext(), UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.EXTRA_USER_NAME, user.getFullName());
            intent.putExtra(UserProfileActivity.EXTRA_USER_ID, user.getUserID());
            startActivity(intent);
        });
        recyclerViewSearchResults.setAdapter(searchAdapter);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), usersList -> {
            if (usersList == null || usersList.isEmpty()) {
                Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show();
            }
            searchAdapter.setUsersList(usersList);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() >= 3) {
                    searchViewModel.searchUsersByName(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    searchViewModel.searchUsersByName(newText);
                } else {
                    searchAdapter.setUsersList(new ArrayList<>());
                }
                return false;
            }
        });
    }
}

