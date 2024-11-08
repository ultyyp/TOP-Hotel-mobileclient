package com.example.top_hotel_mobileapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.adapter.RoomCarouselAdapter;
import com.example.top_hotel_mobileapp.model.Room;
import com.example.top_hotel_mobileapp.network.ApiClient;

import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPagerRooms;
    private List<Room> roomList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the ViewPager
        viewPagerRooms = view.findViewById(R.id.viewPagerRooms);

        // Fetch rooms from the API asynchronously
        new FetchRoomsTask().execute();

        return view;
    }

    // AsyncTask to fetch rooms on a background thread
    private class FetchRoomsTask extends AsyncTask<Void, Void, List<Room>> {

        @Override
        protected List<Room> doInBackground(Void... voids) {
            ApiClient apiClient = new ApiClient(getContext());
            try {
                // Fetch the rooms list in the background
                return apiClient.getRooms();
            } catch (IOException e) {
                Log.e("HomeFragment", "Error fetching rooms: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Room> rooms) {
            if (rooms != null) {
                roomList = rooms;
                // Set up the adapter with the fetched data
                RoomCarouselAdapter roomCarouselAdapter = new RoomCarouselAdapter(roomList, getContext());
                viewPagerRooms.setAdapter(roomCarouselAdapter);
            } else {
                Log.e("HomeFragment", "Failed to fetch rooms.");
            }
        }
    }
}
