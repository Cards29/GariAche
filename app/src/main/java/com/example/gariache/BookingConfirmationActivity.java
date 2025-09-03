package com.example.gariache;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
    private int ridePrice, availableSeats, rideId;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

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
        rideId = intent.getIntExtra("rideId", -1);
        driverName = intent.getStringExtra("driverName");
        pickup = intent.getStringExtra("pickup");
        destination = intent.getStringExtra("destination");
        departureTime = intent.getStringExtra("departureTime");
        ridePrice = intent.getIntExtra("price", 0);
        availableSeats = intent.getIntExtra("availableSeats", 0);
        isFreeRide = intent.getBooleanExtra("isFree", false);
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
        backButton.setOnClickListener(v -> finish());

        // Update total price when seats change
        seatsInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                updateTotalPrice();
            }
        });

        confirmBookingButton.setOnClickListener(v -> handleBookingConfirmation());
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

            // Get current user ID
            SharedPreferences sharedPref = getSharedPreferences("GariAche", MODE_PRIVATE);
            int currentUserId = sharedPref.getInt("current_user_id", -1);

            if (currentUserId == -1) {
                Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save booking to SQLite Database
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_BOOKING_RIDE_ID, rideId);
            values.put(DatabaseHelper.COLUMN_BOOKING_PASSENGER_ID, currentUserId);
            values.put(DatabaseHelper.COLUMN_BOOKING_SEATS, requestedSeats);
            values.put(DatabaseHelper.COLUMN_BOOKING_PHONE, phone);
            values.put(DatabaseHelper.COLUMN_BOOKING_NOTES, notes);
            values.put(DatabaseHelper.COLUMN_BOOKING_STATUS, "confirmed");

            long newBookingId = db.insert(DatabaseHelper.TABLE_BOOKINGS, null, values);

            if (newBookingId == -1) {
                Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update ride's available seats
            updateRideSeats(rideId, availableSeats - requestedSeats);

            // Navigate to success page with all booking data
            Intent intent = new Intent(BookingConfirmationActivity.this, BookingSuccessActivity.class);
            intent.putExtra("bookingId", newBookingId);
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

    private void updateRideSeats(int rideId, int newAvailableSeats) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RIDE_SEATS, newAvailableSeats);

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(rideId)};

        db.update(DatabaseHelper.TABLE_RIDES, values, whereClause, whereArgs);
    }
}
