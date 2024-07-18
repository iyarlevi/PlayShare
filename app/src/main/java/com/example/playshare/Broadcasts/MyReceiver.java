package com.example.playshare.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.example.playshare.Components.LowBatteryDialog;

public class MyReceiver extends BroadcastReceiver {

    private LowBatteryDialog lowBatteryDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                handleBatteryChanged(context, intent);
            }
        }
    }

    private void handleBatteryChanged(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean isPlugged = (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);

        if (level <= 15 && !isPlugged) {
            if (lowBatteryDialog == null) {
                lowBatteryDialog = new LowBatteryDialog(context);

                // Check if battery level is less than or equal to 15% and not connected to charger
                lowBatteryDialog.show();
            }
        } else if (lowBatteryDialog != null) {
            lowBatteryDialog.dismiss();
            lowBatteryDialog = null;
        }
    }
}
