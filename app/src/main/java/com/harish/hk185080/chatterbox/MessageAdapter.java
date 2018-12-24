package com.harish.hk185080.chatterbox;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harish.hk185080.chatterbox.data.Constants;
import com.harish.hk185080.chatterbox.data.MyData;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private Context context;
    private CoordinatorLayout rootLayout;
    private ActionMode actionMode;
    private boolean isMultiSelect = false;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private DatabaseReference mRootRef;
    private MyData myData;
    private String mChatUser;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    public MessageAdapter(List<Messages> mMessageList, Context context, CoordinatorLayout rootLayout, String mChatUser) {

        this.mMessageList = mMessageList;
        this.context = context;
        this.rootLayout = rootLayout;
        this.mChatUser = mChatUser;
        myData = new MyData();
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = mMessageList.get(position);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        if (message.getFrom().equals(mCurrentUserId)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ownchat_tile, parent, false);

            return new MessageViewHolder(v);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_tile, parent, false);

            return new OwnViewHolder(v);
        }
        return null;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;


        public TextView timeText;

        public View mView;

        public MessageViewHolder(View view) {
            super(view);
            mView = view;
            messageText = view.findViewById(R.id.content);
            profileImage = view.findViewById(R.id.userdp);
            //displayName = (TextView) view.findViewById(R.id.name_text_layout);
            //messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeText = view.findViewById(R.id.textview_time);
        }


    }

    public class OwnViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;


        public TextView timeText;

        public View mView;

        public OwnViewHolder(View view) {
            super(view);
            mView = view;
            messageText = view.findViewById(R.id.content);
            profileImage = view.findViewById(R.id.userdp);
            //displayName = (TextView) view.findViewById(R.id.name_text_layout);
            //messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeText = view.findViewById(R.id.textview_time);
            // calender = (TextView) view.findViewById(R.id.calender_bar_layout);

        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof MessageViewHolder) {
            final MessageViewHolder holder = (MessageViewHolder) viewHolder;

            final Messages c = mMessageList.get(i);

            final String from_user = c.getFrom();
            String message_type = c.getType();
            final Long time = c.getTime();
            final String message_id = c.getMessageid();


            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    //viewHolder.displayName.setText(name);
                    String date = Constants.getFormattedtime(context, time);

                    holder.timeText.setText(date);
                    Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_white_48dp).into(holder.profileImage);
                    long previousTs = 0;
                    if (i > 0) {
                        Messages pm = mMessageList.get(i - 1);
                        previousTs = pm.getTime();
                    }
                    //setTimeTextVisibility(c.getTime(), previousTs, viewHolder.calender);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (message_type.equals("text")) {

                holder.messageText.setText(c.getMessage());
                // viewHolder.messageImage.setVisibility(View.INVISIBLE);


            } else {

//            viewHolder.messageText.setVisibility(View.INVISIBLE);
//            Picasso.get().load(c.getMessage()).placeholder(R.drawable.ic_user_image).into(viewHolder.messageImage);

            }
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String message = holder.messageText.getText().toString();
                    Log.d("Clicked", message);
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);


                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
                    arrayAdapter.add("Copy");

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {

                                ClipboardManager myClickboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData myClip = ClipData.newPlainText("text", message);
                                myClickboard.setPrimaryClip(myClip);
                                Snackbar.make(rootLayout, "Copied to Clipboard", Snackbar.LENGTH_LONG).show();

                            } else if (which == 1) {

//                                ClipboardManager myClickboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                                ClipData myClip = ClipData.newPlainText("text", message);
//                                myClickboard.setPrimaryClip(myClip);
                                mRootRef = FirebaseDatabase.getInstance().getReference();
                                if (myData.isInternetConnected(holder.mView.getContext())) {
                                    android.app.AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new android.app.AlertDialog.Builder(holder.mView.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                                    } else {
                                        builder = new android.app.AlertDialog.Builder(holder.mView.getContext());
                                    }
                                    builder
                                            .setMessage("Are you sure you want to Delete this message?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with delete
                                                    if (myData.isInternetConnected(holder.mView.getContext())) {
                                                        Map deleteChatMap = new HashMap();
                                                        deleteChatMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + message_id, null);


                                                        mRootRef.updateChildren(deleteChatMap, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                if (databaseError == null) {
                                                                    Snackbar.make(rootLayout, "Message deleted!!!", Snackbar.LENGTH_LONG).show();
                                                                    delete(holder.getAdapterPosition());
                                                                    //notifyItemRemoved(i);

                                                                } else {
                                                                    String error = databaseError.getMessage();
                                                                    Log.e("ChatsFragment", error);
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();

                                } else {
                                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                                }
                            }
//                String strName = arrayAdapter.getItem(which);
//                AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
//                builderInner.setMessage(strName);
//                builderInner.
//
//("Your Selected Item is");
//                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog,int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builderInner.show();
                        }
                    });
                    builderSingle.show();
                    return true;
                }

            });

        }
        if (viewHolder instanceof OwnViewHolder) {
            final OwnViewHolder holder = (OwnViewHolder) viewHolder;

            final Messages c = mMessageList.get(i);

            String from_user = c.getFrom();
            String message_type = c.getType();
            final Long time = c.getTime();


            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    //viewHolder.displayName.setText(name);
                    String date = Constants.getFormattedtime(context, time);

                    holder.timeText.setText(date);
                    if (!image.equals("default")) {
//                        Glide
//                                .with(holder.mView.getContext())
//                                .load(image)
//                                .into(holder.profileImage);
                        Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_white_48dp).into(holder.profileImage);
                    } else {
                        holder.profileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_account_circle_white_48dp));

                    }
                    long previousTs = 0;
                    if (i > 0) {
                        Messages pm = mMessageList.get(i - 1);
                        previousTs = pm.getTime();
                    }
                    //setTimeTextVisibility(c.getTime(), previousTs, viewHolder.calender);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (message_type.equals("text")) {

                holder.messageText.setText(c.getMessage());
                // viewHolder.messageImage.setVisibility(View.INVISIBLE);


            } else {

//            viewHolder.messageText.setVisibility(View.INVISIBLE);
//            Picasso.get().load(c.getMessage()).placeholder(R.drawable.ic_user_image).into(viewHolder.messageImage);

            }
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String message = holder.messageText.getText().toString();
                    Log.d("Clicked", message);
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);


                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
                    arrayAdapter.add("Copy");

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {

                                ClipboardManager myClickboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData myClip = ClipData.newPlainText("text", message);
                                myClickboard.setPrimaryClip(myClip);
                                Snackbar.make(rootLayout, "Copied to Clipboard", Snackbar.LENGTH_LONG).show();

                            }
                        }
                    });
                    builderSingle.show();
                    return true;
                }

            });

        }

    }


