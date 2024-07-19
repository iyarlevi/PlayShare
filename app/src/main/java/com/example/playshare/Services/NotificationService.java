package com.example.playshare.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Handlers.LocationServiceHandler;
import com.example.playshare.LoginActivity;
import com.example.playshare.Components.NotificationHelper;
import com.example.playshare.R;

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
                .setSmallIcon(R.drawable.ic_logo)
                .build();
        startForeground(1, notification);

        NotificationHelper.createNotificationChannel(this);

        if (FirebaseConnector.getCurrentUser() == null) {
            NotificationHelper.showNotification(this, "PlayShare", "Long time not seen..", new Intent(this, LoginActivity.class));
            stopSelf();
            return;
        }
        try {
            LocationServiceHandler locationServiceHandler = new LocationServiceHandler();
            locationServiceHandler.getGameDistances(this);
        } catch (Exception e) {
            NotificationHelper.showNotification(this, "PlayShare", "Long time not seen..", new Intent(this, LoginActivity.class));
            stopSelf();
        }
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
