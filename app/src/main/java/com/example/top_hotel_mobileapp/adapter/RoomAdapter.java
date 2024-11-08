package com.example.top_hotel_mobileapp.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.fragment.BookRoomFragment;
import com.example.top_hotel_mobileapp.fragment.LoginFragment;
import com.example.top_hotel_mobileapp.model.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private SharedPreferences sharedPreferences;
    private Context context;


    public RoomAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the individual room item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomTypeText.setText(room.getRoomType());
        holder.roomPriceText.setText("$" + room.getRoomPrice() + " / Night");

        // Load the room image from the base64 string
        String base64Image = room.getPhoto();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.roomImage.setImageBitmap(decodedByte);
        }

        holder.itemView.setOnClickListener(v -> {

            // Always get the latest token
            String token = sharedPreferences.getString("token", null);

            if (token != null && !token.isEmpty()) {
                // Create a new fragment instance
                BookRoomFragment bookRoomFragment = new BookRoomFragment();

                // Put the Room object in a Bundle
                Bundle args = new Bundle();
                args.putSerializable("selectedRoom", room); // Pass the room object
                bookRoomFragment.setArguments(args);

                // Now use FragmentManager to open the BookRoomFragment
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, bookRoomFragment) // Replace with your fragment container ID
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(context, "Please Login Before Booking!", Toast.LENGTH_SHORT).show();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }



        });

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    // Method to update the room list when data changes
    public void updateRooms(List<Room> newRoomList) {
        this.roomList = newRoomList;
        notifyDataSetChanged();  // Notify the adapter that data has changed and it should update the views
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        TextView roomTypeText, roomPriceText;
        ImageView roomImage;

        public RoomViewHolder(View itemView) {
            super(itemView);
            roomTypeText = itemView.findViewById(R.id.roomTypeText);
            roomPriceText = itemView.findViewById(R.id.roomPriceText);
            roomImage = itemView.findViewById(R.id.roomImage);
        }
    }
}
