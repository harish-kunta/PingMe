package com.harish.hk185080.chatterbox.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.model.ChatThread;

public class ChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mUserName, mUserLastMessage;
    public String mUserId;
    public ImageView mUserImage;

    public ChatsViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mUserName = (TextView) itemView.findViewById(R.id.chat_username);
        mUserLastMessage = (TextView) itemView.findViewById(R.id.chat_last_message);
        mUserImage = (ImageView) itemView.findViewById(R.id.chat_profile_image);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), mUserName.getText(), Toast.LENGTH_LONG);
    }

    public void setPostImage(ChatThread chatThread, Context context) {
        mUserId = chatThread.getUid();

        String imageUrl = "";

        if (imageUrl == null || imageUrl.isEmpty()) {
            // If the image value is null, load a default placeholder image
            Glide.with(context)
                    .load(R.drawable.card_view_place_holder_image)
                    .into(mUserImage);
        } else {
            // If the image value is not null, load the actual image using Glide
            Glide.with(context)
                    .load(imageUrl)
                    .into(mUserImage);
        }
    }
}
