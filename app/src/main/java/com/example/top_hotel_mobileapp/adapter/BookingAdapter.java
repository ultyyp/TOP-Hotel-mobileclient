package com.example.top_hotel_mobileapp.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.top_hotel_mobileapp.R;
import com.example.top_hotel_mobileapp.model.BookedRoom;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<BookedRoom> bookings;

    public BookingAdapter(List<BookedRoom> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookedRoom booking = bookings.get(position);
        holder.bookingId.setText(String.valueOf(booking.getId()));
        holder.roomId.setText(String.valueOf(booking.getRoom().getId()));
        holder.roomType.setText(booking.getRoom().getRoomType());
        holder.checkInDate.setText(booking.getCheckInDate());
        holder.checkOutDate.setText(booking.getCheckOutDate());
        holder.confirmationCode.setText(booking.getBookingConfirmationCode());
        holder.status.setText(String.valueOf(booking.getStatus()));

        // Set the click listener for the entire booking item (itemView)
        holder.itemView.setOnClickListener(v -> {
            // Get the confirmation code from the clicked booking
            String confirmationCode = booking.getBookingConfirmationCode();

            // Get the system ClipboardManager
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Confirmation Code", confirmationCode);
            clipboard.setPrimaryClip(clip);

            // Show a toast indicating the confirmation code was copied
            Toast.makeText(v.getContext(), "Confirmation code copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }



    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingId, roomId, roomType, checkInDate, checkOutDate, confirmationCode, status;

        BookingViewHolder(View itemView) {
            super(itemView);
            bookingId = itemView.findViewById(R.id.bookingId);
            roomId = itemView.findViewById(R.id.roomId);
            roomType = itemView.findViewById(R.id.roomType);
            checkInDate = itemView.findViewById(R.id.checkInDate);
            checkOutDate = itemView.findViewById(R.id.checkOutDate);
            confirmationCode = itemView.findViewById(R.id.confirmationCode);
            status = itemView.findViewById(R.id.status);
        }
    }
}
