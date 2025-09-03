package com.example.gariache;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private CardView findRideCard, offerRideCard;
    private TextView myRidesText, logoutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Hide action bar for cleaner look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Check if user is logged in
        checkUserLoggedIn();

        initializeViews();
        setupClickListeners();
    }

    private void checkUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("GariAche", MODE_PRIVATE);
        int userId = prefs.getInt("current_user_id", -1);
        if (userId == -1) {
            // Not logged in, redirect to login
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void initializeViews() {
        findRideCard = findViewById(R.id.find_ride_card);
        offerRideCard = findViewById(R.id.offer_ride_card);
        myRidesText = findViewById(R.id.my_rides_text);
        logoutText = findViewById(R.id.logout_text);
    }

    private void setupClickListeners() {
        findRideCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FindRideActivity.class);
                startActivity(intent);
            }
        });

        offerRideCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OfferRideActivity.class);
                startActivity(intent);
            }
        });

        myRidesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ViewMyRidesActivity.class);
                startActivity(intent);
            }
        });

        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear user session data
                SharedPreferences prefs = getSharedPreferences("GariAche", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Return to login screen
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
