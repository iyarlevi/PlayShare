// StatsActivity.java
package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    private TextView livePlayersTextView;
    private TextView basketballPlayersTextView;
    private TextView soccerPlayersTextView;
    private Button mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        livePlayersTextView = findViewById(R.id.livePlayersTextView);
        basketballPlayersTextView = findViewById(R.id.basketballPlayersTextView);
        soccerPlayersTextView = findViewById(R.id.soccerPlayersTextView);
        mapButton = findViewById(R.id.mapButton);

        // Set up a dummy onClick listener for the map button
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MapActivity when the button is clicked
                startActivity(new Intent(StatsActivity.this, MapActivity.class));
            }
        });

        // Update UI with statistics data (if available)
        // Replace the following lines with your actual statistics data retrieval logic
        livePlayersTextView.setText("Live Players: 10");
        basketballPlayersTextView.setText("Basketball Players: 5");
        soccerPlayersTextView.setText("Soccer Players: 5");
    }
}
