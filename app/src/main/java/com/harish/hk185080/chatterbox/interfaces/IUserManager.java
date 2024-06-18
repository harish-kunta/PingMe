package com.harish.hk185080.chatterbox.interfaces;

import android.content.Context;

public interface IUserManager {
    void getCurrentUserDetails(Context context, IUserDetailsCallback callback);
}
