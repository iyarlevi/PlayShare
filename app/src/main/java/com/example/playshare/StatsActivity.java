package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playshare.Components.BottomNavigator;
import com.example.playshare.Components.ProgressDialog;
import com.example.playshare.Components.TopAppBarMenuListener;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.example.playshare.Data.Enums.GameTypeEnum;
import com.example.playshare.Data.Models.GameModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class StatsActivity extends BaseActivityClass {

    private static final String TAG = "StatsActivity";
    private TextView livePlayersTextView, basketballGamesTextView, soccerGamesTextView, tennisGamesTextView;
    private ProgressDialog progressDialog;
    private final FireStoreConnector firestore = FireStoreConnector.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        livePlayersTextView = findViewById(R.id.livePlayersTextView);
        basketballGamesTextView = findViewById(R.id.basketballGamesTextView);
        soccerGamesTextView = findViewById(R.id.soccerGamesTextView);
        tennisGamesTextView = findViewById(R.id.tennisGamesTextView);
        Button mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(v -> startActivity(new Intent(StatsActivity.this, MapActivity.class)));

        //define top app bar:
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarMenuListener topAppBarMenuListener = new TopAppBarMenuListener(this);
        topAppBar.setOnMenuItemClickListener(topAppBarMenuListener);

        //define bottom navigation:
        NavigationBarView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_stats);
        BottomNavigator bottomNavigator = new BottomNavigator(this, R.id.navigation_stats);
        bottomNavigationView.setOnItemSelectedListener(bottomNavigator);

        // Set initial loading text
        progressDialog = new ProgressDialog(this);
        // todo add to strings.xml
        progressDialog.setMessage("Loading stats...");
        progressDialog.show();
        getData();
    }

    private void getData() {
        try {
            firestore.getDocuments(CollectionsEnum.USERS.getCollectionName(),
                    documents -> {
                        int livePlayers = 0;
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                        Date currentDate = new Date();
                        for (Map<String, Object> document : documents) {
                            Object timeStamp = document.get("timeStamp");
                            if (timeStamp != null) {
                                String timeStampString = timeStamp.toString();
                                try {
                                    Date playerDate = sdf.parse(timeStampString);
                                    if (playerDate == null) {
                                        throw new ParseException("Date is null", 0);
                                    }
                                    long diff = currentDate.getTime() - playerDate.getTime();
                                    long diffDays = diff / (24 * 60 * 60 * 1000);
                                    if (diffDays <= 7) {
                                        livePlayers++;
                                    }
                                } catch (ParseException e) {
                                    Log.e(TAG, "Error parsing date: " + e.getMessage());
                                }
                            }
                        }
                        get_GamesStats();


                        livePlayersTextView.setText(getString(R.string.live_players, String.valueOf(livePlayers)));
                        progressDialog.dismiss();

                    }, e -> {
                        Log.e(TAG, "Error getting stats: " + e.getMessage());
                        get_GamesStats();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error getting stats: " + e.getMessage());
            Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_SHORT).show();
        }
    }

    private void get_GamesStats() {
        try {
            firestore.getDocuments(CollectionsEnum.GAMES.getCollectionName(),
                    documents -> {
                        int basketballGames = 0;
                        int soccerGames = 0;
                        int tennisGames = 0;
                        for (Map<String, Object> document : documents) {
                            GameModel game = new GameModel(document);
                            if (game.getType() == GameTypeEnum.BASKETBALL) {
                                basketballGames++;
                            } else if (game.getType() == GameTypeEnum.SOCCER) {
                                soccerGames++;
                            } else if (game.getType() == GameTypeEnum.TENNIS) {
                                tennisGames++;
                            }
                        }
                        basketballGamesTextView.setText(getString(R.string.basketball_games, String.valueOf(basketballGames)));
                        soccerGamesTextView.setText(getString(R.string.soccer_games, String.valueOf(soccerGames)));
                        tennisGamesTextView.setText(getString(R.string.tennis_games, String.valueOf(tennisGames)));
                    }, e -> Log.e(TAG, "Error getting games stats: " + e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "Error getting games stats: " + e.getMessage());
            Toast.makeText(this, getString(R.string.failed_fetch_data), Toast.LENGTH_SHORT).show();
        }
    }
}

