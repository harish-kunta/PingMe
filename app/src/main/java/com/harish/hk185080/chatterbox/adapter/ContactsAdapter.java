package com.harish.hk185080.chatterbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harish.hk185080.chatterbox.model.User;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<User> contactsList;
    private OnContactClickListener onContactClickListener;

    public ContactsAdapter(OnContactClickListener onContactClickListener) {
        this.onContactClickListener = onContactClickListener;
    }

    public void setContactsList(List<User> contactsList) {
        this.contactsList = contactsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String contact = contactsList.get(position).getFullName();
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contactsList != null ? contactsList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContact = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        public void bind(String contact) {
            textViewContact.setText(contact);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                User contact = contactsList.get(position);
                if (onContactClickListener != null) {
                    onContactClickListener.onContactClick(contact);
                }
            }
        }
    }

    public interface OnContactClickListener {
        void onContactClick(User contact);
    }
}

