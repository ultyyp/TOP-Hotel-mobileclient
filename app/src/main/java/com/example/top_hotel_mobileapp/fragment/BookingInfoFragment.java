package com.example.top_hotel_mobileapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.model.BookedRoom;
import com.example.top_hotel_mobileapp.network.ApiClient;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BookingInfoFragment extends Fragment {

    private TextView bookingDetailsTextView;
    private Button cancelButton;
    private ApiClient apiClient;

    private String checkInDate, checkOutDate, guestName, guestEmail, bookingId, roomType;
    private int numOfAdults, numOfChildren, totalNumOfGuests;
    private double roomPrice;
    private boolean isBooked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_info, container, false);

        bookingDetailsTextView = view.findViewById(R.id.bookingDetailsTextView);
        cancelButton = view.findViewById(R.id.cancelButton);
        apiClient = new ApiClient(getContext());

        // Retrieve the booking details passed from BookingsFragment
        if (getArguments() != null) {
            checkInDate = getArguments().getString("checkInDate");
            checkOutDate = getArguments().getString("checkOutDate");
            guestName = getArguments().getString("guestName");
            guestEmail = getArguments().getString("guestEmail");
            numOfAdults = getArguments().getInt("numOfAdults");
            numOfChildren = getArguments().getInt("numOfChildren");
            totalNumOfGuests = getArguments().getInt("totalNumOfGuests");
            bookingId = getArguments().getString("bookingId");
            roomType = getArguments().getString("roomType");
            roomPrice = getArguments().getDouble("roomPrice");
            isBooked = getArguments().getBoolean("isBooked");

            // Display booking details
            bookingDetailsTextView.setText("Guest Name: " + guestName + "\n"
                    + "Guest Email: " + guestEmail + "\n"
                    + "Room Type: " + roomType + "\n"
                    + "Room Price: $" + roomPrice + "/Night\n"
                    + "Check-In: " + checkInDate + "\n"
                    + "Check-Out: " + checkOutDate + "\n"
                    + "Nº Of Adults: " + numOfAdults + "\n"
                    + "Nº Of Children: " + numOfChildren + "\n"
                    + "Total Guests: " + totalNumOfGuests + "\n");
        }

        cancelButton.setOnClickListener(v -> cancelBooking());

        return view;
    }

    private void cancelBooking() {
        if (bookingId != null && !bookingId.isEmpty()) {
            apiClient.cancelBooking(Long.parseLong(bookingId), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Booking canceled successfully", Toast.LENGTH_SHORT).show());
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new BookingsFragment())
                                .commit();
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to cancel booking", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }
}
