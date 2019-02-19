package com.harish.hk185080.chatterbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.ChatOpenActivity;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.model.Inbox;
import com.harish.hk185080.chatterbox.utils.Tools;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterListInbox extends RecyclerView.Adapter<AdapterListInbox.ViewHolder> {

    private Context ctx;
    private List<Inbox> items;
    private FirebaseAuth mAuth;
    private OnClickListener onClickListener = null;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;
    private String mCurrent_user_id;
    private List<String> uidGroup;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView from, email, message, date, image_letter;
        public CircleImageView image;
        public RelativeLayout lyt_checked, lyt_image;
        public View lyt_parent;

        public ViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            email = (TextView) view.findViewById(R.id.email);
            message = (TextView) view.findViewById(R.id.message);
            date = (TextView) view.findViewById(R.id.date);
            image_letter = (TextView) view.findViewById(R.id.image_letter);
            image = view.findViewById(R.id.image);
            lyt_checked = (RelativeLayout) view.findViewById(R.id.lyt_checked);
            lyt_image = (RelativeLayout) view.findViewById(R.id.lyt_image);
            lyt_parent = (View) view.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterListInbox(Context mContext, List<Inbox> items,List<String> uidGroup) {
        this.ctx = mContext;
        this.items = items;
        this.uidGroup=uidGroup;
        selected_items = new SparseBooleanArray();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mMessageDatabase.keepSynced(true);
        mUsersDatabase.keepSynced(true);



    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Inbox inbox = items.get(position);
        final String uid=uidGroup.get(position);



        // displaying text view data

//        holder.from.setText("inbox.from");
//        holder.email.setText("inbox.email");
//        holder.message.setText("inbox.message");
//        holder.date.setText("inbox.date");
//        holder.image_letter.setText("inbox.from.substring(0, 1)");

        holder.lyt_parent.setActivated(selected_items.get(position, false));

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, inbox, position);
            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) return false;
                onClickListener.onItemLongClick(v, inbox, position);
                return true;
            }
        });

        toggleCheckedIcon(holder, position);
        //displayImage(holder, inbox);
        final String list_user_id = uid;

        Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String data = dataSnapshot.child("message").getValue().toString();
                String time=dataSnapshot.child("time").getValue().toString();
                holder.message.setText(data);
                //holder.setMessage(data, model.isSeen());
                holder.date.setText(getFormattedDate(ctx,Long.parseLong(time)));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        mUsersDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    try {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {

//                            String userOnline = dataSnapshot.child("online").getValue().toString();
//                            holder.setUserOnline(userOnline);

                        }

                        holder.from.setText(userName);
                        setImage(holder,userThumb, ctx);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void setImage(ViewHolder holder, String userThumb, Context ctx) {
        if (!userThumb.equals("default")) {
            Glide
                    .with(ctx)
                    .load(userThumb)
                    .into(holder.image);
        } else {
            holder.image.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_account_circle_white_48dp));

        }
    }

//    private void displayImage(ViewHolder holder, Inbox inbox) {
//        if (inbox.image != null) {
//            Tools.displayImageRound(ctx, holder.image, inbox.image);
//            holder.image.setColorFilter(null);
//            holder.image_letter.setVisibility(View.GONE);
//        } else {
//            holder.image.setImageResource(R.drawable.shape_circle);
//            holder.image.setColorFilter(inbox.color);
//            holder.image_letter.setVisibility(View.VISIBLE);
//        }
//    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.lyt_image.setVisibility(View.GONE);
            holder.lyt_checked.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.lyt_checked.setVisibility(View.GONE);
            holder.lyt_image.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }

    public Inbox getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        items.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public interface OnClickListener {
        void onItemClick(View view, Inbox obj, int pos);

        void onItemLongClick(View view, Inbox obj, int pos);
    }
    public String getFormattedDate(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday";
        } else if (now.get(Calendar.DAY_OF_WEEK) == smsTime.get(Calendar.DAY_OF_WEEK)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd/MM/yy", smsTime).toString();
        }
    }
}