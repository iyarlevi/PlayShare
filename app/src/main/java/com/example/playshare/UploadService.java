package com.example.playshare;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class UploadService extends Service {

    private Handler handler;
    private Runnable runnable;
    private static final int INTERVAL = 7000; // 7 seconds

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Your periodic task
                uploadMessage();

                // Schedule the task again
                handler.postDelayed(this, INTERVAL);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable); // Start the periodic task
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop the periodic task
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void uploadMessage() {
        // Logic to upload message
        Intent intent = new Intent(this, MainActivity.class);
        NotificationHelper.showNotification(this, "Upload Service", "Hello, this is your message!", intent);
    }
}
