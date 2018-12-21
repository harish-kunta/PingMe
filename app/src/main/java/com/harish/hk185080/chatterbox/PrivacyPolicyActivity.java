package com.harish.hk185080.chatterbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.harish.hk185080.chatterbox.utils.AppWebViewClients;

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView webview;
    private Toolbar mToolbar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        mToolbar = findViewById(R.id.users_appbar);
        progressBar=findViewById(R.id.loading_spinner);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Privacy Policy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webview = (WebView) findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new AppWebViewClients(progressBar));
        webview.loadUrl("https://chatterboxappharish.blogspot.com/2018/06/privacy-policy.html");

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
