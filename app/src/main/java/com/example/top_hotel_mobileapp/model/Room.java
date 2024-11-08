package com.example.top_hotel_mobileapp.model;

import java.io.Serializable;

public class Room implements Serializable {
    private Long id; // Room ID
    private String roomType; // Type of the room
    private String roomPrice; // Price of the room
    private boolean isBooked; // Booking status
    private String photo; // Base64 encoded string for room's photo

    // Constructor with all fields
    public Room(Long id, String roomType, String roomPrice, boolean isBooked, String photo) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.photo = photo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(String roomPrice) {
        this.roomPrice = roomPrice;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    // Method to retrieve the room photo as a Base64 encoded string
    public String getPhotoBase64() {
        return this.photo;  // Simply returns the photo field, which should be Base64 encoded
    }
}
