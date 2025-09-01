package com.example.gariache;

import android.content.Intent;
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

        initializeViews();
        setupClickListeners();
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
                // Navigate to Find Ride screen
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
                // TODO: Navigate to My Rides screen
                Toast.makeText(HomeActivity.this, "My Rides - Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });

        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to login screen
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
