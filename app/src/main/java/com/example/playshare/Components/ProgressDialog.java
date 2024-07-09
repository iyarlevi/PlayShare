package com.example.playshare.Components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.playshare.R;

public class ProgressDialog extends Dialog {
    private ProgressBar progressBar;
    private final TextView progressText;

    public ProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_dialog);

        progressBar = findViewById(R.id.progressBar);
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
