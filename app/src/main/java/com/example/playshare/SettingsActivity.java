package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playshare.Components.ProgressDialog;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.example.playshare.Data.Models.UserModel;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private EditText heightEditText, ageEditText, nicknameEditText;
    private EditText preferencesEditText;
    private ProgressDialog progressDialog;
    private final FireStoreConnector database = FireStoreConnector.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        nicknameEditText = findViewById(R.id.nicknameEditText);
        preferencesEditText = findViewById(R.id.preferencesEditText);
        heightEditText = findViewById(R.id.heightEditText);
        ageEditText = findViewById(R.id.ageEditText);
        Button saveButton = findViewById(R.id.saveButton);

        // Load saved settings if needed
        loadSavedSettings();

        saveButton.setOnClickListener(v -> saveSettings());

    }

    private void loadSavedSettings() {
        database.getDocument(
                CollectionsEnum.USERS.getCollectionName(),
                FirebaseConnector.getCurrentUser().getUid(),
                document -> {
                    Object objectHolder = document.get("nickname");
                    String nickname = (objectHolder != null) ? objectHolder.toString() : "";
                    nicknameEditText.setText(nickname);

                    objectHolder = document.get("preferences");
                    String preferences = (objectHolder != null) ? objectHolder.toString() : "";
                    preferencesEditText.setText(preferences);

                    objectHolder = document.get("height");
                    String height = (objectHolder != null) ? objectHolder.toString() : "";
                    heightEditText.setText(height);

                    objectHolder = document.get("age");
                    String age = (objectHolder != null) ? objectHolder.toString() : "";
                    ageEditText.setText(age);

                    progressDialog.dismiss();
                },
                e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to load previous settings", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void saveSettings() {
        if (nicknameEditText.getText().toString().isEmpty()) {
            nicknameEditText.setError("Nickname is required");
            Toast.makeText(this, "Please enter nickname", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();

        // extract user data from the form
        int age = (ageEditText.getText().toString().isEmpty()) ? -1 : Integer.parseInt(ageEditText.getText().toString());
        if (age > 120) {
            age = -1;
        }

        double height = (heightEditText.getText().toString().isEmpty()) ? -1 : Double.parseDouble(heightEditText.getText().toString());
        if (height > 3) {
            height = -1;
        }

        ArrayList<String> preferences;
        if (preferencesEditText.getText().toString().isEmpty()) {
            preferences = null;
        } else {
            preferences = new ArrayList<>(Arrays.asList(preferencesEditText.getText().toString().split(",")));
        }

        UserModel user = new UserModel(
                age,
                height,
                nicknameEditText.getText().toString(),
                null,
                preferences
        );

        database.addDocumentWithCustomId(
                CollectionsEnum.USERS.getCollectionName(),
                FirebaseConnector.getCurrentUser().getUid(),
                user.MappingForFirebase(),
                aVoid -> {
                    progressDialog.dismiss();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to save settings", Toast.LENGTH_SHORT).show();
                    Log.d("SettingsActivity", ">>> saveSettings: " + e.getMessage());
                }
        );
    }
}
