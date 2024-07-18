package com.example.playshare.Components;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;

import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.LoginActivity;
import com.example.playshare.R;
import com.example.playshare.SettingsActivity;
import com.example.playshare.StatsActivity;

public class TopAppBarMenuListener implements OnMenuItemClickListener {
    private final Context context;

    public TopAppBarMenuListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onMenuItemClick(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            Intent intent = new Intent(context, SettingsActivity.class);
            context.startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.logout) {
            FirebaseConnector.signOut();
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.showStats) {
            Intent intent = new Intent(context, StatsActivity.class);
            context.startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.editProfile) {
            Intent intent = new Intent(context, SettingsActivity.class);
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}
