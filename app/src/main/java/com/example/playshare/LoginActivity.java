package com.example.playshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.playshare.Components.ProgressDialog;
import com.example.playshare.Connectors.FireStoreConnector;
import com.example.playshare.Connectors.FirebaseConnector;
import com.example.playshare.Data.Enums.CollectionsEnum;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailEdt, passwordEdt;
    private Button loginBtn;
    private ProgressDialog progressDialog;

    FireStoreConnector database = FireStoreConnector.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEdt = findViewById(R.id.emailEditText);
        passwordEdt = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginButton);
        Button registerBtn = findViewById(R.id.registerButton);

        emailEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                passwordEdt.requestFocus();
                return true;
            }
            return false;
        });

        passwordEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginBtn.performClick();
                return true;
            }
            return false;
        });

        // Initialize class fields
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginButton) {
            handleLogin();
        } else if (view.getId() == R.id.registerButton) {
            handleRegister();
        }
    }

    private void handleLogin() {
        try {
            String email = emailEdt.getText().toString();
            if (email.isEmpty()) {
                emailEdt.setError(getString(R.string.email_empty));
                emailEdt.requestFocus();
                return;
            }
            String password = passwordEdt.getText().toString();
            if (password.isEmpty()) {
                passwordEdt.setError(getString(R.string.password_empty));
                passwordEdt.requestFocus();
                return;
            }

            progressDialog.setMessage(getString(R.string.login_delay));
            progressDialog.showLoading();

            FirebaseConnector.signIn(email, password, authResult -> database.getDocumentReference(CollectionsEnum.USERS.getCollectionName(),
                    (authResult.getUser() != null) ? authResult.getUser().getUid() : "No User",
                    documentReference -> { // Success
                        progressDialog.hideLoading();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }, e -> {
                        Intent intent = new Intent(this, SettingsActivity.class);
                        startActivity(intent);
                        finish();
                    }
            ), e -> {
                progressDialog.hideLoading();
                if ((Objects.requireNonNull(e.getMessage())).contains("email address is badly formatted")) {
                    emailEdt.setError(getString(R.string.invalid_email));
                    emailEdt.requestFocus();
                    return;
                }
                if (e.getMessage().contains("password is invalid")) {
                    passwordEdt.setError(getString(R.string.invalid_password));
                    passwordEdt.requestFocus();
                    return;
                }
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.e("LoginActivity", "Error: " + e.getMessage());
        }
    }

    private void handleRegister() {
        // Implement registration functionality here
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
