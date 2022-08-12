package com.anubhav.vitinsiderhostel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.interfaces.iOnRoomServiceCardClicked;
import com.anubhav.vitinsiderhostel.models.RoomService;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class RoomServiceRecyclerAdapter extends RecyclerView.Adapter<RoomServiceRecyclerAdapter.ViewHolder> {


    private final ArrayList<RoomService> roomServiceArrayList;
    private final iOnRoomServiceCardClicked onRoomServiceCardClicked;

    public RoomServiceRecyclerAdapter(ArrayList<RoomService> roomServiceArrayList,iOnRoomServiceCardClicked  listener) {
        this.roomServiceArrayList = roomServiceArrayList;
        onRoomServiceCardClicked = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_room_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.icon.setImageResource(roomServiceArrayList.get(position).getImageUrl());
        holder.serviceName.setText(roomServiceArrayList.get(position).getServiceName());
        holder.cardView.setOnClickListener(v -> {
            onRoomServiceCardClicked.roomServiceCardClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return roomServiceArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        ImageView icon;
        MaterialTextView serviceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.roomServiceCard);
            icon = itemView.findViewById(R.id.roomServiceCardImage);
            serviceName = itemView.findViewById(R.id.roomServiceCardText);
        }

    }


}
