package com.example.gariache;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.BookingViewHolder> {

    private Context context;
    private List<ViewMyRidesActivity.BookingWithRideInfo> bookingsList;
    private DatabaseHelper dbHelper;

    public BookingsAdapter(Context context, List<ViewMyRidesActivity.BookingWithRideInfo> bookingsList) {
        this.context = context;
        this.bookingsList = bookingsList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        ViewMyRidesActivity.BookingWithRideInfo booking = bookingsList.get(position);

        // Set booking details
        holder.bookingId.setText("#GR" + booking.bookingId);
        holder.bookingStatus.setText(booking.bookingStatus.toUpperCase());
        holder.driverName.setText(booking.driverName);
        holder.routeInfo.setText(booking.pickupPoint + " → " + booking.destination);
        holder.rideTime.setText(booking.rideDate + ", " + booking.rideTime);
        holder.bookedSeats.setText(booking.bookedSeats + " seat" + (booking.bookedSeats > 1 ? "s" : ""));

        // Set total amount
        if (booking.pricePerSeat == 0) {
            holder.totalAmount.setText("FREE");
        } else {
            int total = booking.pricePerSeat * booking.bookedSeats;
            holder.totalAmount.setText("৳" + total);
        }

        // Check if cancellation is allowed (30+ minutes before ride)
        boolean canCancel = canCancelBooking(booking.rideDate, booking.rideTime);

        if (canCancel && booking.bookingStatus.equals("confirmed")) {
            holder.cancelButton.setEnabled(true);
            holder.cancelButton.setText("Cancel Booking");
            holder.cancelButton.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_red_dark));
        } else if (!canCancel) {
            holder.cancelButton.setEnabled(false);
            holder.cancelButton.setText("Cannot Cancel (< 30 min)");
            holder.cancelButton.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            holder.cancelButton.setEnabled(false);
            holder.cancelButton.setText("Already " + booking.bookingStatus);
            holder.cancelButton.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.darker_gray));
        }

        // Handle cancel button click
        holder.cancelButton.setOnClickListener(v -> {
            if (canCancel && booking.bookingStatus.equals("confirmed")) {
                showCancelConfirmationDialog(booking, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    private boolean canCancelBooking(String rideDate, String rideTime) {
        try {
            // Parse ride date and time
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String rideDateTime = rideDate + " " + rideTime;
            Date rideDate_parsed = dateTimeFormat.parse(rideDateTime);

            if (rideDate_parsed != null) {
                // Get current time
                Calendar now = Calendar.getInstance();
                Calendar rideCalendar = Calendar.getInstance();
                rideCalendar.setTime(rideDate_parsed);

                // Calculate difference in minutes
                long diffInMillis = rideCalendar.getTimeInMillis() - now.getTimeInMillis();
                long diffInMinutes = diffInMillis / (60 * 1000);

                // Allow cancellation if more than 30 minutes remain
                return diffInMinutes > 30;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showCancelConfirmationDialog(ViewMyRidesActivity.BookingWithRideInfo booking, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?\n\nBooking ID: #GR" + booking.bookingId)
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    cancelBooking(booking, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelBooking(ViewMyRidesActivity.BookingWithRideInfo booking, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Update booking status to cancelled
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_BOOKING_STATUS, "cancelled");

            String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
            String[] whereArgs = {String.valueOf(booking.bookingId)};

            int rowsAffected = db.update(DatabaseHelper.TABLE_BOOKINGS, values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                // Update booking status in the list
                booking.bookingStatus = "cancelled";

                // Notify adapter of the change
                notifyItemChanged(position);

                Toast.makeText(context, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();

                // Refresh the parent activity if possible
                if (context instanceof ViewMyRidesActivity) {
                    ((ViewMyRidesActivity) context).refreshBookings();
                }
            } else {
                Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error cancelling booking", Toast.LENGTH_SHORT).show();
        }
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingId, bookingStatus, driverName, routeInfo, rideTime, bookedSeats, totalAmount;
        Button cancelButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingId = itemView.findViewById(R.id.booking_id);
            bookingStatus = itemView.findViewById(R.id.booking_status);
            driverName = itemView.findViewById(R.id.driver_name);
            routeInfo = itemView.findViewById(R.id.route_info);
            rideTime = itemView.findViewById(R.id.ride_time);
            bookedSeats = itemView.findViewById(R.id.booked_seats);
            totalAmount = itemView.findViewById(R.id.total_amount);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }
    }
}
