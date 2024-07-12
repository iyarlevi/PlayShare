package com.example.playshare.Components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.example.playshare.MainActivity;
import com.example.playshare.MapActivity;
import com.example.playshare.R;
import com.example.playshare.StatsActivity;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigator implements NavigationBarView.OnItemSelectedListener {
    private final Context context;
    private final Integer currentItemId;

    public BottomNavigator(Context context) {
        this.context = context;
        this.currentItemId = null;
    }

    public BottomNavigator(Context context, @IdRes int currentItemId) {
        this.context = context;
        this.currentItemId = currentItemId;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (currentItemId != null && itemId == currentItemId) {
            return true;
        }
        if (itemId == R.id.navigation_home) {
            Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
            return true;
        } else if (itemId == R.id.navigation_stats) {
            Intent intent = new Intent(context.getApplicationContext(), StatsActivity.class);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
            return true;
        } else if (itemId == R.id.navigation_map) {
            Intent intent = new Intent(context.getApplicationContext(), MapActivity.class);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
            return true;
        }
        return false;
    }
}
