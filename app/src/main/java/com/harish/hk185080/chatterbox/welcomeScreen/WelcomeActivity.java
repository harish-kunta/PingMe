package com.harish.hk185080.chatterbox.welcomeScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.harish.hk185080.chatterbox.MainActivity;
import com.harish.hk185080.chatterbox.NewMainActivtiy.BottomNavigationShifting;
import com.harish.hk185080.chatterbox.R;
import com.harish.hk185080.chatterbox.StartActivity;

/**
 * Created by vihaan on 15/06/17.
 */

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{
TextView customText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.agreeNContinueTVBtn).setOnClickListener(this);
        customText=findViewById(R.id.agreeNContinueTV);
        customTextView(customText);
        init();
    }

    private FirebaseAuth mAuth;
    private void init()
    {
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.agreeNContinueTVBtn:
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void onAuthSuccess(FirebaseUser user) {
        startActivity(new Intent(WelcomeActivity.this, BottomNavigationShifting.class));
        finish();
    }
    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "Tap 'Agree and continue' to accept the Ping Me ");
        spanTxt.append("Terms of Service");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_terms)));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - "Terms of Service".length(), spanTxt.length(), 0);
        spanTxt.append(" and");
        //spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 32, spanTxt.length(), 0);
        spanTxt.append(" Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy)));
                startActivity(browserIntent);
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }
}
