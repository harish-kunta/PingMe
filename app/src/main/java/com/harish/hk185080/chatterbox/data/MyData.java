package com.harish.hk185080.chatterbox.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyData  {
    private static final String TAG ="MyData" ;
    String msg;
    public boolean isInternetConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

}
