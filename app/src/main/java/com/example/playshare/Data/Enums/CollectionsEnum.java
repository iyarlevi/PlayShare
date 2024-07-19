package com.example.playshare.Data.Enums;

public enum CollectionsEnum {
    USERS("Users"),
    GAMES("Games");

    private final String collectionName;

    CollectionsEnum(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }
}
