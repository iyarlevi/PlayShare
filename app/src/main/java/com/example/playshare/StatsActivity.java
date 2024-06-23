package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class StatsActivity extends AppCompatActivity {

    private static final String TAG = "StatsActivity";
    private static final String STATS_COLLECTION = "stats"; // Replace with your collection name

    private TextView livePlayersTextView;
    private TextView basketballPlayersTextView;
    private TextView soccerPlayersTextView;

    private FirebaseFirestore db;
    private ListenerRegistration statsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        livePlayersTextView = findViewById(R.id.livePlayersTextView);
        basketballPlayersTextView = findViewById(R.id.basketballPlayersTextView);
        soccerPlayersTextView = findViewById(R.id.soccerPlayersTextView);
        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(v -> startActivity(new Intent(StatsActivity.this, MapActivity.class)));

        db = FirebaseFirestore.getInstance();
        setupStatsListener();
    }

    private void setupStatsListener() {
        statsListener = db.collection(STATS_COLLECTION)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error fetching stats", error);
                        showToast("Error fetching stats: " + error.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                            if (snapshot.exists()) {
                                Log.d(TAG, "Snapshot data: " + snapshot.getData());
                                updateUI(snapshot);
                            } else {
                                showToast("Document does not exist");
                            }
                        }
                    } else {
                        showToast("No data available");
                    }
                });
    }

    private void updateUI(DocumentSnapshot snapshot) {
        try {
            String livePlayersStr = snapshot.getString("livePlayers");
            String basketballPlayersStr = snapshot.getString("basketballPlayers");
            String soccerPlayersStr = snapshot.getString("soccerPlayers");

            Log.d(TAG, "livePlayers: " + livePlayersStr + ", basketballPlayers: " + basketballPlayersStr + ", soccerPlayers: " + soccerPlayersStr);

            runOnUiThread(() -> {
                String live_players_str = livePlayersStr != null ? livePlayersStr : "N/A";
                livePlayersTextView.setText(getString(R.string.live_players, live_players_str));
                String basketball_players_str = basketballPlayersStr != null ? basketballPlayersStr : "N/A";
                basketballPlayersTextView.setText(getString(R.string.basketball_players, basketball_players_str));
                String soccer_players_str = soccerPlayersStr != null ? soccerPlayersStr : "N/A";
                soccerPlayersTextView.setText(getString(R.string.soccer_players, soccer_players_str));

            });

        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
            showToast("Error updating UI: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(StatsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (statsListener != null) {
            statsListener.remove();
        }
    }
}
