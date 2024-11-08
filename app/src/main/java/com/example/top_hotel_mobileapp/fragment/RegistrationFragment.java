package com.example.top_hotel_mobileapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.network.ApiClient;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

import android.widget.CheckBox;
import android.widget.TextView;

public class RegistrationFragment extends Fragment {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private CheckBox termsCheckBox;
    private TextView signInText;
    private ApiClient apiClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        // Initialize views
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        registerButton = view.findViewById(R.id.registerButton);
        termsCheckBox = view.findViewById(R.id.termsCheckBox);
        signInText = view.findViewById(R.id.signInText);

        // Initialize the ApiClient
        apiClient = new ApiClient(getContext());

        // Styling the "Sign In" part of the text
        String signInTextContent = "Already have an account? Sign In";
        SpannableString spannable = new SpannableString(signInTextContent);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), signInTextContent.indexOf("Sign In"), signInTextContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signInText.setText(spannable);

        // Click listener for "Sign In"
        signInText.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        });

        // Register button logic with terms agreement check
        registerButton.setOnClickListener(v -> {
            if (!termsCheckBox.isChecked()) {
                Toast.makeText(getContext(), "You must agree with the terms and conditions to register.", Toast.LENGTH_SHORT).show();
                return;
            }
            registerUser();
        });

        return view;
    }

    private void registerUser() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the registerUser method on the ApiClient
        apiClient.registerUser(firstName, lastName, email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new LoginFragment())
                                .commit();
                    } else {
                        Toast.makeText(getContext(), "Registration failed: User already exists!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

