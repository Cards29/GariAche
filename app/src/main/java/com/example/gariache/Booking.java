package com.example.gariache;

public class Booking {
    private int id;
    private int rideId;
    private int passengerId;
    private int bookedSeats;
    private String passengerPhone;
    private String notes;
    private String bookingDate; // e.g., "2025-09-01 15:00"
    private String status;      // e.g., confirmed/completed/cancelled

    public Booking(int id, int rideId, int passengerId, int bookedSeats, String passengerPhone,
                   String notes, String bookingDate, String status) {
        this.id = id;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.bookedSeats = bookedSeats;
        this.passengerPhone = passengerPhone;
        this.notes = notes;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Getters and Setters for all fields...
    // (autogenerate in Android Studio)
}
