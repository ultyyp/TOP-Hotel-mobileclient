package com.example.top_hotel_mobileapp.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.model.Room;

import java.util.Calendar;

public class BookRoomFragment extends Fragment {
    private ImageView roomImage;
    private TextView roomType, roomPrice;
    private EditText checkInDate, checkOutDate, numOfAdults, numOfChildren;
    private Button continueButton;
    private Room selectedRoom;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_room, container, false);

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        roomImage = view.findViewById(R.id.roomImage);
        roomType = view.findViewById(R.id.roomType);
        roomPrice = view.findViewById(R.id.roomPrice);
        checkInDate = view.findViewById(R.id.checkInDate);
        checkOutDate = view.findViewById(R.id.checkOutDate);
        numOfAdults = view.findViewById(R.id.numOfAdults);
        numOfChildren = view.findViewById(R.id.numOfChildren);
        continueButton = view.findViewById(R.id.continueButton);

        // Retrieve the selected room from the arguments
        selectedRoom = (Room) getArguments().getSerializable("selectedRoom");

        // Set room image, type, and price
        roomType.setText(selectedRoom.getRoomType());
        roomPrice.setText("$" + selectedRoom.getRoomPrice() + " / Night");

        // Load the room image from the base64 string
        String base64Image = selectedRoom.getPhoto();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            roomImage.setImageBitmap(decodedByte);
        }

        // Set up date pickers for check-in and check-out dates
        setupDatePicker(checkInDate);
        setupDatePicker(checkOutDate);

        // Validate form to enable "Continue" button only when all mandatory fields are filled
        validateForm();

        // Handle continue button click
        continueButton.setOnClickListener(v -> {
            String checkIn = checkInDate.getText().toString();
            String checkOut = checkOutDate.getText().toString();
            if (!isCheckOutAfterCheckIn(checkIn, checkOut)) {
                Toast.makeText(getContext(), "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show();
            } else {
                proceedToPayment();
            }
        });

        return view;
    }

    private void setupDatePicker(EditText dateField) {
        dateField.setOnClickListener(v -> {
            // Get the current date as default
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show a DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Set selected date in the field (format: YYYY-MM-DD)
                        dateField.setText(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void validateForm() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String checkIn = checkInDate.getText().toString();
                String checkOut = checkOutDate.getText().toString();
                String adults = numOfAdults.getText().toString();

                boolean isValid = !checkIn.isEmpty() && !checkOut.isEmpty() &&
                        !adults.isEmpty() && Integer.parseInt(adults) >= 1;
                continueButton.setEnabled(isValid);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        };

        checkInDate.addTextChangedListener(textWatcher);
        checkOutDate.addTextChangedListener(textWatcher);
        numOfAdults.addTextChangedListener(textWatcher);
    }

    private boolean isCheckOutAfterCheckIn(String checkIn, String checkOut) {
        // Assuming check-in and check-out are in "YYYY-MM-DD" format
        try {
            Calendar checkInCalendar = Calendar.getInstance();
            Calendar checkOutCalendar = Calendar.getInstance();
            String[] checkInParts = checkIn.split("-");
            String[] checkOutParts = checkOut.split("-");

            checkInCalendar.set(Integer.parseInt(checkInParts[0]), Integer.parseInt(checkInParts[1]) - 1, Integer.parseInt(checkInParts[2]));
            checkOutCalendar.set(Integer.parseInt(checkOutParts[0]), Integer.parseInt(checkOutParts[1]) - 1, Integer.parseInt(checkOutParts[2]));

            return checkOutCalendar.after(checkInCalendar);
        } catch (Exception e) {
            return false;
        }
    }

    private void proceedToPayment() {
        // Default number of children to 0 if emptyf
        String children = numOfChildren.getText().toString();
        if (children.isEmpty()) {
            children = "0";
        }

        // Fetch full name and email from SharedPreferences
        String fullName = sharedPreferences.getString("fullName", "John Doe");
        String email = sharedPreferences.getString("email", "john.doe@example.com");

        // Send data to PaymentFragment
        Bundle bundle = new Bundle();
        bundle.putString("roomId", selectedRoom.getId().toString());
        bundle.putString("email", email);
        bundle.putString("checkInDate", checkInDate.getText().toString());
        bundle.putString("checkOutDate", checkOutDate.getText().toString());
        bundle.putString("numOfAdults", numOfAdults.getText().toString());
        bundle.putString("numOfChildren", children);
        bundle.putString("roomPrice", selectedRoom.getRoomPrice());

        PaymentFragment paymentFragment = new PaymentFragment();
        paymentFragment.setArguments(bundle);

        // Replace current fragment with PaymentFragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentFragment)
                .addToBackStack(null)
                .commit();
    }

}
