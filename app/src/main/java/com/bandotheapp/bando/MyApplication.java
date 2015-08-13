package com.bandotheapp.bando;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Install CustomActivityOnCrash
        CustomActivityOnCrash.install(this);


        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "CKxEw3xAI4MS7l6DtC8p7MFZJHyL8fpkHoL0dmlo", "BxebY2wlJ6vvYUcBJPyp4Xd2pzCskMs86RHeo74K");

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
