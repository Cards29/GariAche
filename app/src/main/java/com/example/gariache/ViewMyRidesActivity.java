package com.example.gariache;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewMyRidesActivity extends AppCompatActivity {

    private ImageView backButton;
    private RecyclerView bookingsRecyclerView;
    private LinearLayout noBookingsLayout;
    private BookingsAdapter bookingsAdapter;
    private DatabaseHelper dbHelper;
    private List<BookingWithRideInfo> bookingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_rides);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupClickListeners();
        loadMyBookings();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        bookingsRecyclerView = findViewById(R.id.bookings_recycler_view);
        noBookingsLayout = findViewById(R.id.no_bookings_layout);

        bookingsList = new ArrayList<>();
        bookingsAdapter = new BookingsAdapter(this, bookingsList);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingsRecyclerView.setAdapter(bookingsAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    private void loadMyBookings() {
        // Get current user ID
        SharedPreferences sharedPref = getSharedPreferences("GariAche", MODE_PRIVATE);
        int currentUserId = sharedPref.getInt("current_user_id", -1);

        if (currentUserId == -1) {
            showNoBookings();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to get bookings with ride and driver information
        String query = "SELECT " +
                "b.id as booking_id, " +
                "b.booked_seats, " +
                "b.passenger_phone, " +
                "b.notes as booking_notes, " +
                "b.booking_date, " +
                "b.status as booking_status, " +
                "r.pickup_point, " +
                "r.destination, " +
                "r.ride_date, " +
                "r.ride_time, " +
                "r.price_per_seat, " +
                "u.name as driver_name " +
                "FROM " + DatabaseHelper.TABLE_BOOKINGS + " b " +
                "JOIN " + DatabaseHelper.TABLE_RIDES + " r ON b.ride_id = r.id " +
                "JOIN " + DatabaseHelper.TABLE_USERS + " u ON r.driver_id = u.id " +
                "WHERE b.passenger_id = ? AND b.status != 'cancelled' " +
                "ORDER BY r.ride_date DESC, r.ride_time DESC";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(currentUserId)})) {
            bookingsList.clear();

            while (cursor.moveToNext()) {
                BookingWithRideInfo booking = new BookingWithRideInfo();
                booking.bookingId = cursor.getLong(cursor.getColumnIndexOrThrow("booking_id"));
                booking.bookedSeats = cursor.getInt(cursor.getColumnIndexOrThrow("booked_seats"));
                booking.passengerPhone = cursor.getString(cursor.getColumnIndexOrThrow("passenger_phone"));
                booking.bookingNotes = cursor.getString(cursor.getColumnIndexOrThrow("booking_notes"));
                booking.bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"));
                booking.bookingStatus = cursor.getString(cursor.getColumnIndexOrThrow("booking_status"));
                booking.pickupPoint = cursor.getString(cursor.getColumnIndexOrThrow("pickup_point"));
                booking.destination = cursor.getString(cursor.getColumnIndexOrThrow("destination"));
                booking.rideDate = cursor.getString(cursor.getColumnIndexOrThrow("ride_date"));
                booking.rideTime = cursor.getString(cursor.getColumnIndexOrThrow("ride_time"));
                booking.pricePerSeat = cursor.getInt(cursor.getColumnIndexOrThrow("price_per_seat"));
                booking.driverName = cursor.getString(cursor.getColumnIndexOrThrow("driver_name"));

                bookingsList.add(booking);
            }

            if (bookingsList.isEmpty()) {
                showNoBookings();
            } else {
                showBookings();
            }

            bookingsAdapter.notifyDataSetChanged();
        }
    }

    private void showBookings() {
        bookingsRecyclerView.setVisibility(View.VISIBLE);
        noBookingsLayout.setVisibility(View.GONE);
    }

    private void showNoBookings() {
        bookingsRecyclerView.setVisibility(View.GONE);
        noBookingsLayout.setVisibility(View.VISIBLE);
    }

    public void refreshBookings() {
        loadMyBookings();
    }

    // Data class to hold booking with ride information
    public static class BookingWithRideInfo {
        public long bookingId;
        public int bookedSeats;
        public String passengerPhone;
        public String bookingNotes;
        public String bookingDate;
        public String bookingStatus;
        public String pickupPoint;
        public String destination;
        public String rideDate;
        public String rideTime;
        public int pricePerSeat;
        public String driverName;
    }
}

