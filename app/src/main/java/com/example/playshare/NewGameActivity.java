package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playshare.Data.GameType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.ArrayList;

public class NewGameActivity extends AppCompatActivity {
    ArrayList<String> gameTypes = new ArrayList<>();
    private AutoCompleteTextView gameTypeInput;
    private EditText dateInput, timeInput;
    private Button saveGame, cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_game);
        // todo get the values from firebase instead
        for (GameType gameType : GameType.values()) {
            gameTypes.add(gameType.toString());
        }
        // initialize the input fields
        gameTypeInput = findViewById(R.id.gameTypeInput);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        saveGame = findViewById(R.id.saveGame);
        cancel = findViewById(R.id.cancel);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gameTypes);
        gameTypeInput.setAdapter(adapter);

        // set click events:
        gameTypeInput.setOnClickListener(v -> gameTypeInput.showDropDown());
        dateInput.setOnClickListener(v -> showDatePickerDialog());
        timeInput.setOnClickListener(v -> showTimePickerDialog());
        saveGame.setOnClickListener(v -> createNewGame());
        cancel.setOnClickListener(v -> cancelOperation());
    }

    private void cancelOperation() {
        Intent intent = new Intent(NewGameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void createNewGame() {
        // todo: implement this method
        boolean isValid = true;
        if (gameTypeInput.getText().toString().isEmpty()){
            gameTypeInput.setError(getString(R.string.game_type_is_required));
            isValid = false;
        }
        else {
            gameTypeInput.setError(null);
        }
        if (dateInput.getText().toString().isEmpty()){
            dateInput.setError(getString(R.string.date_is_required));
            isValid = false;
        }
        else {
            gameTypeInput.setError(null);
        }
        if (timeInput.getText().toString().isEmpty()){
            timeInput.setError(getString(R.string.time_is_required));
            isValid = false;
        }
        else {
            gameTypeInput.setError(null);
        }
        if (!isValid) return;

        // get the location from the intent, default to Azrieli College
        LatLng location = new LatLng(
                getIntent().getDoubleExtra("lat", 31.7692),
                getIntent().getDoubleExtra("lng", 35.1937));
    }

    private void showDatePickerDialog() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a Date");
        MaterialDatePicker<Long> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(selection -> dateInput.setText(picker.getHeaderText()));
    }

    private void showTimePickerDialog() {
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
        builder.setTitleText("Select a Time");
        MaterialTimePicker picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(v -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();
            String time = String.format(getResources().getConfiguration().getLocales().get(0),
                    "%02d:%02d %s", hour % 12 == 0 ? 12 : hour % 12, minute, hour < 12 ? "AM" : "PM");
            timeInput.setText(time);
        });
    }
}