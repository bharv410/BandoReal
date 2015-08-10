package com.bandotheapp.bando;

import android.app.Application;

import com.parse.Parse;

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

    }
}
