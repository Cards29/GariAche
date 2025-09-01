package com.example.gariache;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class OfferRideActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextInputEditText pickupInput, destinationInput, dateInput, timeInput;
    private TextInputEditText seatsInput, priceInput;
    private TextInputEditText carModelInput, carColorInput, licensePlateInput, notesInput;
    private Button postRideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        pickupInput = findViewById(R.id.pickup_input);
        destinationInput = findViewById(R.id.destination_input);
        dateInput = findViewById(R.id.date_input);
        timeInput = findViewById(R.id.time_input);
        seatsInput = findViewById(R.id.seats_input);
        priceInput = findViewById(R.id.price_input);
        carModelInput = findViewById(R.id.car_model_input);
        carColorInput = findViewById(R.id.car_color_input);
        licensePlateInput = findViewById(R.id.license_plate_input);
        notesInput = findViewById(R.id.notes_input);
        postRideButton = findViewById(R.id.post_ride_button);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        postRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePostRide();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateInput.setText(selectedDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                R.style.CustomTimePickerTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        timeInput.setText(selectedTime);
                    }
                },
                hour, minute, false
        );
        timePickerDialog.show();
    }

    private void handlePostRide() {
        // Get all input values
        String pickup = pickupInput.getText().toString().trim();
        String destination = destinationInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();
        String seats = seatsInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String carModel = carModelInput.getText().toString().trim();
        String carColor = carColorInput.getText().toString().trim();
        String licensePlate = licensePlateInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        // Validation (same as before until price validation)
        if (TextUtils.isEmpty(pickup)) {
            pickupInput.setError("Please enter pickup point");
            return;
        }

        if (TextUtils.isEmpty(destination)) {
            destinationInput.setError("Please enter destination");
            return;
        }

        if (TextUtils.isEmpty(date)) {
            dateInput.setError("Please select date");
            return;
        }

        if (TextUtils.isEmpty(time)) {
            timeInput.setError("Please select time");
            return;
        }

        if (TextUtils.isEmpty(seats)) {
            seatsInput.setError("Please enter available seats");
            return;
        }

        // Modified price validation - allow empty for free rides
        // if (TextUtils.isEmpty(price)) {
        //     priceInput.setError("Please enter price per seat");
        //     return;
        // }

        if (TextUtils.isEmpty(carModel)) {
            carModelInput.setError("Please enter car model");
            return;
        }

        if (TextUtils.isEmpty(carColor)) {
            carColorInput.setError("Please enter car color");
            return;
        }

        if (TextUtils.isEmpty(licensePlate)) {
            licensePlateInput.setError("Please enter license plate");
            return;
        }

        try {
            int availableSeats = Integer.parseInt(seats);

            // Handle free rides - if price is empty, set to 0
            int pricePerSeat = 0;
            if (!TextUtils.isEmpty(price)) {
                pricePerSeat = Integer.parseInt(price);
                if (pricePerSeat < 0) {
                    priceInput.setError("Price cannot be negative");
                    return;
                }
            }

            if (availableSeats <= 0 || availableSeats > 8) {
                seatsInput.setError("Please enter valid number of seats (1-8)");
                return;
            }

            // FIREBASE TODO: Save ride to Firebase Database with pricePerSeat (0 for free)

            String message = pricePerSeat == 0 ?
                    "Free ride posted successfully! Passengers can now book your ride." :
                    "Ride posted successfully! Passengers can now book your ride.";

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Return to home screen
            Intent intent = new Intent(OfferRideActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for seats and price", Toast.LENGTH_SHORT).show();
        }
    }

}
