package com.example.top_hotel_mobileapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.adapter.BookingAdapter;
import com.example.top_hotel_mobileapp.model.BookedRoom;
import com.example.top_hotel_mobileapp.model.Room;
import com.example.top_hotel_mobileapp.network.ApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private ApiClient apiClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        apiClient = new ApiClient(requireContext());

        // Profile fields
        TextView idValue = view.findViewById(R.id.id_value);
        TextView firstNameValue = view.findViewById(R.id.first_name_value);
        TextView emailValue = view.findViewById(R.id.email_value);
        TextView lastNameValue = view.findViewById(R.id.last_name_value);
        TextView rolesValue = view.findViewById(R.id.roles_value);
        RecyclerView recyclerView = view.findViewById(R.id.booking_list);

        // Populate profile data
        idValue.setText(sharedPreferences.getString("id", ""));
        emailValue.setText(sharedPreferences.getString("email", ""));
        firstNameValue.setText(sharedPreferences.getString("firstName", ""));
        lastNameValue.setText(sharedPreferences.getString("lastName", ""));
        rolesValue.setText(sharedPreferences.getString("roles", ""));

        // Initialize the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch bookings with callback
        fetchBookings(sharedPreferences.getString("email", ""), new BookingsCallback() {
            @Override
            public void onBookingsFetched(List<BookedRoom> bookings) {
                // Update RecyclerView with bookings
                BookingAdapter adapter = new BookingAdapter(bookings);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                // Show error message
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Logout button
        Button logoutButton = view.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> {
            // Clear token and redirect to login
            sharedPreferences.edit().remove("token").apply();
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

            // Switch to LoginFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        });

        return view;
    }

    // Define the callback interface for fetching bookings
    public interface BookingsCallback {
        void onBookingsFetched(List<BookedRoom> bookings);
        void onError(String errorMessage);
    }

    // Fetch bookings with the given email
    private void fetchBookings(String email, BookingsCallback callback) {
        apiClient.getBookingsByEmail(email, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> callback.onError("Error fetching bookings: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Parse bookings directly in the ProfileFragment
                        String responseBody = response.body().string();
                        List<BookedRoom> bookings = parseBookings(responseBody);

                        // Return bookings to the callback
                        requireActivity().runOnUiThread(() -> callback.onBookingsFetched(bookings));
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() -> callback.onError("Error parsing bookings"));
                        Log.d("e", "Error:" + e.toString());
                    }
                } else {
                    requireActivity().runOnUiThread(() -> callback.onError("Failed to fetch bookings with status code " + response.code()));
                }
            }
        });
    }

    // Parse bookings from the response JSON
    private List<BookedRoom> parseBookings(String responseBody) throws JSONException {
        List<BookedRoom> bookings = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseBody);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookingJson = jsonArray.getJSONObject(i);

            // Extract check-in and check-out dates (convert arrays to strings or Date)
            JSONArray checkInDateArray = bookingJson.getJSONArray("checkInDate");
            JSONArray checkOutDateArray = bookingJson.getJSONArray("checkOutDate");

            String checkInDate = checkInDateArray.getInt(0) + "-" + (checkInDateArray.getInt(1) + 1) + "-" + checkInDateArray.getInt(2); // "YYYY-MM-DD"
            String checkOutDate = checkOutDateArray.getInt(0) + "-" + (checkOutDateArray.getInt(1) + 1) + "-" + checkOutDateArray.getInt(2);

            // Parse room details from the booking JSON object
            JSONObject roomJson = bookingJson.getJSONObject("room");
            Room room = new Room(
                    roomJson.getLong("id"),
                    roomJson.getString("roomType"),
                    String.valueOf(roomJson.getDouble("roomPrice")), // Convert price to string
                    roomJson.getBoolean("booked"), // Correct field for booking status
                    roomJson.isNull("photo") ? null : roomJson.getString("photo") // Handle null photo field
            );

            // Create BookedRoom with parsed data
            BookedRoom bookedRoom = new BookedRoom(
                    bookingJson.getString("id"),
                    roomJson.getString("id"),  // Room ID as a string
                    roomJson.getString("roomType"),
                    checkInDate, // Use formatted date strings
                    checkOutDate,
                    bookingJson.getString("bookingConfirmationCode")
            );

            bookedRoom.setRoom(room); // Set the room details inside the bookedRoom object
            bookings.add(bookedRoom);
        }

        return bookings;
    }

}
