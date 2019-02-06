package com.harish.hk185080.chatterbox;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;




public class AboutActivity extends AppCompatActivity {
    String version;
    TextView versionLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about);
        setContentView(R.layout.activity_about);

        versionLabel=findViewById(R.id.version_number);

            Toolbar toolbar = findViewById(R.id.toolbar1);
            setTitle("Ping Me");
            setSupportActionBar(toolbar);
            if (toolbar != null) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                    }
                });
            }


        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int build=pInfo.versionCode;
            versionLabel.setText(getString(R.string.version, version,build));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

            FloatingActionButton share = findViewById(R.id.shareapp);
            if (share != null) {
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                                try {
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(Intent.EXTRA_SUBJECT, "Ping Me");
                                    String sAux = "\nCheck out new app  PingMe :\n\n";
                                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.harish.hk185080.chatterbox\n\n";
                                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                                    startActivity(Intent.createChooser(i, "choose one"));
                                } catch (Exception e) {
                                    e.printStackTrace();

                            }
//                            BottomSheet sheet = BottomSheetHelper.shareAction(AboutActivity.this, shareIntent).title("Share App").build();
//                            sheet.show();
                        } catch (Exception e) {

                        } catch (Error error) {

                        }
                    }
                });
            }

            ImageView gmail, fb, whatsapp,instagram,youtube;
            gmail = findViewById(R.id.gmailpic);
            fb = findViewById(R.id.fbpic);
            whatsapp = findViewById(R.id.whatsappic);
            instagram= findViewById(R.id.instapic);
            youtube= findViewById(R.id.youtubepic);
            if (gmail != null) {
                gmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            emailIntent.setType("vnd.android.cursor.item/email");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"harishtanu007@gmail.com"});
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Regarding ChatterBox");
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                            startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
                        } catch (Exception e) {

                        } catch (Error e) {

                        }
                    }
                });
            }
            if(youtube!=null)
            {
                youtube.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.youtube.com/channel/UCKQbQvxxCxvhBH8aj-2RUXA"));
                        startActivity(intent);
                    }
                });
            }
            if(instagram!=null)
            {
                instagram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("http://instagram.com/_u/harishtanu");
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                        likeIng.setPackage("com.instagram.android");

                        try {
                            startActivity(likeIng);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://instagram.com/harishtanu")));
                        }
                    }
                });
            }
            if (whatsapp != null) {
                whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //  About_me.this.addActivityListener(someActivityListener);
                        try {
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

// Just two examples of information you can send to pre-fill out data for the
// user.  See android.provider.ContactsContract.Intents.Insert for the complete
// list.
                            intent.putExtra(ContactsContract.Intents.Insert.NAME, "Harish @Developer");
                            intent.putExtra(ContactsContract.Intents.Insert.PHONE, "8801000264");

// Send with it a unique request code, so when you get called back, you can
// check to make sure it is from the intent you launched (ideally should be
// some public static final so receiver can check against it)
                            int PICK_CONTACT = 100;
                            AboutActivity.this.startActivityForResult(intent, PICK_CONTACT);
                        } catch (Exception e) {

                        } catch (Error e) {

                        }
                    }
                });
                if (fb != null) {
                    fb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
//                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/harishtannu1"));
//                                startActivity(browserIntent);
                                Intent facebookIntent = getOpenFacebookIntent(AboutActivity.this);
                                startActivity(facebookIntent);
                            } catch (Exception e) {

                            } catch (Error e) {

                            }
                        }
                    });
                }
            }

    }
    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/100002792891169")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/harish.tannu1")); //catches and opens a url to the desired page
        }
    }
    }

