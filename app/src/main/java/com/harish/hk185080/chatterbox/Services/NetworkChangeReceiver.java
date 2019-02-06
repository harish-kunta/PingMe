package com.harish.hk185080.chatterbox.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.harish.hk185080.chatterbox.utils.NetworkUtil;



public class NetworkChangeReceiver extends BroadcastReceiver {
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String status = NetworkUtil.getConnectivityStatusString(context);


        Log.e("Receiver ", "" + status);

      //  MainActivity.connectionStatus(status.contains(Constants.NOT_CONNECT),context);
//        if (status.equals(Constants.NOT_CONNECT)) {
//            Log.e("Receiver ", "not connection");// your code when internet lost
//           MainActivity.connectionStatus(true,context);
//
//        } else {
//            Log.e("Receiver ", "connected to internet");//your code when internet connection come back
//           MainActivity.connectionStatus(false,context);
//        }

    }
}
