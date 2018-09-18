package com.harish.hk185080.chatterbox;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;



public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    NotificationManager mNotifyManager;
    String MESSAGES = "MESSAGES";
    String REQUESTS = "REQUESTS";
    @Override
    public void onCreate() {
        super.onCreate();
    }

//    private int getUnOpenedChatsCount() {
//
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {



            String notification_title = remoteMessage.getNotification().getTitle();
            String notification_message = remoteMessage.getNotification().getBody();
            String user_id = remoteMessage.getData().get("user_id");
            String notification_type=remoteMessage.getData().get("type");
            Log.d("FirebaseNotification", "title:"+notification_message);
            Log.d("FirebaseNotification", "message:"+notification_message);
            Log.d("FirebaseNotification", "from_user_id"+user_id);

            if (notification_type.equals("message")) {

                createNotificationChannel();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String chatOpenUserID = prefs.getString("ChatOpenUserID", "");
                if (!chatOpenUserID.equals(user_id)) {

                    Intent resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.putExtra("user_id", user_id);
                    resultIntent.putExtra("user_name", notification_title);
                    resultIntent.putExtra("type",notification_type);


//
//// Create the TaskStackBuilder and add the intent, which inflates the back stack
//                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//                stackBuilder.addNextIntentWithParentStack(resultIntent);
//// Get the PendingIntent containing the entire back stack
//                PendingIntent resultPendingIntent =
//                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    PendingIntent resultPendingIntent =
                            TaskStackBuilder.create(this)
                                    // add all of DetailsActivity's parents to the stack,
                                    // followed by DetailsActivity itself
                                    .addNextIntentWithParentStack(resultIntent)
                                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MESSAGES)
                            .setSmallIcon(R.drawable.chatter_box_logo)
                            .setContentTitle(notification_title)
                            .setContentText(notification_message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .setContentIntent(resultPendingIntent);
                    int mNotificationId = (int) System.currentTimeMillis();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(mNotificationId, mBuilder.build());
                }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new
//                    NotificationChannel(NOTIFICATION_CHANNEL_ID, title, importance);
//            mChannel.setDescription(notification);
//            mChannel.enableLights(true);
//            mChannel.setLightColor(ContextCompat.getColor
//                    (getApplicationContext(),R.color.colorPrimary));
//            notificationManager.createNotificationChannel(mChannel);
//
//        }
//
//        Log.d("FirebaseNotification","Received");
//        String notification_title=remoteMessage.getNotification().getTitle();
//        String notification_message=remoteMessage.getNotification().getBody();
//        String click_action=remoteMessage.getNotification().getClickAction();
//        String from_user_id=remoteMessage.getData().get("title");
//        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.skyline);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.chatter_box_logo)
//                .setContentTitle(notification_title)
//                .setContentText(notification_message);
//
//
//        Intent resultIntent = new Intent(click_action);
//        resultIntent.putExtra("title",from_user_id);
//
//        PendingIntent resultPendingIntent=
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        mBuilder.setContentIntent(resultPendingIntent);
//        mBuilder.setSound(sound);
//
//        int mNotificationId=(int) System.currentTimeMillis();
//        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(mNotificationId,mBuilder.build());
//


//        String title=remoteMessage.getNotification().getTitle();
//        String message=remoteMessage.getNotification().getBody();
//
//        Intent i = new Intent(this,AboutActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);
//        /*int color=3066993;*/
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setAutoCancel(true)
//                .setContentTitle(title)
//                .setContentText(message)
//                /* .setColor(color)*/
//                .setSmallIcon(R.drawable.ic_group_white_24dp)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        manager.notify(0,builder.build());
            }
            else if(notification_type.equals("request"))
            {
                createRequestChannel();
                Intent resultIntent = new Intent(this, MainActivity.class);
                resultIntent.putExtra("user_id", user_id);
                resultIntent.putExtra("type",notification_type);
                //resultIntent.putExtra("user_name", notification_title);


//
//// Create the TaskStackBuilder and add the intent, which inflates the back stack
//                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//                stackBuilder.addNextIntentWithParentStack(resultIntent);
//// Get the PendingIntent containing the entire back stack
//                PendingIntent resultPendingIntent =
//                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                PendingIntent resultPendingIntent =
                        TaskStackBuilder.create(this)
                                // add all of DetailsActivity's parents to the stack,
                                // followed by DetailsActivity itself
                                .addNextIntentWithParentStack(resultIntent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MESSAGES)
                        .setSmallIcon(R.drawable.chatter_box_logo)
                        .setContentTitle(notification_title)
                        .setContentText(notification_message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent);
                int mNotificationId = (int) System.currentTimeMillis();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(mNotificationId, mBuilder.build());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void createRequestChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = REQUESTS;
            String description = getString(R.string.requests_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(REQUESTS, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = MESSAGES;
            String description = getString(R.string.requests_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MESSAGES, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
