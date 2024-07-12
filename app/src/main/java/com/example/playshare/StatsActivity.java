package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playshare.Components.BottomNavigator;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private static final String TAG = "StatsActivity";
    private TextView livePlayersTextView;
    private TextView basketballPlayersTextView;
    private TextView soccerPlayersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        livePlayersTextView = findViewById(R.id.livePlayersTextView);
        basketballPlayersTextView = findViewById(R.id.basketballPlayersTextView);
        soccerPlayersTextView = findViewById(R.id.soccerPlayersTextView);
        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(v -> startActivity(new Intent(StatsActivity.this, MapActivity.class)));

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        //define bottom navigation:
        NavigationBarView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_stats);
        BottomNavigator bottomNavigator = new BottomNavigator(this, R.id.navigation_stats);
        bottomNavigationView.setOnItemSelectedListener(bottomNavigator);

        // Set initial loading text
        livePlayersTextView.setText(getString(R.string.loading));
        basketballPlayersTextView.setText(getString(R.string.loading));
        soccerPlayersTextView.setText(getString(R.string.loading));
        FireStoreConnector firestore = FireStoreConnector.getInstance();

        // todo : get this directly from users data
        firestore.getDocuments(CollectionsEnum.STATS.getCollectionName(),
                documents -> {
                    Map<String, Object> document = documents.get(0);
                    livePlayersTextView.setText("Live players: " + document.get("livePlayers"));
                    basketballPlayersTextView.setText("Basketball Players: " + document.get("basketballPlayers"));
                    soccerPlayersTextView.setText("Soccer Players: " + document.get("soccerPlayers"));
                }, e -> {
                    Log.e(TAG, "Error getting stats: " + e.getMessage());
                    Toast.makeText(this, "Error getting stats: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
