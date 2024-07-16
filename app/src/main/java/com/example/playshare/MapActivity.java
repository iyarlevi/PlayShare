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

    private final FireStoreConnector fireStoreConnector = FireStoreConnector.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //define bottom navigation:
        NavigationBarView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        BottomNavigator bottomNavigator = new BottomNavigator(this, R.id.navigation_map);
        bottomNavigationView.setOnItemSelectedListener(bottomNavigator);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Setup Permission Launcher callback
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        startLocationUpdates();
                    } else {
                        Toast.makeText(this, "Permission NOT Granted!", Toast.LENGTH_LONG).show();
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

        LatLng azrieli = new LatLng(31.7692, 35.1937);
        googleMap.addMarker(new MarkerOptions().position(azrieli).title("Azrieli College"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(azrieli, 15f));
        addGamesToMap();

        mapPath = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "App needs to access Device's Location!", Toast.LENGTH_LONG).show();
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
        Log.d("MainActivity", ">>> onLocationChanged: " + location);

        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation = pos; // Update the user's current location

        googleMap.clear();
        addGamesToMap();

        // Add marker for user's current location
        userLocationMarker = googleMap.addMarker(new MarkerOptions().position(pos).title("Your Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));

        // draw path
        mapPath.add(pos);
        googleMap.addPolyline(new PolylineOptions().addAll(mapPath).color(Color.BLUE).width(10f));

        // Set click listener for the markers
        googleMap.setOnMarkerClickListener(marker -> {
            if (marker.equals(userLocationMarker)) {
                showUserLocationDetails();
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

    private void showUserLocationDetails() {
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

                    tvUserNickname.setText("Nickname: " + document.get("nickname"));
                    tvUserAge.setText("Age: " + document.get("age"));

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
                    Toast.makeText(this, "Error: cannot fetch user details", Toast.LENGTH_LONG).show();
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
        Log.d("MainActivity", ">>> onMapClick: " + latLng);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Create new game");
        builder.setMessage("Do you want to create a new game here?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, NewGameActivity.class);
            intent.putExtra("lat", latLng.latitude);
            intent.putExtra("lng", latLng.longitude);
            startActivity(intent);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void addGamesToMap() {
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
                    Toast.makeText(this, "Error: cannot fetch games", Toast.LENGTH_LONG).show();
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
                        builder.setNeutralButton("Delete", (dialog, which) -> {
                            handleGameDeletion(dialog, FirebaseConnector.getCurrentUser().getUid(), (String) document.get("currentGame"));
                        });
                    }
                    //todo: add navigation to the game
//                    builder.setNegativeButton("Navigate", (dialog, which) -> {
//                    });
                    builder.create().show();
                },
                e -> {
                    Log.e("MainActivity", ">>> Error: " + e.getMessage());
                    Toast.makeText(this, "Error: cannot fetch game creator", Toast.LENGTH_LONG).show();
                }
        );
    }

    private void handleGameDeletion(DialogInterface dialog, String userId, String gameId) {
        fireStoreConnector.deleteDocument(
                CollectionsEnum.GAMES.getCollectionName(),
                gameId,
                success -> {
                    Toast.makeText(this, "Game deleted successfully", Toast.LENGTH_LONG).show();
                    HashMap<String, Object> userGame = new HashMap<>();
                    userGame.put("currentGame", null);
                    fireStoreConnector.updateDocument(
                            CollectionsEnum.USERS.getCollectionName(),
                            userId,
                            userGame,
                            success1 -> {
                                Log.d("MainActivity", ">>> Game removed from user's games list");
                                dialog.dismiss();
                            },
                            error1 -> {
                                Log.e("MainActivity", ">>> Error: cannot remove game from user's games list");
                            }
                    );
                },
                error -> {
                    Toast.makeText(this, "Error: cannot delete game", Toast.LENGTH_LONG).show();
                }
        );

    }
}
