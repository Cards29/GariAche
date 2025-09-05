package com.example.gariache;

public class Ride {
    private String id;
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
    private String status; // e.g., active/completed/cancelled

    // No-arg constructor for Firebase
    public Ride() {}

    public Ride(String id, int driverId, String pickupPoint, String destination, String date, String time,
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

    // Getters
    public String getId() { return id; }
    public int getDriverId() { return driverId; }
    public String getPickupPoint() { return pickupPoint; }
    public String getDestination() { return destination; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getAvailableSeats() { return availableSeats; }
    public int getPricePerSeat() { return pricePerSeat; }
    public String getCarModel() { return carModel; }
    public String getCarColor() { return carColor; }
    public String getLicensePlate() { return licensePlate; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setPricePerSeat(int pricePerSeat) { this.pricePerSeat = pricePerSeat; }
    public void setCarModel(String carModel) { this.carModel = carModel; }
    public void setCarColor(String carColor) { this.carColor = carColor; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setStatus(String status) { this.status = status; }
}
