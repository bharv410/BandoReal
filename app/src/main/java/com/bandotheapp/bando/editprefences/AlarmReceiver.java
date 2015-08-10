package com.bandotheapp.bando.editprefences;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
        showNotifWithPostOrNah(context, "Bando has new posts!");
    }

    private void showNotifWithPostOrNah(Context context, String text) {
        Intent deleteIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //building the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.bandologo)
                .setContentTitle(text)
                .setTicker(text)
                .setColor(Color.RED);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, mBuilder.build());
    }
}
