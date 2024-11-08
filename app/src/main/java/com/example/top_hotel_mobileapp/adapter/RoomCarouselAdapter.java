package com.example.top_hotel_mobileapp.adapter;

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

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.fragment.BookRoomFragment;
import com.example.top_hotel_mobileapp.fragment.LoginFragment;
import com.example.top_hotel_mobileapp.model.Room;
import com.example.top_hotel_mobileapp.network.ApiClient;

import java.util.List;

public class RoomCarouselAdapter extends RecyclerView.Adapter<RoomCarouselAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private Context context;
    private SharedPreferences sharedPreferences;

    public RoomCarouselAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_carousel, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomTypeText.setText(room.getRoomType());
        holder.roomPriceText.setText("$" + room.getRoomPrice() + " / Night");

        // Load the room image from the Base64 string
        String base64Image = room.getPhoto();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.roomImage.setImageBitmap(decodedByte);
        }

        holder.itemView.setOnClickListener(v -> {
            String token = sharedPreferences.getString("token", null);

            if (token != null && !token.isEmpty()) {
                // Navigate to BookRoomFragment when a room is clicked
                BookRoomFragment bookRoomFragment = new BookRoomFragment();
                Bundle args = new Bundle();
                args.putSerializable("selectedRoom", room);  // Pass the Room object to the fragment
                bookRoomFragment.setArguments(args);

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, bookRoomFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Show a message prompting the user to log in
                // You can customize this based on how you want to handle the login state
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

    // Method to update the room list
    public void updateRooms(List<Room> newRoomList) {
        this.roomList = newRoomList;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
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
