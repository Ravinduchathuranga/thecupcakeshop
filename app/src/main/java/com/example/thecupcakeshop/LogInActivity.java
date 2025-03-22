package com.example.thecupcakeshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private Button signInButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.progressBar);

        // Set OnClickListener for Sign In Button
        signInButton.setOnClickListener(v -> signInUser());
    }

    private void signInUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate Inputs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show Progress Bar
        progressBar.setVisibility(View.VISIBLE);

        // Sign In with Firebase
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE); // Hide Progress Bar
                    if (task.isSuccessful()) {
                        // Sign In Success
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null && user.getEmail().equals("adminuser@cupcake.com")) {
                            // Navigate to Admin Dashboard
                            startActivity(new Intent(LogInActivity.this, DashboardActivity.class));
                        } else {
                            // Navigate to User Dashboard
                            startActivity(new Intent(LogInActivity.this, DashboardActivity.class));
                        }
                        finish(); // Close SignInActivity
                    } else {
                        // Sign In Failed
                        Toast.makeText(LogInActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}