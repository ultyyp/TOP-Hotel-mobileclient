package com.example.top_hotel_mobileapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.adapter.RoomAdapter;
import com.example.top_hotel_mobileapp.model.Room;
import com.example.top_hotel_mobileapp.network.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomsFragment extends Fragment {

    private ApiClient apiClient;
    private RecyclerView roomsRecyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList = new ArrayList<>();
    private List<Room> filteredList = new ArrayList<>();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private Spinner sortSpinner;
    private Button prevPageBtn, nextPageBtn;
    private TextView pageNumberText;
    private int currentPage = 1;
    private final int roomsPerPage = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        // Initialize RecyclerView and Adapter
        roomsRecyclerView = view.findViewById(R.id.roomsRecyclerView);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        roomAdapter = new RoomAdapter(filteredList, getContext());
        roomsRecyclerView.setAdapter(roomAdapter);

        // Initialize pagination buttons and page text
        prevPageBtn = view.findViewById(R.id.prevPageBtn);
        nextPageBtn = view.findViewById(R.id.nextPageBtn);
        pageNumberText = view.findViewById(R.id.pageNumber);

        prevPageBtn.setOnClickListener(v -> changePage(currentPage - 1));
        nextPageBtn.setOnClickListener(v -> changePage(currentPage + 1));

        // Initialize sorting spinner
        sortSpinner = view.findViewById(R.id.sortSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRoomType = (String) parentView.getItemAtPosition(position);
                filterRoomsByType(selectedRoomType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Initialize the API client and load rooms
        apiClient = new ApiClient(requireContext());
        loadRoomTypes();
        loadRooms();  // Initial rooms load

        return view;
    }

    private void loadRoomTypes() {
        new Thread(() -> {
            try {
                List<String> roomTypes = apiClient.getRoomTypes();
                mainHandler.post(() -> {
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) sortSpinner.getAdapter();
                    adapter.clear();
                    adapter.add("All Types"); // Add an "All Types" option to show all rooms
                    adapter.addAll(roomTypes);
                    adapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                Log.e("RoomsFragment", "Error fetching room types", e);
            }
        }).start();
    }

    private void loadRooms() {
        new Thread(() -> {
            try {
                List<Room> rooms = apiClient.getRooms();
                Log.d("Rooms Loaded", "Rooms fetched successfully");
                roomList.clear();
                roomList.addAll(rooms);
                mainHandler.post(() -> {
                    filteredList.clear();
                    filteredList.addAll(roomList);  // Add all rooms initially
                    changePage(1); // Show first page after loading data
                    roomAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                Log.e("RoomsFragment", "Error fetching rooms", e);
            }
        }).start();
    }

    private void changePage(int page) {
        // Ensure the requested page is valid and doesn't go out of bounds
        int totalPages = (int) Math.ceil((double) filteredList.size() / roomsPerPage);
        if (page < 1 || page > totalPages) return; // Page number out of range

        currentPage = page;
        int start = (currentPage - 1) * roomsPerPage;
        int end = Math.min(start + roomsPerPage, filteredList.size());

        // Only try to create a sublist if start < end
        if (start < end) {
            List<Room> pageRooms = filteredList.subList(start, end);
            roomAdapter.updateRooms(pageRooms); // Update the adapter with the new list of rooms
        }

        updatePageNumber();
    }

    private void updatePageNumber() {
        int totalPages = (int) Math.ceil((double) filteredList.size() / roomsPerPage);
        pageNumberText.setText("Page " + currentPage + " of " + totalPages);
    }

    private void filterRoomsByType(String roomType) {
        List<Room> filteredRooms = new ArrayList<>();
        for (Room room : roomList) {
            if ("All Types".equals(roomType) || room.getRoomType().equals(roomType)) {
                filteredRooms.add(room);
            }
        }
        // Update filtered list with rooms based on the selected type
        filteredList = filteredRooms;
        // Now update the filteredList and pagination
        changePage(1);
    }
}


