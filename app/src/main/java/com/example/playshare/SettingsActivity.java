package com.example.playshare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText sportEditText, levelEditText, preferencesEditText;
    private EditText heightEditText, ageEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sportEditText = findViewById(R.id.sportEditText);
        levelEditText = findViewById(R.id.levelEditText);
        preferencesEditText = findViewById(R.id.preferencesEditText);
        heightEditText = findViewById(R.id.heightEditText);
        ageEditText = findViewById(R.id.ageEditText);
        saveButton = findViewById(R.id.saveButton);

        // Load saved settings if needed
        loadSavedSettings();

        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void loadSavedSettings() {
        SharedPreferences preferences = getSharedPreferences("user_settings", MODE_PRIVATE);
        String sport = preferences.getString("sport", "");
        String level = preferences.getString("level", "");
        String preferencesText = preferences.getString("preferences", "");
        String height = preferences.getString("height", "");
        String age = preferences.getString("age", "");

        sportEditText.setText(sport);
        levelEditText.setText(level);
        preferencesEditText.setText(preferencesText);
        heightEditText.setText(height);
        ageEditText.setText(age);
    }

    private void saveSettings() {
        String sport = sportEditText.getText().toString().trim();
        String level = levelEditText.getText().toString().trim();
        String preferencesText = preferencesEditText.getText().toString().trim();
        String height = heightEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();

        SharedPreferences.Editor editor = getSharedPreferences("user_settings", MODE_PRIVATE).edit();
        editor.putString("sport", sport);
        editor.putString("level", level);
        editor.putString("preferences", preferencesText);
        editor.putString("height", height);
        editor.putString("age", age);
        editor.apply();

        finish();
    }
}
