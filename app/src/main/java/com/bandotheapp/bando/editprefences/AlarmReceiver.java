package com.bandotheapp.bando.editprefences;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bandotheapp.bando.MainActivity;
import com.bandotheapp.bando.R;

import java.util.Random;

/**
 * Created by benjamin.harvey on 8/7/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("benmark", "recieved");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String notifs = sharedPreferences.getString("notifs", "1");
        if(notifs.equals("1"))
            showNotifWithPostOrNah(context, "Bando has new posts!");
    }

    private void showNotifWithPostOrNah(Context context, String text) {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        //building the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.bandologo)
                .setContentTitle(text)
                .setAutoCancel(true)
                .setTicker(text)
                .setColor(Color.RED);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, mBuilder.build());
    }
}
