package com.example.top_hotel_mobileapp.model;

import java.io.Serializable;
import java.util.List;

public class BookedRoom implements Serializable {
    private Long id;
    private String checkInDate;
    private String checkOutDate;
    private String guestName;
    private String guestEmail;
    private int numOfAdults;
    private int numOfChildren;
    private int totalNumOfGuests;
    private String bookingConfirmationCode;
    private Room room;

    // Getters and Setters for BookedRoom fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    public int getNumOfAdults() { return numOfAdults; }
    public void setNumOfAdults(int numOfAdults) { this.numOfAdults = numOfAdults; }
    public int getNumOfChildren() { return numOfChildren; }
    public void setNumOfChildren(int numOfChildren) { this.numOfChildren = numOfChildren; }
    public int getTotalNumOfGuests() { return totalNumOfGuests; }
    public void setTotalNumOfGuests(int totalNumOfGuests) { this.totalNumOfGuests = totalNumOfGuests; }
    public String getBookingConfirmationCode() { return bookingConfirmationCode; }
    public void setBookingConfirmationCode(String bookingConfirmationCode) { this.bookingConfirmationCode = bookingConfirmationCode; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    // 7-parameter constructor to initialize the object
    public BookedRoom(String id, String roomId, String roomType, String checkInDate, String checkOutDate, String bookingConfirmationCode) {
        this.id = Long.parseLong(id);
        this.checkInDate = checkInDate; // You can format this date further if needed
        this.checkOutDate = checkOutDate; // You can format this date further if needed
        this.bookingConfirmationCode = bookingConfirmationCode;
        this.room = new Room(Long.parseLong(roomId), roomType, "", false, ""); // Assuming Room constructor with id, roomType, roomPrice, isBooked, photo
    }

    // Method to get status based on checkInDate, checkOutDate, and current date
    public String getStatus() {
        // Get the current date
        String currentDate = java.time.LocalDate.now().toString();

        // Compare currentDate with checkInDate and checkOutDate
        if (currentDate.compareTo(checkInDate) >= 0 && currentDate.compareTo(checkOutDate) <= 0) {
            return "Ongoing";
        } else {
            return "Booked";
        }
    }
}
