package com.example.playshare.Data.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private int _age;
    private String _imageUrl;
    private String _nickname;
    private Double _height;
    private LatLng _location;
    private ArrayList<String> _preferences;
    private String _timeStamp;

    public UserModel(int age, double height, String nickname, LatLng location, ArrayList<String> preferences, String imageUrl) {
        _age = -1;
        if (age <= 120)
            _age = age;
        _height = 0.0;
        if (height <= 3) {
            _height = height;
        }
        _nickname = nickname;
        _preferences = preferences;
        _location = location;
        _timeStamp = Calendar.getInstance().getTime().toString();
        if (imageUrl != null)
            _imageUrl = imageUrl;
    }

    public UserModel(Map<String, Object> document) {
        _age = -1;
        _height = 0.0;
        Object ageObject = document.get("age");
        if (ageObject instanceof Long) {
            _age = ((Long) ageObject).intValue();
        } else if (ageObject instanceof Integer) {
            _age = (Integer) ageObject;
        }
        Object heightObject = document.get("height");
        if (heightObject instanceof Double) {
            _height = (Double) heightObject;
        }
        if (document.containsKey("nickname"))
            _nickname = (String) document.get("nickname");
        Object locationObject = document.get("location");
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
        _timeStamp = (String) document.get("timeStamp");
        if (document.containsKey("imageUrl")) {
            _imageUrl = (String) document.get("imageUrl");
        }
//        _preferences = (ArrayList<String>) document.get("preferences");
        Object preferences = document.get("preferences");
        if (preferences instanceof Map) {
            Map<?, ?> preferencesMap = (Map<?, ?>) preferences;
            _preferences = new ArrayList<>();
            for (Map.Entry<?, ?> entry : preferencesMap.entrySet()) {
                _preferences.add((String) entry.getValue());
            }
        }
    }

    public int getAge() {
        return _age;
    }

    public void setAge(int age) {
        _age = age;
    }

    public Double getHeight() {
        return _height;
    }

    public void setHeight(Double height) {
        _height = height;
    }

    public String getNickname() {
        return _nickname;
    }

    public void setNickname(String nickname) {
        _nickname = nickname;
    }

    public LatLng getLocation() {
        return _location;
    }

    public void setLocation(LatLng location) {
        _location = location;
    }

    public ArrayList<String> getPreferences() {
        return _preferences;
    }

    public void setPreferences(ArrayList<String> preferences) {
        _preferences = preferences;
    }

    public void addPreference(String preference) {
        _preferences.add(preference);
    }

    public void removePreference(String preference) {
        _preferences.remove(preference);
    }

    public void clearPreferences() {
        _preferences.clear();
    }

    public String getTimeStamp() {
        return _timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        _timeStamp = timeStamp;
    }

    public String getImageUrl() {
        return _imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        _imageUrl = imageUrl;
    }

    public void removeImageUrl() {
        _imageUrl = null;
    }

    public Map<String, Object> MappingForFirebase() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("age", _age);
        res.put("height", _height);
        res.put("nickname", _nickname);
        res.put("location", _location);
        res.put("preferences", _preferences);
        res.put("timeStamp", _timeStamp);
        if (_imageUrl != null)
            res.put("imageUrl", _imageUrl);
        return res;
    }
}
