package com.example.playshare.Data.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class UserModel {
    private int _age;
    private String _nickname;
    private LatLng _location;
    private ArrayList<String> _preferences;

    public UserModel(int age, String nickname, LatLng location, ArrayList<String> preferences) {
        _age = age;
        _nickname = nickname;
        _location = location;
        _preferences = preferences;
    }

    public int getAge() {
        return _age;
    }

    public void setAge(int age) {
        _age = age;
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
}
