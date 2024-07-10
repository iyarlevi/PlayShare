package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playshare.Data.Enums.GameLayoutEnum;
import com.example.playshare.Data.Enums.GameTypeEnum;
import com.example.playshare.Data.Enums.PlayLevelEnum;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class NewGameActivity extends AppCompatActivity {
    ArrayList<String> gameTypes = new ArrayList<>();
    ArrayList<String> playersLevels = new ArrayList<>();
    ArrayList<String> preferredGames = new ArrayList<>();
    private AutoCompleteTextView gameTypeInput, playersLevelInput, preferredGameInput;
    private Button saveGame, cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_game);

        for (GameTypeEnum gameType : GameTypeEnum.values()) {
            gameTypes.add(gameType.toString());
        }

        for (PlayLevelEnum level : PlayLevelEnum.values()) {
            playersLevels.add(level.toString());
        }

        for (GameLayoutEnum gameLayout : GameLayoutEnum.values()) {
            preferredGames.add(gameLayout.getTitle());
        }

        // initialize the input fields
        gameTypeInput = findViewById(R.id.gameTypeInput);
        playersLevelInput = findViewById(R.id.playersLevelInput);
        preferredGameInput = findViewById(R.id.preferredGameInput);
        saveGame = findViewById(R.id.saveGame);
        cancel = findViewById(R.id.cancel);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gameTypes);
        gameTypeInput.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, playersLevels);
        playersLevelInput.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, preferredGames);
        preferredGameInput.setAdapter(adapter);

        // set click events:
        gameTypeInput.setOnClickListener(v -> gameTypeInput.showDropDown());
        playersLevelInput.setOnClickListener(v -> playersLevelInput.showDropDown());
        preferredGameInput.setOnClickListener(v -> preferredGameInput.showDropDown());
        saveGame.setOnClickListener(v -> createNewGame());
        cancel.setOnClickListener(v -> cancelOperation());
    }

    private void cancelOperation() {
        finish();
    }

    private void createNewGame() {
        // todo: implement this method
        boolean isValid = true;
        if (gameTypeInput.getText().toString().isEmpty()) {
            gameTypeInput.setError(getString(R.string.game_type_is_required));
            isValid = false;
        } else {
            gameTypeInput.setError(null);
        }
        if (playersLevelInput.getText().toString().isEmpty()) {
            playersLevelInput.setError(getString(R.string.players_level_is_required));
            isValid = false;
        } else {
            playersLevelInput.setError(null);
        }
        if (preferredGameInput.getText().toString().isEmpty()) {
            preferredGameInput.setError(getString(R.string.preferred_game_is_required));
            isValid = false;
        } else {
            preferredGameInput.setError(null);
        }
        if (!isValid) return;

        // get the location from the intent, default to Azrieli College
        LatLng location = new LatLng(
                getIntent().getDoubleExtra("lat", 31.7692),
                getIntent().getDoubleExtra("lng", 35.1937));
    }
}