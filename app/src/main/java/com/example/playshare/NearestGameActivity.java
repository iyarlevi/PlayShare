package com.example.playshare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.example.playshare.Data.Models.GameModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NearestGameActivity extends BaseActivityClass {

    private static final String TAG = "NearestGameActivity";

    private TextView titleTextView;
    private TextView nearestGameTextView;
    private FireStoreConnector fireStoreConnector;
    private LatLng userLocation; // Assuming this is populated with user's current location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_game);
        Log.d(TAG, "onCreate: Activity created");

        titleTextView = findViewById(R.id.titleTextView); // Initialize titleTextView
        nearestGameTextView = findViewById(R.id.nearestGameTextView);

        fireStoreConnector = FireStoreConnector.getInstance();

        // Check for user location permission and fetch nearest games
        if (checkLocationPermission()) {
            fetchUserLocation();
        } else {
            requestLocationPermission();
        }
    }

    private void fetchUserLocation() {
        Log.d(TAG, "fetchUserLocation: Fetching user location");
        // Here you would implement your logic to fetch the user's location
        // For demonstration purposes, let's assume you have a method to get user's location
        getUserLocation();
    }

    private void getUserLocation() {
        Log.d(TAG, "getUserLocation: Getting user location from Firestore");
        fireStoreConnector.getDocuments(CollectionsEnum.USERS.getCollectionName(),
                documents -> {
                    Log.d(TAG, "getUserLocation: Documents received successfully");
                    // Assuming you have a method to get user's location from documents
                    userLocation = getUserLocationFromDocuments(documents);
                    if (userLocation != null) {
                        fetchNearestGames();
                    } else {
                        Log.e(TAG, "getUserLocation: User location is null or not found in documents");
                        Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show();
                        // Set a placeholder or default location
                        userLocation = new LatLng(0, 0); // Set to a non-real location (e.g., 0, 0)
                        fetchNearestGames(); // Proceed with fetching games with placeholder location
                    }
                },
                e -> {
                    Log.e(TAG, "getUserLocation: Error fetching user location: " + e.getMessage());
                    Toast.makeText(this, "Error fetching user location", Toast.LENGTH_SHORT).show();
                }
        );
    }

    // Method to get user's location from documents (assuming basic implementation)
    private LatLng getUserLocationFromDocuments(List<Map<String, Object>> documents) {
        if (documents.isEmpty()) {
            Log.w(TAG, "getUserLocationFromDocuments: No documents found");
            return null;
        }

        // Assuming user's location is stored in a field called "location"
        // and it is a LatLng object
        Map<String, Object> document = documents.get(0);
        if (document.containsKey("location")) {
            Object locationObj = document.get("location");
            if (locationObj instanceof LatLng) {
                LatLng location = (LatLng) locationObj;
                Log.d(TAG, "getUserLocationFromDocuments: User location found: " + location.toString());
                return location;
            } else {
                Log.w(TAG, "getUserLocationFromDocuments: 'location' field is not of type LatLng");
                return null;
            }
        } else {
            Log.w(TAG, "getUserLocationFromDocuments: 'location' field not found in document");
            return null;
        }
    }

    private void fetchNearestGames() {
        Log.d(TAG, "fetchNearestGames: Fetching nearest games");
        if (userLocation == null) {
            Log.e(TAG, "fetchNearestGames: User location is null");
            Toast.makeText(this, "User location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch games from Firestore
        fireStoreConnector.getDocuments(CollectionsEnum.GAMES.getCollectionName(),
                documents -> {
                    Log.d(TAG, "fetchNearestGames: Games fetched successfully");
                    List<GameModel> games = new ArrayList<>();
                    for (int i = 0; i < documents.size(); i++) {
                        GameModel game = new GameModel(documents.get(i));
                        games.add(game);
                    }
                    displayNearestGame(games);
                },
                e -> {
                    Log.e(TAG, "fetchNearestGames: Error fetching games: " + e.getMessage());
                    Toast.makeText(this, "Error fetching games", Toast.LENGTH_SHORT).show();
                }
        );
    }

    @SuppressLint("SetTextI18n")
    private void displayNearestGame(List<GameModel> games) {
        // Assuming you have a method to calculate nearest game based on userLocation
        GameModel nearestGame = calculateNearestGame(games);

        if (nearestGame != null) {
            // Calculate distance with two decimal places
            double distanceMeters = calculateDistance(userLocation, nearestGame.getLocation());
            @SuppressLint("DefaultLocale") String distanceFormatted = String.format("%.2f", distanceMeters);

            titleTextView.setText("Nearest Game");
            nearestGameTextView.setText("Type: " + nearestGame.getType().toString() + "\n" +
                    "Distance: " + distanceFormatted + " meters\n" +
                    "Layout: " + nearestGame.getLayout().getTitle() + "\n" +
                    "Level: " + nearestGame.getLevel().toString());
        } else {
            titleTextView.setText("No Nearest Game Found");
            nearestGameTextView.setText("");
        }
    }


    // Method to calculate nearest game (assuming basic distance calculation)
    private GameModel calculateNearestGame(List<GameModel> games) {
        Log.d(TAG, "calculateNearestGame: Calculating nearest game");
        if (games.isEmpty()) {
            Log.d(TAG, "calculateNearestGame: No games available");
            return null;
        }

        GameModel nearestGame = games.get(0);
        double minDistance = calculateDistance(userLocation, nearestGame.getLocation());

        for (int i = 1; i < games.size(); i++) {
            GameModel currentGame = games.get(i);
            double distance = calculateDistance(userLocation, currentGame.getLocation());
            if (distance < minDistance) {
                nearestGame = currentGame;
                minDistance = distance;
            }
        }

        Log.d(TAG, "calculateNearestGame: Nearest game calculated: " + nearestGame.toString());
        return nearestGame;
    }

    // Method to calculate distance between two LatLng points (using Haversine formula for simplicity)
    private double calculateDistance(LatLng start, LatLng end) {
        double lat1 = start.latitude;
        double lon1 = start.longitude;
        double lat2 = end.latitude;
        double lon2 = end.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000; // Convert miles to meters

        return dist;
    }

    // Example methods for handling location permission (replace with your actual implementation)
    private boolean checkLocationPermission() {
        // Check if location permission is granted
        return true; // Replace with actual logic
    }

    private void requestLocationPermission() {
        // Request location permission
        // Example: ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
    }
}
