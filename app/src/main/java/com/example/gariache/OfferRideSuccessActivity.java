package com.example.gariache;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;

public class OfferRideSuccessActivity extends AppCompatActivity {

    private TextView driverNameView, pickupView, destinationView, dateTimeView;
    private TextView seatsView, priceView, carModelView, carColorView, licenseView, notesView, notesLabel;
    private Button doneButton;

    private String driverName, pickup, destination, date, time, carModel, carColor, license, notes;
    private int seats;
    private int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride_success);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Handle back press using new API
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Go to home instead of going back to offer ride form
                Intent intent = new Intent(OfferRideSuccessActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        initViews();
        getIntentData();
        populateUI();
        setupListeners();
    }

    /**
     * Initialize all UI views
     */
    private void initViews() {
        driverNameView = findViewById(R.id.driver_name);
        pickupView = findViewById(R.id.pickup);
        destinationView = findViewById(R.id.destination);
        dateTimeView = findViewById(R.id.date_time);
        seatsView = findViewById(R.id.seats);
        priceView = findViewById(R.id.price);
        carModelView = findViewById(R.id.car_model);
        carColorView = findViewById(R.id.car_color);
        licenseView = findViewById(R.id.license);
        notesView = findViewById(R.id.notes);
        notesLabel = findViewById(R.id.notes_label);
        doneButton = findViewById(R.id.done_button);
    }

    /**
     * Get data passed from OfferRideActivity via Intent
     */
    private void getIntentData() {
        Intent intent = getIntent();
        driverName = intent.getStringExtra("driverName");
        pickup = intent.getStringExtra("pickup");
        destination = intent.getStringExtra("destination");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        seats = intent.getIntExtra("seats", 0);
        price = intent.getIntExtra("price", 0);
        carModel = intent.getStringExtra("carModel");
        carColor = intent.getStringExtra("carColor");
        license = intent.getStringExtra("license");
        notes = intent.getStringExtra("notes");
    }

    /**
     * Populate UI with ride details
     */
    private void populateUI() {
        // Set basic ride information
        driverNameView.setText(driverName != null ? driverName : "Driver");
        pickupView.setText(pickup);
        destinationView.setText(destination);
        dateTimeView.setText(date + " at " + time);
        seatsView.setText(seats + " seats");

        // Handle price display - show FREE for zero price
        if (price == 0) {
            priceView.setText("FREE");
        } else {
            priceView.setText("৳" + price);
        }

        // Set car details
        carModelView.setText(carModel);
        carColorView.setText(carColor);
        licenseView.setText(license);

        // Show notes only if provided
        if (notes != null && !notes.trim().isEmpty()) {
            notesLabel.setVisibility(View.VISIBLE);
            notesView.setVisibility(View.VISIBLE);
            notesView.setText(notes);
        } else {
            notesLabel.setVisibility(View.GONE);
            notesView.setVisibility(View.GONE);
        }
    }

    /**
     * Setup click listeners
     */
    private void setupListeners() {
        doneButton.setOnClickListener(v -> {
            // Navigate to home screen and clear activity stack
            Intent intent = new Intent(OfferRideSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
