package com.example.playshare.Components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.example.playshare.R;

public class ProgressDialog extends Dialog {
    private final TextView progressText;

    public ProgressDialog(Context context) {
        this(context, R.color.colorPrimary);
    }

    public ProgressDialog(Context context, @ColorRes int backgroundColor) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_dialog);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(context.getColor(backgroundColor)));
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        progressText = findViewById(R.id.progressText);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setMessage(String message) {
        progressText.setText(message);
    }

    public void showLoading() {
        if (!isShowing()) {
            show();
        }
    }

    public void hideLoading() {
        if (isShowing()) {
            dismiss();
        }
    }
}
