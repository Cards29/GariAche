package com.example.gariache;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FindRideActivity extends AppCompatActivity {

    private TextInputEditText pickupInput, destinationInput, dateInput, timeInput;
    private Button searchButton;
    private ImageView backButton;
    private LinearLayout ridesContainer, noRidesLayout;
    private TextView availableRidesTitle;

    // FIREBASE TODO: Replace this with Firebase data models
    private List<RideData> availableRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        setupClickListeners();
        // FIREBASE TODO: Replace with Firebase data loading
        loadHardcodedRides();
    }

    private void initializeViews() {
        pickupInput = findViewById(R.id.pickup_input);
        destinationInput = findViewById(R.id.destination_input);
        dateInput = findViewById(R.id.date_input);
        timeInput = findViewById(R.id.time_input);
        searchButton = findViewById(R.id.search_button);
        backButton = findViewById(R.id.back_button);
        ridesContainer = findViewById(R.id.rides_container);
        noRidesLayout = findViewById(R.id.no_rides_layout);
        availableRidesTitle = findViewById(R.id.available_rides_title);
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
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
                R.style.CustomDatePickerTheme, // Add this line
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
                R.style.CustomTimePickerTheme, // Add this line for custom theme
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

    private void performSearch() {
        String pickup = pickupInput.getText().toString().trim();
        String destination = destinationInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();

        // Basic validation
        if (pickup.isEmpty() || destination.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // FIREBASE TODO: Replace this with Firebase query
        // Example: Query rides where pickup matches and destination matches and date matches
        searchRidesInFirebase(pickup, destination, date, time);
    }

    // FIREBASE TODO: Replace this entire method with Firebase query
    private void searchRidesInFirebase(String pickup, String destination, String date, String time) {
        // This is hardcoded data - replace with Firebase query
        // Firebase query should look like:
        // DatabaseReference ridesRef = FirebaseDatabase.getInstance().getReference("rides");
        // Query query = ridesRef.orderByChild("pickup").equalTo(pickup);
        // Then filter by destination, date, etc.

        List<RideData> searchResults = new ArrayList<>();

        // Hardcoded search simulation
        for (RideData ride : availableRides) {
            if (ride.getPickup().toLowerCase().contains(pickup.toLowerCase()) &&
                    ride.getDestination().toLowerCase().contains(destination.toLowerCase())) {
                searchResults.add(ride);
            }
        }

        displaySearchResults(searchResults);
    }

    private void displaySearchResults(List<RideData> rides) {
        ridesContainer.removeAllViews();

        if (rides.isEmpty()) {
            availableRidesTitle.setVisibility(View.GONE);
            ridesContainer.setVisibility(View.GONE);
            noRidesLayout.setVisibility(View.VISIBLE);
        } else {
            availableRidesTitle.setVisibility(View.VISIBLE);
            ridesContainer.setVisibility(View.VISIBLE);
            noRidesLayout.setVisibility(View.GONE);

            for (RideData ride : rides) {
                addRideView(ride);
            }
        }
    }

    private void addRideView(RideData ride) {
        View rideView = LayoutInflater.from(this).inflate(R.layout.ride_item, ridesContainer, false);

        TextView driverName = rideView.findViewById(R.id.driver_name);
        TextView ridePrice = rideView.findViewById(R.id.ride_price);
        TextView pickupPoint = rideView.findViewById(R.id.pickup_point);
        TextView destinationPoint = rideView.findViewById(R.id.destination_point);
        TextView departureTime = rideView.findViewById(R.id.departure_time);
        TextView availableSeats = rideView.findViewById(R.id.available_seats);
        Button bookButton = rideView.findViewById(R.id.book_button);

        // FIREBASE TODO: Get actual data from Firebase ride object
        driverName.setText(ride.getDriverName());

        // Handle free rides display
        if (ride.getPrice() == 0) {
            ridePrice.setText("FREE");
            ridePrice.setTextColor(getColor(R.color.blue_accent)); // Keep blue color
        } else {
            ridePrice.setText("৳" + ride.getPrice());
        }

        pickupPoint.setText(ride.getPickup());
        destinationPoint.setText(ride.getDestination());
        departureTime.setText(ride.getDepartureTime());
        availableSeats.setText(ride.getAvailableSeats() + " seats left");

        // Handle book button click
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to booking confirmation with ride data
                Intent intent = new Intent(FindRideActivity.this, BookingConfirmationActivity.class);

                // FIREBASE TODO: Pass Firebase ride ID instead of hardcoded data
                intent.putExtra("driverName", ride.getDriverName());
                intent.putExtra("pickup", ride.getPickup());
                intent.putExtra("destination", ride.getDestination());
                intent.putExtra("departureTime", ride.getDepartureTime());
                intent.putExtra("price", ride.getPrice());
                intent.putExtra("availableSeats", ride.getAvailableSeats());
                intent.putExtra("isFree", ride.getPrice() == 0); // Add this flag

                startActivity(intent);
            }
        });

        ridesContainer.addView(rideView);
    }



    // FIREBASE TODO: Remove this method and load data from Firebase
    private void loadHardcodedRides() {
        availableRides = new ArrayList<>();
        availableRides.add(new RideData("Ahmed Khan", "IUT Main Gate", "Dhanmondi", "9:00 AM", 2, 150));
        availableRides.add(new RideData("Sara Islam", "IUT Back Gate", "Gulshan", "10:30 AM", 3, 0)); // FREE ride
        availableRides.add(new RideData("Rafiq Ahmed", "IUT Main Gate", "Old Dhaka", "2:00 PM", 1, 120));
        availableRides.add(new RideData("Nasir Uddin", "IUT Main Gate", "Uttara", "4:15 PM", 2, 0)); // FREE ride
    }


    // FIREBASE TODO: Replace this with Firebase data model
    private static class RideData {
        private String driverName;
        private String pickup;
        private String destination;
        private String departureTime;
        private int availableSeats;
        private int price;

        public RideData(String driverName, String pickup, String destination,
                        String departureTime, int availableSeats, int price) {
            this.driverName = driverName;
            this.pickup = pickup;
            this.destination = destination;
            this.departureTime = departureTime;
            this.availableSeats = availableSeats;
            this.price = price;
        }

        // Getters
        public String getDriverName() { return driverName; }
        public String getPickup() { return pickup; }
        public String getDestination() { return destination; }
        public String getDepartureTime() { return departureTime; }
        public int getAvailableSeats() { return availableSeats; }
        public int getPrice() { return price; }
    }
}
