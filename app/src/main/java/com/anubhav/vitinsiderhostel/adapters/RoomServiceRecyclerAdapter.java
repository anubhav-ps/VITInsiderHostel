package com.anubhav.vitinsiderhostel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.models.RoomService;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class RoomServiceRecyclerAdapter extends RecyclerView.Adapter<RoomServiceRecyclerAdapter.ViewHolder> {


    private final ArrayList<RoomService> roomServiceArrayList;
    private RecyclerCardViewClickListener recyclerCardViewClickListener;

    public RoomServiceRecyclerAdapter(ArrayList<RoomService> roomServiceArrayList, RecyclerCardViewClickListener listener) {
        this.roomServiceArrayList = roomServiceArrayList;
        recyclerCardViewClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_service_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.backgroundImage.setImageResource(roomServiceArrayList.get(position).getImageUrl());
        holder.serviceName.setText(roomServiceArrayList.get(position).getServiceName());
        holder.backgroundImage.setOnClickListener(v -> {
            recyclerCardViewClickListener.onCardItemClickListener(position);
        });
    }

    @Override
    public int getItemCount() {
        return roomServiceArrayList.size();
    }

    public interface RecyclerCardViewClickListener {
        void onCardItemClickListener(int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView backgroundImage;
        MaterialTextView serviceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundImage = itemView.findViewById(R.id.roomServiceCardImage);
            serviceName = itemView.findViewById(R.id.roomServiceCardText);
        }

    }


}
