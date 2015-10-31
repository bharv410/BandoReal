package com.bandotheapp.bando;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import classes.AnalyticsTrackers;
import com.bandotheapp.bando.comments.Comment;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class MyApplication extends Application {

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();



        //Install CustomActivityOnCrash
        CustomActivityOnCrash.install(this);


        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(Comment.class);

        Parse.initialize(this, "CKxEw3xAI4MS7l6DtC8p7MFZJHyL8fpkHoL0dmlo", "BxebY2wlJ6vvYUcBJPyp4Xd2pzCskMs86RHeo74K");

        ParseUser.enableRevocableSessionInBackground();

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                    ParsePush.subscribeInBackground("Giants");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
