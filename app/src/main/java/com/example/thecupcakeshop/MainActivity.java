package com.example.thecupcakeshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth; // Firebase Authentication instance
    private ProgressBar progressBar; // ProgressBar for loading state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle system bars (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ProgressBar
        progressBar = findViewById(R.id.progressBarHome);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Introduce a 5-second delay using a Handler
        new Handler().postDelayed(() -> {
            // Check if the user is already signed in
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                if (user.getEmail().equals("admin@thecupcakeshop.com")) {
                    // Navigate to Admin Dashboard
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                } else {
                    // Navigate to Staff Dashboard or show an error
                    Toast.makeText(this, "Unauthorized user", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LogInActivity.class));
                }
            } else {
                // User is not signed in, navigate to LogInActivity
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
            }

            // Hide ProgressBar after navigation
            progressBar.setVisibility(View.GONE);

            // Finish the MainActivity to prevent going back
            finish();
        }, 5000); // 5000 milliseconds = 5 seconds
    }
}