//    private String getTime(long unixTime) {
////        long time = unixTime * (long) 1000;
////        Date date = new Date(time);
////        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy a");
////        format.setTimeZone(TimeZone.getTimeZone("GMT"));
////        Log.d("date", format.format(date));
////        return format.format(date);
//
//        try {
//            Calendar calendar = Calendar.getInstance();
//            TimeZone tz = TimeZone.getTimeZone("IST");
//            calendar.setTimeInMillis(unixTime);
//            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//            Date currenTimeZone = calendar.getTime();
//            return sdf.format(currenTimeZone);
//        } catch (Exception e) {
//        }
//        return "";
//    }

    private String getCalenderTime(long unixTime) {
        try {
            Calendar calendar = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("IST");
            calendar.setTimeInMillis(unixTime);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            Date currenTimeZone = calendar.getTime();
            if (now.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                return "Today";
            } else if (now.get(Calendar.DATE) - calendar.get(Calendar.DATE) == 1) {
                return "Yesterday";
            } else {
                return DateFormat.getDateInstance().format(currenTimeZone);
            }
        } catch (Exception e) {
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView timeText) {

        if (ts2 == 0) {
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(getCalenderTime(ts1));
        } else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(ts1);
            cal2.setTimeInMillis(ts2);

            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_WEEK) == cal2.get(Calendar.DAY_OF_WEEK);

            if (sameMonth) {
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            } else {
                timeText.setVisibility(View.VISIBLE);
                timeText.setText(getCalenderTime(ts1));
            }

        }
    }

    public void delete(int position) { //removes the row
        mMessageList.remove(position);
        notifyItemRemoved(position);
    }



}