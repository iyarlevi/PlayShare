package com.example.playshare.Handlers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.playshare.Components.NotificationHelper;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.example.playshare.Data.Models.GameModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class LocationServiceHandler {
    private final FireStoreConnector database = FireStoreConnector.getInstance();
    private final Map<GameModel, Float> gamesDistanceMap = new HashMap<>();
    private Context context = null;

    public void getGameDistances(Context context) {
        if (context == null) {
            return;
        }
        this.context = context;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocationFromDB();
        }
    }

    private void getUserLocationFromDB() {
        database.getDocument(
                CollectionsEnum.USERS.getCollectionName(),
                FirebaseConnector.getCurrentUser().getUid(),
                documentMap ->
                {
                    if (documentMap == null || documentMap.isEmpty()) {
                        return;
                    }
                    Object locationObj = documentMap.get("location");
                    if (locationObj instanceof Map) {
                        Map<?, ?> locationMap = (Map<?, ?>) locationObj;
                        if (locationMap.containsKey("latitude") && locationMap.containsKey("longitude")) {
                            Object latObj = locationMap.get("latitude");
                            Object lngObj = locationMap.get("longitude");
                            if (latObj instanceof Double && lngObj instanceof Double) {
                                double latitude = (Double) latObj;
                                double longitude = (Double) lngObj;
                                LatLng userLocation = new LatLng(latitude, longitude);
                                getGamesLocationsDistances(userLocation);
                            }
                        }
                    }
                },
                e -> Log.e("LocationServiceHandler", "Error getting user location: " + e.getMessage())
        );
    }

    public void getGamesLocationsDistances(LatLng userLocation) {
        database.getDocuments(
                CollectionsEnum.GAMES.getCollectionName(),
                documents -> {
                    for (Map<String, Object> document : documents) {
                        GameModel game = new GameModel(document);
                        if (game.getLocation() != null) {
                            float[] distance = new float[1];
                            android.location.Location.distanceBetween(
                                    userLocation.latitude, userLocation.longitude,
                                    game.getLocation().latitude, game.getLocation().longitude,
                                    distance
                            );
                            gamesDistanceMap.put(game, distance[0]);
                        }
                    }
                    FindNearestGame();
                },
                e -> Log.e("LocationServiceHandler", "Error getting games locations: " + e.getMessage())
        );
    }

    private void FindNearestGame() {
        GameModel nearestGame = null;
        float nearestDistance = Float.MAX_VALUE;
        for (Map.Entry<GameModel, Float> entry : gamesDistanceMap.entrySet()) {
            if (entry.getValue() < nearestDistance) {
                nearestGame = entry.getKey();
                nearestDistance = entry.getValue();
            }
        }
        NotificationHelper.createNotificationChannel(context);
        if (nearestGame != null && nearestDistance < 1000) {
            Intent intent = new Intent(context, com.example.playshare.MapActivity.class);
            intent.putExtra("latitude", nearestGame.getLocation().latitude);
            intent.putExtra("longitude", nearestGame.getLocation().longitude);
            NotificationHelper.showNotification(context,
                    "Game Offer",
                    "Would you like to play " + nearestGame.getType().name() + "?",
                    intent
            );
            Intent serviceIntent = new Intent(context, com.example.playshare.Services.NotificationService.class);
            context.stopService(serviceIntent);
        } else {
            NotificationHelper.showNotification(context, "PlayShare", "Long time not seen...", new Intent(context, com.example.playshare.MainActivity.class));
            Intent serviceIntent = new Intent(context, com.example.playshare.Services.NotificationService.class);
            context.stopService(serviceIntent);
        }
    }
}
