package com.harish.hk185080.chatterbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.SearchViewHolder> {

    private List<User> usersList;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public UserSearchAdapter(OnItemClickListener onItemClickListener) {
        this.usersList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.userNameTextView.setText(user.getFullName());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(user));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTextView;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.textViewUserName);
        }
    }
}

