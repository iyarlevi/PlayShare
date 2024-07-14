package com.example.playshare.Data.Models;

import com.example.playshare.Data.Enums.GameLayoutEnum;
import com.example.playshare.Data.Enums.GameTypeEnum;
import com.example.playshare.Data.Enums.PlayLevelEnum;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class GameModel {
    private GameTypeEnum _type;
    private LatLng _location;
    private GameLayoutEnum _layout;
    private String _creatorReference;
    private PlayLevelEnum _level;

    public GameModel(Map<String, Object> firestoreDocument) {
        if (firestoreDocument == null) {
            return;
        }
        _type = GameTypeEnum.valueOf((String) firestoreDocument.get("type"));
        Object locationObject = firestoreDocument.get("location");
        if (locationObject instanceof Map) {
            Map<?, ?> locationMap = (Map<?, ?>) locationObject;
            if (locationMap.containsKey("latitude") && locationMap.containsKey("longitude")) {
                Object latObj = locationMap.get("latitude");
                Object lngObj = locationMap.get("longitude");
                if (latObj instanceof Double && lngObj instanceof Double) {
                    double latitude = (Double) latObj;
                    double longitude = (Double) lngObj;
                    _location = new LatLng(latitude, longitude);
                }
            }
        }
        _layout = GameLayoutEnum.valueOf((String) firestoreDocument.get("layout"));
        _creatorReference = (String) firestoreDocument.get("creatorReference");
        _level = PlayLevelEnum.valueOf((String) firestoreDocument.get("level"));
    }

    public GameModel(GameTypeEnum type, LatLng location, GameLayoutEnum layout, String creatorReference, PlayLevelEnum level) {
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

    public String getCreatorReference() {
        return _creatorReference;
    }

    public void setCreatorReference(String creatorReference) {
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

    public Map<String, Object> MappingForFirebase() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("type", _type.toString());
        res.put("location", _location);
        res.put("layout", _layout.toString());
        res.put("creatorReference", _creatorReference);
        res.put("level", _level.toString());
        return res;
    }

}
