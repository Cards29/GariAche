package com.example.gariache;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private TextInputEditText pickupInput, dateInput, timeInput;
    private TextInputEditText seatsInput, priceInput;
    private TextInputEditText carModelInput, carColorInput, licensePlateInput, notesInput;
    private AutoCompleteTextView destinationAutoComplete;
    private Button postRideButton;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        initializeViews();
        setupDestinationDropdown();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        pickupInput = findViewById(R.id.pickup_input);
        destinationAutoComplete = findViewById(R.id.destination_autocomplete);
        dateInput = findViewById(R.id.date_input);
        timeInput = findViewById(R.id.time_input);
        seatsInput = findViewById(R.id.seats_input);
        priceInput = findViewById(R.id.price_input);
        carModelInput = findViewById(R.id.car_model_input);
        carColorInput = findViewById(R.id.car_color_input);
        licensePlateInput = findViewById(R.id.license_plate_input);
        notesInput = findViewById(R.id.notes_input);
        postRideButton = findViewById(R.id.post_ride_button);

        // Fix pickup to IUT and make it non-editable
        pickupInput.setText("IUT");
        pickupInput.setFocusable(false);
        pickupInput.setClickable(false);
        pickupInput.setCursorVisible(false);
    }

    private void setupDestinationDropdown() {
        String[] destinations = getResources().getStringArray(R.array.dhaka_destinations);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, destinations);
        destinationAutoComplete.setAdapter(adapter);

        // Set white background for dropdown popup
        destinationAutoComplete.setDropDownBackgroundResource(R.drawable.white_dropdown_background);

        // Disable keyboard input, only allow selection
        destinationAutoComplete.setKeyListener(null);

        // Handle item selection
        destinationAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            destinationAutoComplete.setText(selected, false);
            destinationAutoComplete.dismissDropDown();
        });

        // Handle click to show dropdown
        destinationAutoComplete.setOnClickListener(v -> {
            destinationAutoComplete.showDropDown();
        });
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
        String pickup = "IUT"; // Always IUT now
        String destination = destinationAutoComplete.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();
        String seats = seatsInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String carModel = carModelInput.getText().toString().trim();
        String carColor = carColorInput.getText().toString().trim();
        String licensePlate = licensePlateInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        // Validation
        if (destination.isEmpty() || destination.equals("Select Destination")) {
            destinationAutoComplete.setError("Please select a destination");
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

            // Get current logged-in user ID
            SharedPreferences sharedPref = getSharedPreferences("GariAche", MODE_PRIVATE);
            int currentUserId = sharedPref.getInt("current_user_id", -1);

            if (currentUserId == -1) {
                Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save ride to SQLite Database
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_RIDE_DRIVER_ID, currentUserId);
            values.put(DatabaseHelper.COLUMN_RIDE_PICKUP, pickup);
            values.put(DatabaseHelper.COLUMN_RIDE_DESTINATION, destination);
            values.put(DatabaseHelper.COLUMN_RIDE_DATE, date);
            values.put(DatabaseHelper.COLUMN_RIDE_TIME, time);
            values.put(DatabaseHelper.COLUMN_RIDE_SEATS, availableSeats);
            values.put(DatabaseHelper.COLUMN_RIDE_PRICE, pricePerSeat);
            values.put(DatabaseHelper.COLUMN_RIDE_CAR_MODEL, carModel);
            values.put(DatabaseHelper.COLUMN_RIDE_CAR_COLOR, carColor);
            values.put(DatabaseHelper.COLUMN_RIDE_LICENSE_PLATE, licensePlate);
            values.put(DatabaseHelper.COLUMN_RIDE_NOTES, notes);
            values.put(DatabaseHelper.COLUMN_RIDE_STATUS, "active");

            long newRideId = db.insert(DatabaseHelper.TABLE_RIDES, null, values);

            if (newRideId == -1) {
                Toast.makeText(this, "Failed to post ride. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                String message = pricePerSeat == 0 ?
                        "Free ride posted successfully! Passengers can now book your ride." :
                        "Ride posted successfully! Passengers can now book your ride.";

                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                // Return to home screen
                Intent intent = new Intent(OfferRideActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for seats and price", Toast.LENGTH_SHORT).show();
        }
    }
}
