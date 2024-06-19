package com.harish.hk185080.chatterbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.model.SettingsItem;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    private List<SettingsItem> settingsItemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SettingsItem item);
    }

    public SettingsAdapter(List<SettingsItem> settingsItemList, OnItemClickListener listener) {
        this.settingsItemList = settingsItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        SettingsItem item = settingsItemList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return settingsItemList.size();
    }

    static class SettingsViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView title;

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }

        public void bind(final SettingsItem item, final OnItemClickListener listener) {
            icon.setImageResource(item.getIconRes());
            title.setText(item.getTitle());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
