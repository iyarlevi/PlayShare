package com.example.playshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Thread(() -> {
            try {
                Thread.sleep(3500); // Sleep for 3.5 seconds
            } catch (InterruptedException e) {
                if (e.getMessage() != null)
                    Log.e("SplashScreenActivity", e.getMessage());
                else
                    Log.e("SplashScreenActivity", "Unknown error occurred");
            }

            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }).start();
    }

}