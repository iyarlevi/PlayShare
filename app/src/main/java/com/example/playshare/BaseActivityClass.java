package com.example.playshare;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playshare.Components.MyReceiver;

public class BaseActivityClass extends AppCompatActivity {
    private BroadcastReceiver receiver;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new MyReceiver();
        // Initialize the Notification Channel (only needed once)
//        NotificationHelper.createNotificationChannel(this);
//
//        // Start the upload service
//        serviceIntent = new Intent(this, UploadService.class);
//        startService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(serviceIntent);
    }
}
