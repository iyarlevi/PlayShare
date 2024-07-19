package com.example.playshare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.playshare.Components.BottomNavigator;
import com.example.playshare.Components.ProgressDialog;
import com.example.playshare.Components.TopAppBarMenuListener;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends BaseActivityClass {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ProgressDialog progressDialog;
    private final FireStoreConnector database = FireStoreConnector.getInstance();
    private RequestQueue volleyQueue;
    private ImageView profileImageView;
    private TextView nameTextView, heightTextView, ageTextView, preferencesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTextView = findViewById(R.id.nameTextView);
        heightTextView = findViewById(R.id.heightTextView);
        ageTextView = findViewById(R.id.ageTextView);
        preferencesTextView = findViewById(R.id.preferencesTextView);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        //define bottom navigation:
        NavigationBarView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        BottomNavigator bottomNavigator = new BottomNavigator(this, R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(bottomNavigator);
        volleyQueue = Volley.newRequestQueue(this);
        profileImageView = findViewById(R.id.imageView);

        getUserData();
        requestLocationPermission();

        //define top app bar:
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarMenuListener topAppBarMenuListener = new TopAppBarMenuListener(this);
        topAppBar.setOnMenuItemClickListener(topAppBarMenuListener);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getUserData() {
        database.getDocument(CollectionsEnum.USERS.getCollectionName(),
                FirebaseConnector.getCurrentUser().getUid(),
                documentMap -> {
                    Log.d("MainActivity", "getUserData: " + documentMap);
                    if (documentMap == null || documentMap.isEmpty()) {
                        Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (documentMap.get("nickname") != null)
                        nameTextView.setText((String) documentMap.get("nickname"));
                    Object heightObj = documentMap.get("height");
                    if (heightObj != null) {
                        double height = Double.parseDouble(heightObj.toString());
                        if (height > 0) {
                            heightTextView.setText(String.valueOf(height));
                        }
                    }
                    Object ageObj = documentMap.get("age");
                    if (ageObj != null) {
                        int age = Integer.parseInt(ageObj.toString());
                        if (age > 0) {
                            ageTextView.setText(String.valueOf(age));
                        }
                    }
                    Object preferences = documentMap.get("preferences");
                    if (preferences != null) {
                        preferencesTextView.setText(preferences.toString());
                    }

                    Object imageUrl = documentMap.get("imageUrl");
                    if (imageUrl != null) {
                        String url = imageUrl.toString();
                        if (!url.isEmpty()) {
                            ImageRequest imageRequest = new ImageRequest(url,
                                    response -> {
                                        profileImageView.setImageBitmap(response);
                                        progressDialog.dismiss();
                                    },
                                    0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                                    error -> {
                                        Log.d("SettingsActivity", ">>> loadSavedSettings: " + error.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(this, getString(R.string.failed_load_image), Toast.LENGTH_SHORT).show();
                                    });
                            volleyQueue.add(imageRequest);
                        } else {
                            progressDialog.dismiss();
                        }
                    } else {
                        progressDialog.dismiss();
                    }
                },
                e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_SHORT).show();
                }
        );
    }
}
