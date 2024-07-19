package com.example.playshare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.playshare.Components.BottomNavigator;
import com.example.playshare.Components.TopAppBarMenuListener;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.example.playshare.Data.Models.GameModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends BaseActivityClass implements
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ArrayList<LatLng> mapPath;
    private LatLng myLocation;
    private final List<GameModel> gamesArray = new ArrayList<>();
    private final Map<Marker, GameModel> gameMarkers = new HashMap<>();
    private Marker userLocationMarker;
    private boolean isCameraMoved = false, isInRoute = false;
    private final FireStoreConnector fireStoreConnector = FireStoreConnector.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseConnector.getCurrentUser() == null) {
            // deal with the case where the user is not logged in and see notification to here
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_map);

        //define bottom navigation:
        NavigationBarView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        BottomNavigator bottomNavigator = new BottomNavigator(this, R.id.navigation_map);
        bottomNavigationView.setOnItemSelectedListener(bottomNavigator);

        //define top app bar:
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarMenuListener topAppBarMenuListener = new TopAppBarMenuListener(this);
        topAppBar.setOnMenuItemClickListener(topAppBarMenuListener);

        // Setup Permission Launcher callback
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        startLocationUpdates();
                    } else {
                        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                    }
                });

        // Setup Google Map - when its ready, will call onMapReady();
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (map != null) {
            map.getMapAsync(this);
        }

        // Setup Location Manager
        locationManager = getSystemService(LocationManager.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            double lat = intent.getDoubleExtra("latitude", 0);
            double lng = intent.getDoubleExtra("longitude", 0);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15f));
            isCameraMoved = true;
        } else {
            // Default location - Azrieli Center
            myLocation = new LatLng(31.7692, 35.1937);
        }

        mapPath = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            } else {
                // ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
        } else {
            Toast.makeText(this, "GPS Disabled!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        updateDatabaseLocation();
        checkForGameDelete();
        refreshMap();
    }

    public void refreshMap() {
        googleMap.clear();
        addGamesToMap();

        // Add marker for user's current location
        userLocationMarker = googleMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location"));
        if (!isCameraMoved && !isInRoute) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f));
            isCameraMoved = true;
        }

        // draw path
        if (isInRoute) {
            mapPath.add(myLocation);
            googleMap.addPolyline(new PolylineOptions().addAll(mapPath).color(Color.BLUE).width(10f));
        }

        // Set click listener for the markers
        googleMap.setOnMarkerClickListener(marker -> {
            if (marker.equals(userLocationMarker)) {
                showUserDetails();
                return true;
            }
            GameModel game = gameMarkers.get(marker);
            if (game != null) {
                showGameDetails(game);
                return true;
            }
            return false;
        });
    }

    private void showUserDetails() {
        // Fetch the current user's details from Firestore
        String userId = FirebaseConnector.getCurrentUser().getUid();
        fireStoreConnector.getDocument(CollectionsEnum.USERS.getCollectionName(),
                userId,
                document -> {
                    // Inflate the dialog layout
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View dialogView = inflater.inflate(R.layout.dialog_user_details, null);

                    // Set the user's details in the dialog
                    TextView tvUserNickname = dialogView.findViewById(R.id.tvUserNickname);
                    TextView tvUserAge = dialogView.findViewById(R.id.tvUserAge);
                    Button btnClose = dialogView.findViewById(R.id.btnClose);

                    tvUserNickname.setText(getString(R.string.users_nickname, document.get("nickname")));
                    tvUserAge.setText(getString(R.string.users_age, document.get("age")));

                    // Create and show the dialog
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                    builder.setView(dialogView);
                    Dialog dialog = builder.create();
                    dialog.show();
                    btnClose.setOnClickListener(v -> {
                        // Dismiss the dialog
                        dialog.dismiss();
                    });
                },
                e -> {
                    Log.e("MainActivity", ">>> Error: " + e.getMessage());
                    Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                }
        );
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("MainActivity", ">>> onProviderEnabled: " + provider);
        Toast.makeText(this, "GPS Enabled!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("MainActivity", ">>> onProviderDisabled: " + provider);
        Toast.makeText(this, "GPS Disabled!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // todo add to string xml
        Toast.makeText(this, getString(R.string.create_new_game_hint), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(getString(R.string.create_new_game));
        builder.setMessage(getString(R.string.create_new_game_question));
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            Intent intent = new Intent(this, NewGameActivity.class);
            intent.putExtra("lat", latLng.latitude);
            intent.putExtra("lng", latLng.longitude);
            startActivity(intent);
        });
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void addGamesToMap() {
        gamesArray.clear();
        fireStoreConnector.getDocuments(CollectionsEnum.GAMES.getCollectionName(),
                documents -> {
                    for (int i = 0; i < documents.size(); i++) {
                        Map<String, Object> currentDoc = documents.get(i);
                        GameModel game = new GameModel(currentDoc);
                        gamesArray.add(game);
                    }
                    setupGamesMarkers(gamesArray);
                },
                e -> {
                    Log.e("MainActivity", ">>> Error: " + e.getMessage());
                    Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                }
        );
    }

    private void setupGamesMarkers(List<GameModel> games) {
        for (GameModel game : games) {
            Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), game.getType().getIcon());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(iconBitmap);
            Marker gameMarker = googleMap.addMarker(new MarkerOptions().position(game.getLocation()).icon(icon));
            gameMarkers.put(gameMarker, game);
        }
    }

    private void showGameDetails(GameModel game) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        fireStoreConnector.getDocument(CollectionsEnum.USERS.getCollectionName(),
                game.getCreatorReference(),
                document -> {
                    builder.setTitle("Game Details");
                    builder.setMessage("Game Type: " + game.getType().name() + "\n" +
                            "Game Layout: " + game.getLayout().getTitle() + "\n" +
                            "Game Level: " + game.getLevel().name() + "\n" +
                            "Creator: " + document.get("nickname"));
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    if (game.getCreatorReference().equals(FirebaseConnector.getCurrentUser().getUid())) {
                        builder.setNeutralButton("Delete", (dialog, which) -> handleGameDeletion(dialog, FirebaseConnector.getCurrentUser().getUid(), (String) document.get("currentGame")));
                    }
                    //todo optional: add navigation to the game
//                   optional!!! builder.setNegativeButton("Navigate", (dialog, which) -> {
//                    });
                    builder.create().show();
                },
                e -> {
                    Log.e("MainActivity", ">>> Error: " + e.getMessage());
                    Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                }
        );
    }

    private void handleGameDeletion(DialogInterface dialog, String userId, String gameId) {
        fireStoreConnector.deleteDocument(
                CollectionsEnum.GAMES.getCollectionName(),
                gameId,
                success -> {
                    Toast.makeText(this, getString(R.string.game_deleted), Toast.LENGTH_LONG).show();
                    HashMap<String, Object> userGame = new HashMap<>();
                    userGame.put("currentGame", null);
                    fireStoreConnector.updateDocument(
                            CollectionsEnum.USERS.getCollectionName(),
                            userId,
                            userGame,
                            success1 -> {
                                Log.d("MainActivity", ">>> Game removed from user's games list");
                                refreshMap();
                                dialog.dismiss();
                            },
                            error1 -> Log.e("MainActivity", ">>> Error: cannot remove game from user's games list")
                    );
                },
                error -> Toast.makeText(this, getString(R.string.game_deleted_failed), Toast.LENGTH_LONG).show()
        );

    }

    private void updateDatabaseLocation() {
        Map<String, Object> userLocation = new HashMap<>();
        userLocation.put("location", myLocation);
        fireStoreConnector.updateDocument(CollectionsEnum.USERS.getCollectionName(),
                FirebaseConnector.getCurrentUser().getUid(),
                userLocation,
                success -> Log.d("MainActivity", ">>> User location updated successfully"),
                error -> Log.e("MainActivity", ">>> Error: " + error.getMessage())
        );
    }

    private void checkForGameDelete() {
        fireStoreConnector.getDocument(CollectionsEnum.USERS.getCollectionName(),
                FirebaseConnector.getCurrentUser().getUid(),
                document -> {
                    if (document.get("currentGame") == null) {
                        return;
                    }
                    String gameId = (String) document.get("currentGame");
                    float[] distance = new float[1];
                    fireStoreConnector.getDocument(
                            CollectionsEnum.GAMES.getCollectionName(),
                            gameId,
                            gameDocument -> {
                                GameModel game = new GameModel(gameDocument);
                                android.location.Location.distanceBetween(
                                        myLocation.latitude,
                                        myLocation.longitude,
                                        game.getLocation().latitude,
                                        game.getLocation().longitude,
                                        distance
                                );
                                if (distance[0] > 1000) {
                                    handleGameDeletion(new MaterialAlertDialogBuilder(this).create(), FirebaseConnector.getCurrentUser().getUid(), gameId);
                                }
                            }
                            ,
                            e -> Log.e("MainActivity", ">>> Error: " + e.getMessage())
                    );
                },
                e -> Log.e("MainActivity", ">>> Error: " + e.getMessage())
        );

    }

}
