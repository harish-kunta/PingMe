package com.harish.hk185080.chatterbox;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public NotificationsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDate(String date) {
        TextView userNameView = mView.findViewById(R.id.user_single_status);
        userNameView.setText(date);

    }

    public void setName(String name) {
        TextView userNameView = mView.findViewById(R.id.user_single_name);
        userNameView.setText(name);
    }

    public void setUserImage(String thumb_image, Context ctx) {
        CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
        try {
            if (!thumb_image.equals("default")) {
                Glide.with(ctx)
                        .load(thumb_image)
                        .into(userImageView);
                //Picasso.get().load(thumb_image).placeholder(R.drawable.ic_account_circle_white_48dp).into(userImageView);
            } else {

                userImageView.setImageResource(R.drawable.ic_account_circle_white_48dp);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            userImageView.setImageResource(R.drawable.ic_account_circle_white_48dp);

        }
    }

    public void setUserOnline(String online_status) {
        View userOnlineView = mView.findViewById(R.id.user_single_online);
        if (online_status.equals("true")) {

            userOnlineView.setVisibility(View.VISIBLE);
        } else {

            userOnlineView.setVisibility(View.INVISIBLE);
        }
    }
}
