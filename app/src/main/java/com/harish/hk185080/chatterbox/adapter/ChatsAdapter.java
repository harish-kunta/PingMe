package com.harish.hk185080.chatterbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.harish.hk185080.chatterbox.ChatActivity;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.model.ChatThread;
import com.harish.hk185080.chatterbox.viewholder.ChatsViewHolder;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsViewHolder> {
    private final List<ChatThread> chatThreads;
    private final Context context;

    public ChatsAdapter(List<ChatThread> chatThreads, Context context) {
        this.chatThreads = chatThreads;
        this.context = context;
    }

    @Override
    public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatsViewHolder chatsViewHolder = new ChatsViewHolder(layoutView);
        return chatsViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int position) {
        //Set ViewTag
        holder.itemView.setTag(position);
        ChatThread chatThread = chatThreads.get(position);
        holder.mUserName.setText(chatThread.getName());
        holder.mUserLastMessage.setText(chatThread.getEmail());
        holder.setPostImage(chatThread, holder.itemView.getContext());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            context.startActivity(intent);
        });
    }

    private String getDistanceMetric() {
        return "miles";
    }

    @Override
    public int getItemCount() {
        return this.chatThreads.size();
    }
}
