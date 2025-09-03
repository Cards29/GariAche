package com.example.gariache;

public class Ride {
    private int id;
    private int driverId; // Foreign key to User.id
    private String pickupPoint;
    private String destination;
    private String date;
    private String time;
    private int availableSeats;
    private int pricePerSeat; // 0 for free ride
    private String carModel;
    private String carColor;
    private String licensePlate;
    private String notes;
    private String status;     // e.g., active/completed/cancelled

    public Ride(int id, int driverId, String pickupPoint, String destination, String date, String time,
                int availableSeats, int pricePerSeat, String carModel, String carColor, String licensePlate, String notes, String status) {
        this.id = id;
        this.driverId = driverId;
        this.pickupPoint = pickupPoint;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.availableSeats = availableSeats;
        this.pricePerSeat = pricePerSeat;
        this.carModel = carModel;
        this.carColor = carColor;
        this.licensePlate = licensePlate;
        this.notes = notes;
        this.status = status;
    }

    // Getters and Setters for all fields...
    // (autogenerate in Android Studio)
}
