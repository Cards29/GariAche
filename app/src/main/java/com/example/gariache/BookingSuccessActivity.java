package com.example.gariache;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BookingSuccessActivity extends AppCompatActivity {

    private TextView bookingId, driverNameSummary, pickupSummary, destinationSummary;
    private TextView datetimeSummary, seatsSummary, phoneSummary, notesSummary;
    private TextView totalAmountSummary, totalLabel;
    private LinearLayout notesLayout;
    private Button goHomeButton;
    private boolean isFreeRide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        populateBookingDetails();
        setupClickListeners();
    }

    private void initializeViews() {
        bookingId = findViewById(R.id.booking_id);
        driverNameSummary = findViewById(R.id.driver_name_summary);
        pickupSummary = findViewById(R.id.pickup_summary);
        destinationSummary = findViewById(R.id.destination_summary);
        datetimeSummary = findViewById(R.id.datetime_summary);
        seatsSummary = findViewById(R.id.seats_summary);
        phoneSummary = findViewById(R.id.phone_summary);
        notesSummary = findViewById(R.id.notes_summary);
        totalAmountSummary = findViewById(R.id.total_amount_summary);
        notesLayout = findViewById(R.id.notes_layout);
        goHomeButton = findViewById(R.id.go_home_button);
    }

    private void populateBookingDetails() {
        Intent intent = getIntent();

        // Get all the booking data passed from BookingConfirmationActivity
        String driverName = intent.getStringExtra("driverName");
        String pickup = intent.getStringExtra("pickup");
        String destination = intent.getStringExtra("destination");
        String departureTime = intent.getStringExtra("departureTime");
        int ridePrice = intent.getIntExtra("ridePrice", 0);
        int bookedSeats = intent.getIntExtra("bookedSeats", 1);
        String phone = intent.getStringExtra("phone");
        String notes = intent.getStringExtra("notes");
        isFreeRide = intent.getBooleanExtra("isFree", false); // Get free ride flag

        // Generate booking ID (FIREBASE TODO: Use actual booking ID from database)
        String generatedId = generateBookingId();
        bookingId.setText("#" + generatedId);

        // Populate all fields
        driverNameSummary.setText(driverName);
        pickupSummary.setText(pickup);
        destinationSummary.setText(destination);
        datetimeSummary.setText("Today, " + departureTime);
        seatsSummary.setText(bookedSeats + " seat" + (bookedSeats > 1 ? "s" : ""));
        phoneSummary.setText(phone);

        // Handle free rides vs paid rides in total amount display
        if (isFreeRide) {
            totalLabel.setText("Ride Type:");
            totalAmountSummary.setText("FREE");
        } else {
            totalLabel.setText("Total Amount Paid:");
            int totalAmount = ridePrice * bookedSeats;
            totalAmountSummary.setText("৳" + totalAmount);
        }

        // Show notes if provided
        if (!TextUtils.isEmpty(notes)) {
            notesLayout.setVisibility(View.VISIBLE);
            notesSummary.setText(notes);
        }
    }

    private void setupClickListeners() {
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to home and clear all previous activities
                Intent intent = new Intent(BookingSuccessActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    // FIREBASE TODO: Replace with actual booking ID generation from Firebase
    private String generateBookingId() {
        String prefix = "GR";
        Random random = new Random();
        int number = 10000 + random.nextInt(90000); // 5-digit random number
        return prefix + number;
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to booking form - go to home instead
        Intent intent = new Intent(BookingSuccessActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
