package com.example.playshare.Data.Enums;

import android.content.res.Resources;

import com.example.playshare.R;

public enum GameLayoutEnum {
    TEN_VS_TEN(Resources.getSystem().getString(R.string.ten_game)),
    FIVE_VS_FIVE(Resources.getSystem().getString(R.string.five_game)),
    THREE_VS_THREE(Resources.getSystem().getString(R.string.three_game)),
    TWO_VS_TWO(Resources.getSystem().getString(R.string.two_game)),
    ONE_VS_ONE(Resources.getSystem().getString(R.string.one_game));

    private final String _title;

    GameLayoutEnum(String title) {
        _title = title;
    }

    public String getTitle() {
        return _title;
    }

    public static GameLayoutEnum getEnum(String value) {
        for (GameLayoutEnum v : values())
            if (v.getTitle().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
