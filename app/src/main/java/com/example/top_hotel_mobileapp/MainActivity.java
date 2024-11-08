package com.example.top_hotel_mobileapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.top_hotel_mobileapp.fragment.BookingsFragment;
import com.example.top_hotel_mobileapp.fragment.HomeFragment;
import com.example.top_hotel_mobileapp.fragment.LoginFragment;
import com.example.top_hotel_mobileapp.fragment.ProfileFragment;
import com.example.top_hotel_mobileapp.fragment.RoomsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        Fragment initialFragment = new HomeFragment();

        // Load the initial fragment (Profile or Login)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, initialFragment).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();

                // Always get the latest token
                String token = sharedPreferences.getString("token", null);

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_rooms) {
                    selectedFragment = new RoomsFragment();
                } else if (itemId == R.id.nav_bookings) {
                    // If logged in, show BookingsFragment, otherwise show LoginFragment
                    if (token != null && !token.isEmpty()) {
                        selectedFragment = new BookingsFragment();
                    } else {
                        selectedFragment = new LoginFragment();
                    }
                } else if (itemId == R.id.nav_profile) {
                    // If logged in, show ProfileFragment, otherwise show LoginFragment
                    if (token != null && !token.isEmpty()) {
                        selectedFragment = new ProfileFragment();
                    } else {
                        selectedFragment = new LoginFragment();
                    }
                }

                // Replace the fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
    }
}
