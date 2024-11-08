package com.example.top_hotel_mobileapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.network.ApiClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PaymentFragment extends Fragment {

    private ApiClient apiClient;

    private EditText etCheckInDate;
    private EditText etCheckOutDate;
    private EditText etNumAdults;
    private EditText etNumChildren;

    private String roomId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        apiClient = new ApiClient(requireContext());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        EditText etFirstName = view.findViewById(R.id.etFirstName);
        EditText etLastName = view.findViewById(R.id.etLastName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        etCheckInDate = view.findViewById(R.id.etCheckInDate);
        etCheckOutDate = view.findViewById(R.id.etCheckOutDate);
        // Set the date format to "yyyy-MM-dd"
        etCheckInDate.setHint("yyyy-MM-dd");
        etCheckOutDate.setHint("yyyy-MM-dd");
        etNumAdults = view.findViewById(R.id.etNumAdults);
        etNumChildren = view.findViewById(R.id.etNumChildren);
        Button btnSubmitBooking = view.findViewById(R.id.btnSubmitBooking);
        TextView tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

        // Retrieve data passed from BookRoomFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            roomId = bundle.getString("roomId", "");
            String email = bundle.getString("email", "");
            String firstName = sharedPreferences.getString("firstName", "");
            String lastName = sharedPreferences.getString("lastName", "");
            String checkInDate = bundle.getString("checkInDate", "");
            String checkOutDate = bundle.getString("checkOutDate", "");
            String numAdults = bundle.getString("numOfAdults", "1");
            String numChildren = bundle.getString("numOfChildren", "0");
            String roomPrice = bundle.getString("roomPrice", "0");

            // Fill in the EditText fields
            etFirstName.setText(firstName);
            etLastName.setText(lastName);
            etEmail.setText(email);
            etCheckInDate.setText(checkInDate);
            etCheckOutDate.setText(checkOutDate);
            etNumAdults.setText(numAdults);
            etNumChildren.setText(numChildren);

            // Calculate and display the total price
            int numNights = calculateNumOfNights(checkInDate, checkOutDate);
            double price = Double.parseDouble(roomPrice);
            double totalPrice = numNights * price;
            tvTotalPrice.setText("Total Price: $" + totalPrice);
        }

        btnSubmitBooking.setOnClickListener(v -> {
            String checkInDate = etCheckInDate.getText().toString().trim();
            String checkOutDate = etCheckOutDate.getText().toString().trim();

            // Ensure dates are in the "yyyy-MM-dd" format (add leading zero for day and month if necessary)
            checkInDate = formatDate(checkInDate);
            checkOutDate = formatDate(checkOutDate);

            // Use a DateTimeFormatter that allows single digit days or months
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                // Parse the string dates into LocalDate objects
                LocalDate parsedCheckInDate = LocalDate.parse(checkInDate, formatter);
                LocalDate parsedCheckOutDate = LocalDate.parse(checkOutDate, formatter);

                // Format the LocalDate objects back to strings, if necessary
                String formattedCheckInDate = parsedCheckInDate.format(formatter);
                String formattedCheckOutDate = parsedCheckOutDate.format(formatter);

                String numAdultsStr = etNumAdults.getText().toString().trim();
                String numChildrenStr = etNumChildren.getText().toString().trim();

                if (TextUtils.isEmpty(checkInDate) || TextUtils.isEmpty(checkOutDate) || TextUtils.isEmpty(numAdultsStr)) {
                    Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int numAdults = Integer.parseInt(numAdultsStr);
                int numChildren = TextUtils.isEmpty(numChildrenStr) ? 0 : Integer.parseInt(numChildrenStr);

                String guestEmail = bundle.getString("email", "");
                String firstName = sharedPreferences.getString("firstName", "");
                String lastName = sharedPreferences.getString("lastName", "");
                String guestFullName = firstName + " " + lastName;

                // Proceed with saving the booking using an anonymous callback
                apiClient.saveBooking(roomId, formattedCheckInDate, formattedCheckOutDate, guestEmail, guestFullName, numAdults, numChildren, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        requireActivity().runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Booking successful", Toast.LENGTH_SHORT).show();

                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new RoomsFragment())
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                if(response.code() == 400) {
                                    Toast.makeText(requireContext(), "Room Already Booked At That Date!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Invalid date format. Please use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private int calculateNumOfNights(String checkInDate, String checkOutDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);
            if (checkIn != null && checkOut != null && checkOut.after(checkIn)) {
                long diffInMillis = checkOut.getTime() - checkIn.getTime();
                return (int) (diffInMillis / (1000 * 60 * 60 * 24)); // Convert milliseconds to days
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method to add leading zero to single-digit day or month
    private String formatDate(String date) {
        String[] parts = date.split("-");
        String day = parts[2];
        String month = parts[1];

        // Add a leading zero if the day or month is a single digit
        if (day.length() == 1) {
            day = "0" + day;
        }
        if (month.length() == 1) {
            month = "0" + month;
        }

        return parts[0] + "-" + month + "-" + day;
    }
}
