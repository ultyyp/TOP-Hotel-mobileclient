package com.example.top_hotel_mobileapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.top_hotel_mobileapp.R;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private ApiClient apiClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        apiClient = new ApiClient(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        EditText etUsername = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        TextView signUpText = view.findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                login(username, password);
            } else {
                Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
            }
        });


        // Styling the "Sign In" part of the text
        String signInTextContent = "Don't have an account yet? Sign Up";
        SpannableString spannable = new SpannableString(signInTextContent);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), signInTextContent.indexOf("Sign Up"), signInTextContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUpText.setText(spannable);

        // Click listener for "Sign In"
        signUpText.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegistrationFragment())
                    .commit();
        });


        return view;
    }

    // LoginFragment.java
    private void login(String username, String password) {
        apiClient.login(username, password, new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String email = jsonObject.getString("email");

                        //Store the token
                        apiClient.storeToken(jsonObject.getString("token"));

                        // Save email to shared preferences
                        sharedPreferences.edit().putString("email", email).apply();

                        // Fetch user details using the email
                        getUserDetails(email);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Login failed with status code " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void getUserDetails(String email) {
        apiClient.getUser(email, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject userJson = new JSONObject(responseBody);

                        // Parse user data and save to shared preferences
                        sharedPreferences.edit()
                                .putString("id", userJson.getString("id"))
                                .putString("firstName", userJson.getString("firstName"))
                                .putString("lastName", userJson.getString("lastName"))
                                .putString("email", userJson.getString("email"))
                                .apply();

                        // Parse roles
                        JSONArray rolesArray = userJson.getJSONArray("roles");
                        List<String> roles = new ArrayList<>();
                        for (int i = 0; i < rolesArray.length(); i++) {
                            roles.add(rolesArray.getJSONObject(i).getString("name"));
                        }
                        sharedPreferences.edit().putString("roles", roles.toString()).apply();

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "User details saved", Toast.LENGTH_SHORT).show();
                            // Navigate to ProfileFragment
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new ProfileFragment())
                                    .commit();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Error parsing user details", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to fetch user details with status code " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

}
