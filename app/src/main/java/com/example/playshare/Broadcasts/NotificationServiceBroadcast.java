package com.example.playshare.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.playshare.Services.NotificationService;

public class NotificationServiceBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("NOTIFICATION_SERVICE")) {
            Log.d("NotificationServiceBroadcast", "onReceive: NotificationServiceBroadcast");
            Intent serviceIntent = new Intent(context, NotificationService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
