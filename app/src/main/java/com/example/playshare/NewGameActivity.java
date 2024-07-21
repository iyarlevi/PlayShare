package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.playshare.Components.TopAppBarMenuListener;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.example.playshare.Data.Enums.GameLayoutEnum;
import com.example.playshare.Data.Enums.GameTypeEnum;
import com.example.playshare.Data.Enums.PlayLevelEnum;
import com.example.playshare.Data.Models.GameModel;
import com.example.playshare.Data.Models.UserModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashMap;

public class NewGameActivity extends BaseActivityClass {
    ArrayList<String> gameTypes = new ArrayList<>();
    ArrayList<String> playersLevels = new ArrayList<>();
    ArrayList<String> preferredGames = new ArrayList<>();
    private AutoCompleteTextView gameTypeInput, playersLevelInput, preferredGameInput;
    private final FireStoreConnector database = FireStoreConnector.getInstance();
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

        //define top app bar:
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarMenuListener topAppBarMenuListener = new TopAppBarMenuListener(this);
        topAppBar.setOnMenuItemClickListener(topAppBarMenuListener);


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
        saveGame.setOnClickListener(v -> {
            saveGame.setEnabled(false);
            cancel.setEnabled(false);
            createNewGame();
        });
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
        if (!isValid) {
            saveGame.setEnabled(true);
            cancel.setEnabled(true);
            return;
        }

        // get the location from the intent, default to Azrieli College
        LatLng location = new LatLng(
                getIntent().getDoubleExtra("lat", 31.7692),
                getIntent().getDoubleExtra("lng", 35.1937));

        String currentUserUid = FirebaseConnector.getCurrentUser().getUid();
        try {
            database.getDocument(CollectionsEnum.USERS.getCollectionName(),
                    currentUserUid,
                    userDocument -> {
                        UserModel user = new UserModel(userDocument);
                        if (userDocument.getOrDefault("currentGame", null) != null) {
                            Toast.makeText(this, getString(R.string.game_already_exists), Toast.LENGTH_SHORT).show();
                            cancel.setEnabled(true);
                            return;
                        }
                        // check distance from the user location to the game location
                        float[] distance = new float[1];
                        android.location.Location.distanceBetween(
                                user.getLocation().latitude,
                                user.getLocation().longitude,
                                location.latitude,
                                location.longitude,
                                distance
                        );
                        if (distance[0] > 1000) {
                            Toast.makeText(this, getString(R.string.game_too_far), Toast.LENGTH_SHORT).show();
                            cancel.setEnabled(true);
                            return;
                        }

                        GameModel game = new GameModel(
                                GameTypeEnum.valueOf(gameTypeInput.getText().toString()),
                                location,
                                GameLayoutEnum.getEnum(preferredGameInput.getText().toString()),
                                currentUserUid,
                                PlayLevelEnum.valueOf(playersLevelInput.getText().toString())
                        );
                        database.addDocument(CollectionsEnum.GAMES.getCollectionName(),
                                game.MappingForFirebase(),
                                gameDocumentReference -> {
                                    HashMap<String, Object> currentGame = new HashMap<>();
                                    currentGame.put("currentGame", gameDocumentReference.getId());
                                    database.updateDocument(
                                            CollectionsEnum.USERS.getCollectionName(),
                                            currentUserUid,
                                            currentGame,
                                            aVoid -> {
                                                Toast.makeText(this, getString(R.string.game_created), Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(this, MapActivity.class);
                                                intent.putExtra("latitude", location.latitude);
                                                intent.putExtra("longitude", location.longitude);
                                                startActivity(intent);
                                                finish();
                                            },
                                            e -> {
                                                Toast.makeText(this, getString(R.string.game_not_created), Toast.LENGTH_SHORT).show();
                                                cancel.setEnabled(true);
                                                Log.e("NewGameActivity", ">>> createNewGame: " + e.getMessage());
                                            }
                                    );
                                },
                                e -> {
                                    Toast.makeText(this, getString(R.string.game_not_created), Toast.LENGTH_SHORT).show();
                                    Log.e("NewGameActivity", ">>> createNewGame: " + e.getMessage());
                                }
                        );
                    },
                    e -> Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_SHORT).show()
            );
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_SHORT).show();
            Log.e("NewGameActivity", ">>> createNewGame: " + e.getMessage());
        }
    }
}