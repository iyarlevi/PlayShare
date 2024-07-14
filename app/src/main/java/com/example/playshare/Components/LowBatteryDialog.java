package com.example.playshare.Components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.example.playshare.R;

public class LowBatteryDialog extends Dialog {

    private Button dismissButton;

    public LowBatteryDialog(Context context) {
        this(context, R.color.colorSurface);
    }

    public LowBatteryDialog(Context context, @ColorRes int backgroundColor) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_low_battery);
        if (getWindow() != null) {
            ColorDrawable colorDrawable = new ColorDrawable(context.getColor(backgroundColor));
            colorDrawable.setAlpha(50);
            getWindow().setBackgroundDrawable(colorDrawable);
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        dismissButton = findViewById(R.id.buttonDismiss);
        dismissButton.setOnClickListener(v -> dismiss());
    }
}
