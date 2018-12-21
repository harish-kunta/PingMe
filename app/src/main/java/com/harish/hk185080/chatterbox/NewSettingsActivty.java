package com.harish.hk185080.chatterbox;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class NewSettingsActivty extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();



    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            // gallery EditText change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_gallery_name)));

            // notification preference change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));

            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

            Preference version = findPreference("version");
            try {
                version.setSummary(getString(R.string.app_version,appVersion(getActivity()),appBuild(getActivity())));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Preference sentRequest=findPreference(getString(R.string.sent_requests));
            sentRequest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    openRequestActivity(getActivity());
                    return false;
                }
            });

            Preference changePassword=findPreference(getString(R.string.change_password));
            changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    openChangePasswordActivity(getActivity());
                    return false;
                }
            });

//            Preference blockedUsers=findPreference(getString(R.string.blocked_users));
//            blockedUsers.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    openBlockedUsersActivity(getActivity());
//                    return false;
//                }
//            });
            Preference notificationsList=findPreference(getString(R.string.notifications));
            notificationsList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    openNotificationsListActivity(getActivity());
                    return false;
                }
            });

//            Preference notificationRingtone=findPreference(getString(R.string.key_notifications_new_message_ringtone));
//            notificationRingtone.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//
//
//                    return false;
//                }
//            });



        }




    }



public int appBuild() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        int build = pInfo.versionCode;
        return build;
    }

//    private static void bindPreferenceSummaryToValue(Preference preference) {
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
//                PreferenceManager
//                        .getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), ""));
//    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(R.string.summary_choose_ringtone);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("key_gallery_name")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        int code;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body+"."+ code+ "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"harishtanu007@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from Ping Me app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
    public static void openRequestActivity(Context context)
    {
        Intent i = new Intent(context.getApplicationContext(), SentRequestActivity.class);
        context.startActivity(i);
    }
    public static void openChangePasswordActivity(Context context)
    {
        Intent i = new Intent(context.getApplicationContext(), ChangePasswordActivity.class);
        context.startActivity(i);
    }

    public static void openBlockedUsersActivity(Context context)
    {
        Intent i = new Intent(context.getApplicationContext(), BlockedListActivity.class);
        context.startActivity(i);
    }

    public static String appVersion(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        String version = pInfo.versionName;
        return version;
    }
    public static int appBuild(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        int build = pInfo.versionCode;
        return build;
    }
    public static void openNotificationsListActivity(Context context)
    {
        Intent i = new Intent(context.getApplicationContext(), NotificationsActivity.class);
        context.startActivity(i);
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
