package com.example.playshare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static int notificationID = 0;
    private static final String CHANNEL_ID = "CHANNEL_ID1";
    private static final String CHANNEL_NAME = "CHANNEL_NAME1";

    // Method to initialize the Notification Channel (Should be called once, for example in your Application class)
    public static void createNotificationChannel(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);

        notificationManager.createNotificationChannel(notificationChannel);
    }

    // Method to show the Notification
    public static void showNotification(Context context, String title, String text, Intent intent) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo) // Replace this with the app's icon
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(notificationID++, notification);
    }
}

//  to use this code in the activity:
//
//  // Initialize the Notification Channel (only needed once)
//        NotificationHelper.createNotificationChannel(this);
//
//  // To show a notification
//        Intent intent = new Intent(this, MainActivity.class);
//        NotificationHelper.showNotification(this, "Your Title", "Your notification text", intent);
//    }