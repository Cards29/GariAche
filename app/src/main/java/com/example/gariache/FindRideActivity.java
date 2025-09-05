package com.example.gariache;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.firebase.database.FirebaseDatabase;

public class FindRideActivity extends AppCompatActivity {

    private TextInputEditText pickupInput, dateInput; // REMOVED: timeInput
    private AutoCompleteTextView destinationAutoComplete;
    private Button searchButton;
    private ImageView backButton;
    private LinearLayout ridesContainer, noRidesLayout;
    private TextView availableRidesTitle;
    private DatabaseHelper dbHelper;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);

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
        pickupInput = findViewById(R.id.pickup_input);
        destinationAutoComplete = findViewById(R.id.destination_autocomplete);
        dateInput = findViewById(R.id.date_input);
        // REMOVED: timeInput = findViewById(R.id.time_input);
        searchButton = findViewById(R.id.search_button);
        backButton = findViewById(R.id.back_button);
        ridesContainer = findViewById(R.id.rides_container);
        noRidesLayout = findViewById(R.id.no_rides_layout);
        availableRidesTitle = findViewById(R.id.available_rides_title);

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
        backButton.setOnClickListener(v -> finish());

        dateInput.setOnClickListener(v -> showDatePicker());

        // REMOVED: timeInput.setOnClickListener(v -> showTimePicker());

        searchButton.setOnClickListener(v -> performSearch());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerTheme,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // REMOVED: showTimePicker() method entirely

    private void performSearch() {
        String pickup = "IUT"; // Always IUT
        String destination = destinationAutoComplete.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        // REMOVED: String time = timeInput.getText().toString().trim();

        // Basic validation - UPDATED: removed time validation
        if (destination.isEmpty() || destination.equals("Select Destination")) {
            destinationAutoComplete.setError("Please select a destination");
            return;
        }

        if (date.isEmpty()) { // UPDATED: only check date, not time
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Search rides in SQLite database - UPDATED: removed time parameter
        searchRidesInDatabase(pickup, destination, date);
    }

    private void searchRidesInDatabase(String pickup, String destination, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_RIDE_PICKUP + " LIKE ? AND " +
                DatabaseHelper.COLUMN_RIDE_DESTINATION + " LIKE ? AND " +
                DatabaseHelper.COLUMN_RIDE_DATE + " = ? AND " +
                DatabaseHelper.COLUMN_RIDE_STATUS + " = ?";

        String[] selectionArgs = {"%" + pickup + "%", "%" + destination + "%", date, "active"};

        List<Ride> searchResults = new ArrayList<>();

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_RIDES, null, selection, selectionArgs, null, null, null)) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                int driverId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_DRIVER_ID));
                String pickupDB = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_PICKUP));
                String destinationDB = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_DESTINATION));
                String dateDB = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_DATE));
                String timeDB = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_TIME));
                int availableSeats = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_SEATS));
                int pricePerSeat = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_PRICE));
                String carModel = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_CAR_MODEL));
                String carColor = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_CAR_COLOR));
                String licensePlate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_LICENSE_PLATE));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_NOTES));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIDE_STATUS));

                Ride ride = new Ride(id, driverId, pickupDB, destinationDB, dateDB, timeDB,
                        availableSeats, pricePerSeat, carModel, carColor, licensePlate, notes, status);
                searchResults.add(ride);
            }
        }

        displaySearchResults(searchResults);
    }

    private void displaySearchResults(List<Ride> rides) {
        ridesContainer.removeAllViews();

        if (rides.isEmpty()) {
            availableRidesTitle.setVisibility(View.GONE);
            ridesContainer.setVisibility(View.GONE);
            noRidesLayout.setVisibility(View.VISIBLE);
        } else {
            availableRidesTitle.setVisibility(View.VISIBLE);
            ridesContainer.setVisibility(View.VISIBLE);
            noRidesLayout.setVisibility(View.GONE);

            for (Ride ride : rides) {
                addRideView(ride);
            }
        }
    }

    private void addRideView(Ride ride) {
        View rideView = LayoutInflater.from(this).inflate(R.layout.ride_item, ridesContainer, false);

        TextView driverName = rideView.findViewById(R.id.driver_name);
        TextView ridePrice = rideView.findViewById(R.id.ride_price);
        TextView pickupPoint = rideView.findViewById(R.id.pickup_point);
        TextView destinationPoint = rideView.findViewById(R.id.destination_point);
        TextView departureTime = rideView.findViewById(R.id.departure_time);
        TextView availableSeats = rideView.findViewById(R.id.available_seats);
        Button bookButton = rideView.findViewById(R.id.book_button);

        // Get current logged-in user ID
        SharedPreferences prefs = getSharedPreferences("GariAche", MODE_PRIVATE);
        int currentUserId = prefs.getInt("current_user_id", -1);

        // Get driver name from database
        String driverNameText = getDriverName(ride.getDriverId());
        driverName.setText(driverNameText);

        // Set ride details
        pickupPoint.setText(ride.getPickupPoint());
        destinationPoint.setText(ride.getDestination());
        departureTime.setText(ride.getDate() + " at " + ride.getTime());
        availableSeats.setText(ride.getAvailableSeats() + " seats left");

        // Handle pricing
        if (ride.getPricePerSeat() == 0) {
            ridePrice.setText("FREE");
            ridePrice.setTextColor(ContextCompat.getColor(this, R.color.blue_accent));
        } else {
            ridePrice.setText("৳" + ride.getPricePerSeat());
        }

        // ✅ NEW: Check if this is the user's own ride
        if (currentUserId == ride.getDriverId()) {
            // This is the user's own ride - disable booking
            bookButton.setText("Your Ride");
            bookButton.setEnabled(false);
            bookButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green_success));
            bookButton.setTextColor(ContextCompat.getColor(this, R.color.white));

            // No click listener needed for own rides
            bookButton.setOnClickListener(v -> {
                Toast.makeText(this, "You cannot book your own ride", Toast.LENGTH_SHORT).show();
            });
        }
        // Check if no seats available
        else if (ride.getAvailableSeats() <= 0) {
            // No seats available - disable button
            bookButton.setText("No seats left");
            bookButton.setEnabled(false);
            bookButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grey));
            bookButton.setTextColor(ContextCompat.getColor(this, R.color.white));

            bookButton.setOnClickListener(v -> {
                Toast.makeText(this, "This ride is fully booked", Toast.LENGTH_SHORT).show();
            });
        }
        // Normal booking available
        else {
            // Seats available and not own ride - enable booking
            bookButton.setText("Book This Ride");
            bookButton.setEnabled(true);
            bookButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue_primary));
            bookButton.setTextColor(ContextCompat.getColor(this, R.color.white));

            // Handle book button click
            bookButton.setOnClickListener(v -> {
                Intent intent = new Intent(FindRideActivity.this, BookingConfirmationActivity.class);
                intent.putExtra("rideId", ride.getId());
                intent.putExtra("driverName", driverNameText);
                intent.putExtra("pickup", ride.getPickupPoint());
                intent.putExtra("destination", ride.getDestination());
                intent.putExtra("departureTime", ride.getDate() + " at " + ride.getTime());
                intent.putExtra("price", ride.getPricePerSeat());
                intent.putExtra("availableSeats", ride.getAvailableSeats());
                intent.putExtra("isFree", ride.getPricePerSeat() == 0);
                startActivity(intent);
            });
        }

        ridesContainer.addView(rideView);
    }


    private String getDriverName(int driverId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_USER_NAME};
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(driverId)};

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME));
            }
        }
        return "Unknown Driver";
    }
}
