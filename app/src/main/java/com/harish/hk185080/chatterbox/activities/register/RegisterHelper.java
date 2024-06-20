package com.harish.hk185080.chatterbox.activities.register;

import static com.harish.hk185080.chatterbox.utils.StringResourceHelper.checkCapitalLetters;
import static com.harish.hk185080.chatterbox.utils.StringResourceHelper.isValidEmail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.ScrollView;

import com.google.android.material.snackbar.Snackbar;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.activities.login.LoginActivity;
import com.harish.hk185080.chatterbox.database.DataSourceHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.utils.StringResourceHelper;

public class RegisterHelper {
    private static final String TAG = "RegisterHelper";
    private Context context;
    private Activity activity;
    private ScrollView rootLayout;
    private ProgressDialog mRegProgress;

    public RegisterHelper(Context context, ScrollView rootLayout) {
        this.activity = (Activity) context;
        this.context = context;
        this.rootLayout = rootLayout;
    }

    public void signup(String name, String email, String mobile, String password, String confirmPassword) {
        if (checkCapitalLetters(email)) {
            Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.email_cannot_contain_capital_letters), Snackbar.LENGTH_LONG).show();
        } else if (!isValidEmail(email)) {
            Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_valid_email), Snackbar.LENGTH_LONG).show();
        } else if (password.length() <= 0) {
            Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.enter_password), Snackbar.LENGTH_LONG).show();
        } else if (!confirmPassword.equals(password)) {
            Snackbar.make(rootLayout, StringResourceHelper.getString(context, R.string.passwords_not_match), Snackbar.LENGTH_LONG).show();
        } else {
            mRegProgress = new ProgressDialog(context, R.style.AppThemeDialog);
            mRegProgress.setIndeterminate(true);
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.setMessage("Creating Account...");
            mRegProgress.show();


            IDataSource dataSource = DataSourceHelper.getDataSource();
            dataSource.createUser(name, email, password, new IDataSourceCallback() {
                @Override
                public void onSuccess() {
                    mRegProgress.dismiss();
                    Snackbar.make(rootLayout, "Verification email sent to " + email, Snackbar.LENGTH_LONG).show();
                    openLoginPage();
                }

                @Override
                public void onFailure(String errorMessage) {
                    mRegProgress.dismiss();
                    Snackbar.make(rootLayout, errorMessage, Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void openLoginPage() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        activity.finish();
    }
}

