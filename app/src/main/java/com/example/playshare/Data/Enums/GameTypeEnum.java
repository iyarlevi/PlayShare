package com.example.playshare.Data.Enums;

import androidx.annotation.DrawableRes;

import com.example.playshare.R;

public enum GameTypeEnum {
    SOCCER(R.drawable.bmp_soccer),
    BASKETBALL(R.drawable.bmp_basketball),
    TENNIS(R.drawable.bmp_tennis);

    private final int _icon;

    GameTypeEnum(@DrawableRes int icon) {
        _icon = icon;
    }

    public int getIcon() {
        return _icon;
    }
}
