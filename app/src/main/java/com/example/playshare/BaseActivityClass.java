package com.example.playshare;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playshare.Broadcasts.MyReceiver;
import com.example.playshare.Handlers.NotificationServiceAlarm;

public class BaseActivityClass extends AppCompatActivity {
    private BroadcastReceiver receiver;
    private NotificationServiceAlarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new MyReceiver();
        alarm = new NotificationServiceAlarm(this);
        alarm.setAlarm();
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
    }
}
