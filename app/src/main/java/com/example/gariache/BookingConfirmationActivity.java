package com.example.gariache;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class BookingConfirmationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView driverNameDetail, pickupDetail, destinationDetail, timeDetail, priceDetail, totalPrice, totalPriceLabel;
    private TextInputEditText seatsInput, phoneInput, notesInput;
    private Button confirmBookingButton;
    private boolean isFreeRide = false;

    // Ride data from previous activity
    private String driverName, pickup, destination, departureTime;
    private int ridePrice, availableSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        getRideDataFromIntent();
        populateRideDetails();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        driverNameDetail = findViewById(R.id.driver_name_detail);
        pickupDetail = findViewById(R.id.pickup_detail);
        destinationDetail = findViewById(R.id.destination_detail);
        timeDetail = findViewById(R.id.time_detail);
        priceDetail = findViewById(R.id.price_detail);
        totalPrice = findViewById(R.id.total_price);
        totalPriceLabel = findViewById(R.id.total_price_label);

        seatsInput = findViewById(R.id.seats_input);
        phoneInput = findViewById(R.id.phone_input);
        notesInput = findViewById(R.id.notes_input);

        confirmBookingButton = findViewById(R.id.confirm_booking_button);
    }

    private void getRideDataFromIntent() {
        Intent intent = getIntent();
        driverName = intent.getStringExtra("driverName");
        pickup = intent.getStringExtra("pickup");
        destination = intent.getStringExtra("destination");
        departureTime = intent.getStringExtra("departureTime");
        ridePrice = intent.getIntExtra("price", 0);
        availableSeats = intent.getIntExtra("availableSeats", 0);
        isFreeRide = intent.getBooleanExtra("isFree", false); // Get free ride flag
    }

    private void populateRideDetails() {
        driverNameDetail.setText(driverName);
        pickupDetail.setText(pickup);
        destinationDetail.setText(destination);
        timeDetail.setText(departureTime);
        // Handle free ride display
        if (isFreeRide) {
            priceDetail.setText("FREE");
            totalPriceLabel.setText("Ride Type:");
            totalPrice.setText("FREE");
        } else {
            priceDetail.setText("৳" + ridePrice);
            totalPriceLabel.setText("Total Amount:");
            totalPrice.setText("৳" + ridePrice);
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Update total price when seats change
        seatsInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateTotalPrice();
                }
            }
        });

        confirmBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBookingConfirmation();
            }
        });
    }

    private void updateTotalPrice() {
        String seatsText = seatsInput.getText().toString().trim();
        if (!TextUtils.isEmpty(seatsText)) {
            try {
                int requestedSeats = Integer.parseInt(seatsText);
                if (requestedSeats > 0 && requestedSeats <= availableSeats) {
                    if (isFreeRide) {
                        totalPrice.setText("FREE");
                    } else {
                        int total = ridePrice * requestedSeats;
                        totalPrice.setText("৳" + total);
                    }
                } else {
                    seatsInput.setError("Invalid number of seats (max: " + availableSeats + ")");
                }
            } catch (NumberFormatException e) {
                seatsInput.setError("Please enter a valid number");
            }
        }
    }

    private void handleBookingConfirmation() {
        String seats = seatsInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(seats)) {
            seatsInput.setError("Please enter number of seats");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneInput.setError("Please enter your phone number");
            return;
        }

        try {
            int requestedSeats = Integer.parseInt(seats);
            if (requestedSeats <= 0 || requestedSeats > availableSeats) {
                seatsInput.setError("Invalid number of seats (max: " + availableSeats + ")");
                return;
            }

            // FIREBASE TODO: Save booking to Firebase Database
            // BookingData booking = new BookingData(driverName, pickup, destination,
            //                                       departureTime, requestedSeats, phone, notes);
            // Save to Firebase and get booking ID

            // Navigate to success page with all booking data
            Intent intent = new Intent(BookingConfirmationActivity.this, BookingSuccessActivity.class);
            intent.putExtra("driverName", driverName);
            intent.putExtra("pickup", pickup);
            intent.putExtra("destination", destination);
            intent.putExtra("departureTime", departureTime);
            intent.putExtra("ridePrice", ridePrice);
            intent.putExtra("bookedSeats", requestedSeats);
            intent.putExtra("phone", phone);
            intent.putExtra("notes", notes);
            intent.putExtra("isFree", isFreeRide);

            startActivity(intent);
            finish();

        } catch (NumberFormatException e) {
            seatsInput.setError("Please enter a valid number");
        }
    }

}
