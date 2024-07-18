package com.example.playshare.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.playshare.MainActivity;
import com.example.playshare.NotificationHelper;

public class NotificationService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel("FOREGROUND_SERVICE_CHANNEL", "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, "FOREGROUND_SERVICE_CHANNEL")
                .setContentTitle("Search for games")
                .setContentText("Searching for games in the background...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
        startForeground(1, notification);

        NotificationHelper.createNotificationChannel(this);
        NotificationHelper.showNotification(this, "Your Title", "Your notification text", new Intent(this, MainActivity.class));
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
