package com.example.playshare;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.playshare.Components.ProgressDialog;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;
import com.example.playshare.Data.Enums.GameTypeEnum;
import com.example.playshare.Data.Models.UserModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends BaseActivityClass {
    private EditText heightEditText, ageEditText, nicknameEditText, preferencesInput;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> openGalleryChooseImage;
    private ImageView profileImageView;
    private Bitmap imageBitmap;
    private boolean changedImage = false;
    private RequestQueue volleyQueue;
    private final FireStoreConnector database = FireStoreConnector.getInstance();
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final String[] preferredGames = new String[GameTypeEnum.values().length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        for (GameTypeEnum gameType : GameTypeEnum.values()) {
            preferredGames[gameType.ordinal()] = gameType.toString();
        }

        nicknameEditText = findViewById(R.id.nicknameEditText);
        heightEditText = findViewById(R.id.heightEditText);
        ageEditText = findViewById(R.id.ageEditText);
        profileImageView = findViewById(R.id.imageView);
        preferencesInput = findViewById(R.id.preferencesInput);
        Button cameraButton = findViewById(R.id.cameraButton);
        Button galleryButton = findViewById(R.id.galleryButton);
        Button saveButton = findViewById(R.id.saveButton);
        volleyQueue = Volley.newRequestQueue(this);


        // Load saved settings if needed
        loadSavedSettings();

        preferencesInput.setOnClickListener(v -> setPreferencesInput());
        saveButton.setOnClickListener(v -> saveSettings());
        cameraButton.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(takePictureIntent);
        });
        galleryButton.setOnClickListener(v -> openGalleryChooseImage.launch("image/*"));

        // create Launchers
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
                        saveButton.setEnabled(false);
                        imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                        profileImageView.setImageBitmap(imageBitmap);
                        changedImage = true;
                        saveButton.setEnabled(true);
                    }
                });

        openGalleryChooseImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        saveButton.setEnabled(false);
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                            profileImageView.setImageBitmap(imageBitmap);
                            changedImage = true;
                        } catch (Exception e) {
                            Log.d("SettingsActivity", ">>> openGalleryChooseImage: " + e.getMessage());
                        }
                        saveButton.setEnabled(true);
                    }
                });
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
                    String preferences = (objectHolder != null) ? objectHolder.toString().replaceAll("[\\[\\]]", "") : "";
                    preferencesInput.setText(preferences);

                    objectHolder = document.get("height");
                    String height = (objectHolder != null) ? objectHolder.toString() : "";
                    heightEditText.setText(height);

                    objectHolder = document.get("age");
                    String age = (objectHolder != null) ? objectHolder.toString() : "";
                    ageEditText.setText(age);

                    objectHolder = document.get("imageUrl");
                    if (objectHolder != null) {
                        String imageUrl = objectHolder.toString();
                        ImageRequest imageRequest = new ImageRequest(imageUrl,
                                response -> {
                                    profileImageView.setImageBitmap(response);
                                    progressDialog.dismiss();
                                },
                                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                                error -> {
                                    Log.d("SettingsActivity", ">>> loadSavedSettings: " + error.getMessage());
                                    progressDialog.dismiss();
                                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                                });
                        volleyQueue.add(imageRequest);
                    } else {
                        progressDialog.dismiss();
                    }

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

        if (imageBitmap != null && changedImage) {
            StorageReference mountainsRef = firebaseStorage.getReference().child("images/" + FirebaseConnector.getCurrentUser().getUid() + ".jpg");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(imageBytes);
            uploadTask.addOnFailureListener(exception -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                Log.d("SettingsActivity", ">>> saveSettings: " + exception.getMessage());
            }).addOnSuccessListener(taskSnapshot -> mountainsRef.getDownloadUrl().addOnSuccessListener(uri -> uploadData(uri.toString())));
        } else {
            uploadData(null);
        }
    }

    private void uploadData(String imageUrl) {
        // extract user data from the form
        int age = (ageEditText.getText().toString().isEmpty()) ? -1 : Integer.parseInt(ageEditText.getText().toString());
        double height = (heightEditText.getText().toString().isEmpty()) ? -1 : Double.parseDouble(heightEditText.getText().toString());

        ArrayList<String> preferences;
        if (preferencesInput.getText().toString().isEmpty()) {
            preferences = null;
        } else {
            preferences = new ArrayList<>(Arrays.asList(preferencesInput.getText().toString().split(",")));
        }

        UserModel user = new UserModel(
                age,
                height,
                nicknameEditText.getText().toString(),
                null,
                preferences,
                imageUrl
        );

        database.updateDocument(
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

    private void setPreferencesInput() {
        ArrayList<String> chosenPreferredGames = new ArrayList<>();
        boolean[] checkedItems = new boolean[preferredGames.length];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //todo add xml string
        builder.setTitle("Select Game Types");
        builder.setMultiChoiceItems(preferredGames, checkedItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                chosenPreferredGames.add(preferredGames[which]);
            } else {
                chosenPreferredGames.remove(preferredGames[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder selectedItemsString = new StringBuilder();
            for (String item : chosenPreferredGames) {
                if (selectedItemsString.length() > 0) {
                    selectedItemsString.append(",");
                }
                selectedItemsString.append(item);
            }
            preferencesInput.setText(selectedItemsString.toString());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

}
