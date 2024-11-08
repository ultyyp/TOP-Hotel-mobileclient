package com.example.top_hotel_mobileapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.top_hotel_mobileapp.model.Room;
import com.google.gson.Gson;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://192.168.1.5:8080";
    private final OkHttpClient client;
    private final SharedPreferences sharedPreferences;

    public ApiClient(Context context) {
        client = new OkHttpClient();
        sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }

    private String getToken() {
        return sharedPreferences.getString("token", "");
    }

    public void storeToken(String token) {
        sharedPreferences.edit().putString("token", token).apply();
    }

    private Request.Builder getRequestBuilderWithHeaders() {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + getToken())
                .addHeader("Content-Type", "application/json");
    }

    private Request.Builder getMultipartRequestBuilder() {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + getToken())
                .addHeader("Content-Type", "multipart/form-data");
    }

    public List<Room> getRooms() throws IOException {
        List<Room> rooms = new ArrayList<>();
        Request request = getRequestBuilderWithHeaders()
                .url(BASE_URL + "/rooms/all-rooms")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JSONArray jsonArray = new JSONArray(responseBody);

            // Loop through the JSON array and create Room objects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject roomJson = jsonArray.getJSONObject(i);
                Long id = roomJson.getLong("id");
                String roomType = roomJson.getString("roomType");
                String roomPrice = roomJson.getString("roomPrice");
                boolean isBooked = roomJson.getBoolean("booked");
                String photo = roomJson.getString("photo");

                // Create a Room object and add it to the list
                Room room = new Room(id, roomType, roomPrice, isBooked, photo);
                rooms.add(room);
            }

        } catch (JSONException e) {
            Log.e("ApiClient", "JSON parsing error", e);
        }

        return rooms;
    }

    public List<String> getRoomTypes() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/rooms/room/types")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Assuming response is a JSON array of strings
                String responseBody = response.body().string();
                // Use Gson to convert the JSON array to a List of Strings
                return new Gson().fromJson(responseBody, List.class);
            } else {
                throw new IOException("Error fetching room types: " + response.message());
            }
        }
    }


    public void login(String email, String password, Callback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/login")
                .post(body)
                .build();

        // Execute request asynchronously
        client.newCall(request).enqueue(callback);
    }

    // New method for registering a user
    public void registerUser(String firstName, String lastName, String email, String password, Callback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("firstName", firstName);
            jsonBody.put("lastName", lastName);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/register-user")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getUser(String email, Callback callback) {
        Request request = getRequestBuilderWithHeaders()
                .url(BASE_URL + "/users/" + email)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getBookingsByEmail(String email, Callback callback) {
        Request request = getRequestBuilderWithHeaders()
                .url(BASE_URL + "/bookings/user/" + email + "/bookings")
                .get()
                .build();

        client.newCall(request).enqueue(callback); // This will run asynchronously
    }

    public void saveBooking(String roomId, String checkInDate, String checkOutDate, String guestEmail, String guestFullName, int numAdults, int numChildren, Callback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("checkInDate", checkInDate);
            Log.d("e", checkInDate);
            jsonBody.put("checkOutDate", checkOutDate);
            jsonBody.put("guestEmail", guestEmail);
            jsonBody.put("guestFullName", guestFullName);
            jsonBody.put("numOfAdults", numAdults);
            jsonBody.put("numOfChildren", numChildren);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // If the JSON fails to construct, exit the method.
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = getRequestBuilderWithHeaders()
                .url(BASE_URL + "/bookings/room/" + roomId + "/booking")
                .post(body)
                .build();

        // Execute the request asynchronously and pass the callback to handle response or failure
        client.newCall(request).enqueue(callback);
    }

    // Add new method for getting booking by confirmation code
    public void getBookingByConfirmationCode(String confirmationCode, Callback callback) {
        Request request = getRequestBuilderWithHeaders()
                .url(BASE_URL + "/bookings/confirmation/" + confirmationCode)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    // Add new method for canceling booking
    public void cancelBooking(Long bookingId, Callback callback) {
        Request request = getRequestBuilderWithHeaders()
                .url(BASE_URL + "/bookings/booking/" + bookingId + "/delete")
                .delete()
                .build();

        client.newCall(request).enqueue(callback);
    }

}
