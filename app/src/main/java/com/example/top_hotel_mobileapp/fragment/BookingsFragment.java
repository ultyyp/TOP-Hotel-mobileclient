package com.example.top_hotel_mobileapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.network.ApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class BookingsFragment extends Fragment {

    private EditText confirmationCodeEditText;
    private Button searchButton;
    private ApiClient apiClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        confirmationCodeEditText = view.findViewById(R.id.confirmationCodeEditText);
        searchButton = view.findViewById(R.id.searchButton);
        apiClient = new ApiClient(getContext());

        searchButton.setOnClickListener(v -> searchBooking());

        return view;
    }

    private void searchBooking() {
        String confirmationCode = confirmationCodeEditText.getText().toString().trim();

        if (confirmationCode.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a confirmation code", Toast.LENGTH_SHORT).show();
            return;
        }

        apiClient.getBookingByConfirmationCode(confirmationCode, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    // Parse JSON directly into variables
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

                    // Extract data from the response
                    JsonArray checkInDateArray = jsonObject.getAsJsonArray("checkInDate");
                    String checkInDate = String.format("%d-%02d-%02d", checkInDateArray.get(0).getAsInt(), checkInDateArray.get(1).getAsInt(), checkInDateArray.get(2).getAsInt());

                    JsonArray checkOutDateArray = jsonObject.getAsJsonArray("checkOutDate");
                    String checkOutDate = String.format("%d-%02d-%02d", checkOutDateArray.get(0).getAsInt(), checkOutDateArray.get(1).getAsInt(), checkOutDateArray.get(2).getAsInt());

                    String guestName = jsonObject.get("guestName").getAsString();
                    String guestEmail = jsonObject.get("guestEmail").getAsString();
                    int numOfAdults = jsonObject.get("numOfAdults").getAsInt();
                    int numOfChildren = jsonObject.get("numOfChildren").getAsInt();
                    int totalNumOfGuests = jsonObject.get("totalNumOfGuests").getAsInt();
                    String bookingId = jsonObject.get("id").getAsString();

                    JsonObject roomObject = jsonObject.getAsJsonObject("room");
                    String roomType = roomObject.get("roomType").getAsString();
                    double roomPrice = roomObject.get("roomPrice").getAsDouble();
                    boolean isBooked = roomObject.get("booked").getAsBoolean();

                    // Open the BookingInfoFragment with booking details
                    Bundle bundle = new Bundle();
                    bundle.putString("checkInDate", checkInDate);
                    bundle.putString("checkOutDate", checkOutDate);
                    bundle.putString("guestName", guestName);
                    bundle.putString("guestEmail", guestEmail);
                    bundle.putInt("numOfAdults", numOfAdults);
                    bundle.putInt("numOfChildren", numOfChildren);
                    bundle.putInt("totalNumOfGuests", totalNumOfGuests);
                    bundle.putString("bookingId", bookingId);
                    bundle.putString("roomType", roomType);
                    bundle.putDouble("roomPrice", roomPrice);
                    bundle.putBoolean("isBooked", isBooked);

                    BookingInfoFragment bookingInfoFragment = new BookingInfoFragment();
                    bookingInfoFragment.setArguments(bundle);

                    // Replace the current fragment with BookingInfoFragment
                    getActivity().runOnUiThread(() -> {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, bookingInfoFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    });
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Booking not found", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
