package com.example.playshare.Data.Enums;

public enum GameLayoutEnum {
    TEN_VS_TEN("10 vs 10"),
    FIVE_VS_FIVE("5 vs 5"),
    THREE_VS_THREE("3 vs 3"),
    TWO_VS_TWO("2 vs 2"),
    ONE_VS_ONE("1 vs 1");

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
