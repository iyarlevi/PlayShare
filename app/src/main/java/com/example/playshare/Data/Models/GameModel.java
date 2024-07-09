package com.example.playshare.Data.Models;

import com.example.playshare.Data.Enums.GameLayoutEnum;
import com.example.playshare.Data.Enums.GameTypeEnum;
import com.example.playshare.Data.Enums.PlayLevelEnum;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;

public class GameModel {
    private GameTypeEnum _type;
    private LatLng _location;
    private GameLayoutEnum _layout;
    private DocumentReference _creatorReference;
    private PlayLevelEnum _level;

    public GameModel(GameTypeEnum type, LatLng location, GameLayoutEnum layout, DocumentReference creatorReference, PlayLevelEnum level) {
        _type = type;
        _location = location;
        _layout = layout;
        _creatorReference = creatorReference;
        _level = level;
    }

    public GameTypeEnum getType() {
        return _type;
    }

    public void setType(GameTypeEnum type) {
        _type = type;
    }

    public LatLng getLocation() {
        return _location;
    }

    public void setLocation(LatLng location) {
        _location = location;
    }

    public GameLayoutEnum getLayout() {
        return _layout;
    }

    public int getLayoutIndex() {
        return _layout.ordinal();
    }

    public String getLayoutTitle() {
        return _layout.getTitle();
    }

    public void setLayout(GameLayoutEnum layout) {
        _layout = layout;
    }

    public void setLayout(int layoutIndex) {
        _layout = GameLayoutEnum.values()[layoutIndex];
    }

    public DocumentReference getCreatorReference() {
        return _creatorReference;
    }

    public void setCreatorReference(DocumentReference creatorReference) {
        _creatorReference = creatorReference;
    }

    public PlayLevelEnum getLevel() {
        return _level;
    }

    public int getLevelIndex() {
        return _level.ordinal();
    }

    public void setLevel(PlayLevelEnum level) {
        _level = level;
    }

    public void setLevel(int levelIndex) {
        _level = PlayLevelEnum.values()[levelIndex];
    }

}
