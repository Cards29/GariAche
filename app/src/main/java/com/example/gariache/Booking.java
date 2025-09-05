package com.example.gariache;

public class Booking {
    private String id;
    private int rideId;
    private int passengerId;
    private int bookedSeats;
    private String passengerPhone;
    private String notes;
    private String bookingDate; // e.g., "2025-09-01 15:00"
    private String status; // e.g., confirmed/completed/cancelled

    // No-arg constructor for Firebase
    public Booking() {}

    public Booking(String id, int rideId, int passengerId, int bookedSeats, String passengerPhone,
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

    // Getters
    public String getId() { return id; }
    public int getRideId() { return rideId; }
    public int getPassengerId() { return passengerId; }
    public int getBookedSeats() { return bookedSeats; }
    public String getPassengerPhone() { return passengerPhone; }
    public String getNotes() { return notes; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setRideId(int rideId) { this.rideId = rideId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }
    public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public void setStatus(String status) { this.status = status; }
}
