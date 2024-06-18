package com.harish.hk185080.chatterbox.utils;

import android.content.Context;
import android.content.res.Resources;

import java.util.regex.Pattern;

public class StringResourceHelper {
    public static String getString(Context context, int id) {
        Resources res = context.getResources();
        return res.getString(id);
    }

    public static boolean checkCapitalLetters(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if (capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
