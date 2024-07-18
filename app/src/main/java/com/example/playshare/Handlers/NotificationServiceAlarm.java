package com.example.playshare.Handlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.playshare.Broadcasts.NotificationServiceBroadcast;

public class NotificationServiceAlarm {
    private final AlarmManager alarmManager;
    private final Context context;

    public NotificationServiceAlarm(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm() {
        int interval = 1000 * 60 * 60 * 2; // 2 hours
//        int interval = 60000; // 1 minute for testing
        Intent intent = new Intent(context, NotificationServiceBroadcast.class);
        intent.setAction("NOTIFICATION_SERVICE");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(context, NotificationServiceBroadcast.class);
        intent.setAction("NOTIFICATION_SERVICE");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pendingIntent != null)
            alarmManager.cancel(pendingIntent);
    }
}
