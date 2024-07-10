package com.example.playshare.Data.Enums;

public enum CollectionsEnum {
    USERS("Users"),
    GAMES("Games"),
    STATS("stats");

    private final String collectionName;

    CollectionsEnum(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }
}
