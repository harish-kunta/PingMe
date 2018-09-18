package com.harish.hk185080.chatterbox;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harish.hk185080.chatterbox.data.MyData;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSaveChanges;


    private DatabaseReference mStatusDatabase;
    private FirebaseUser mFirebaseUser;

    private ProgressDialog mStatusProgress;
    private RelativeLayout rootLayout;
    private MyData myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        rootLayout = findViewById(R.id.rootlayout);
        myData = new MyData();
        mToolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mFirebaseUser.getUid();

        String status_value = getIntent().getStringExtra("status_value");

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mStatus = findViewById(R.id.status_text);
        mSaveChanges = findViewById(R.id.change_status_button);

        mStatus.getEditText().setText(status_value);
        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myData.isInternetConnected(StatusActivity.this)) {

                    mStatusProgress = new ProgressDialog(StatusActivity.this);
                    mStatusProgress.setTitle("Saving Changes");
                    mStatusProgress.setMessage("Please wait while we save changes");
                    mStatusProgress.show();

                    String status = mStatus.getEditText().getText().toString();

                    mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mStatusProgress.dismiss();
                                Snackbar.make(rootLayout, "Status Changed Successfully", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(rootLayout, "Error in uploading status", Snackbar.LENGTH_LONG).show();

                            }
                        }
                    });
                } else {
                    Snackbar.make(rootLayout, "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
