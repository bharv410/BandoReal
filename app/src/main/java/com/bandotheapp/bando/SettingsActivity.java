package com.bandotheapp.bando;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.analytics.GoogleAnalytics;

import classes.InstagramApp;
import classes.TwitterLogin;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String client_id = "49fcbbb3abe9448798d8849806da6cd4";
    private final String client_secret = "424a0cc8965a4f7da7c73897fb90b810";
    private final String callback_url = "http://phantom.com";

    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private InstagramApp mApp;
    private InstagramApp.OAuthAuthenticationListener listener;

    CheckBoxPreference twitPref, igPref;
    Preference about;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        listener = new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(SettingsActivity.this, "Connected to IG!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(SettingsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        };
        mApp = new InstagramApp(this, client_id, client_secret, callback_url);
        mApp.setListener(listener);

        setupSimplePreferencesScreen();

    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    private void setupSimplePreferencesScreen() {
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_connect);

        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_general);


        twitPref = (CheckBoxPreference) findPreference("twit_checkbox");
        twitPref.setChecked(TwitterLogin.getTwitterLoginCheck());
        twitPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                if (TwitterLogin.getTwitterLoginCheck() != true) {
                    TwitterLogin twitterLogin = new TwitterLogin(SettingsActivity.this);
                    twitterLogin.setTwitterLogin();
                } else {
                    TwitterLogin twitterLogin = new TwitterLogin(SettingsActivity.this);
                    twitterLogin.removeAccessToken();
                }
                refresh();
                return true;
            }
        });

        twitPref.setIcon(R.drawable.twitterlogo);

        igPref = (CheckBoxPreference) findPreference("ig_checkbox");
        igPref.setChecked(mApp.hasAccessToken());
        igPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                connectToInstagram();
                return true;
            }
        });

        igPref.setIcon(R.drawable.instagramlogo);

        Preference prefereces = findPreference("info");
        prefereces.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
                return false;
            }
        });

        Preference prefereces2 = findPreference("share");
        prefereces2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I just found out about Bando. You have to check it out! Bandotheapp.com");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return false;
            }
        });
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void connectToInstagram() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    SettingsActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    mApp.resetAccessToken();
                                    Toast.makeText(getApplicationContext(), "Connected to IG! as " + mApp.getUserName(), Toast.LENGTH_LONG).show();
                                    refresh();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    refresh();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void refresh() {
        twitPref.setChecked(TwitterLogin.getTwitterLoginCheck());
        igPref.setChecked(mApp.hasAccessToken());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        switch (key) {
            case "pref_key_auto_delete":
                boolean notificationStatus = SP.getBoolean(key, false);
                Log.d("stat", String.valueOf(notificationStatus));
                break;

            case "pref_key_notification_list":
                String downloadType = SP.getString(key, "n/a");
                Log.d("stat", String.valueOf(downloadType));
                break;
        }
    }
}
