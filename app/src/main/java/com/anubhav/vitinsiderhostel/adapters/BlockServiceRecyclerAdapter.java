package com.anubhav.vitinsiderhostel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.models.BlockService;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class BlockServiceRecyclerAdapter extends RecyclerView.Adapter<BlockServiceRecyclerAdapter.ViewHolder> {


    private final List<BlockService> blockServiceArrayList;
    private RecyclerBlockServiceCardClickListener recyclerCardViewClickListener;

    public BlockServiceRecyclerAdapter(List<BlockService> blockServiceArrayList, RecyclerBlockServiceCardClickListener listener) {
        this.blockServiceArrayList = blockServiceArrayList;
        this.recyclerCardViewClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_block_service, parent, false);
        return new BlockServiceRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.icon.setImageResource(blockServiceArrayList.get(position).getImageUrl());
        holder.serviceName.setText(blockServiceArrayList.get(position).getServiceName());
        holder.cardView.setOnClickListener(v -> {
            recyclerCardViewClickListener.onBlockServiceCardClickListener(position);
        });
    }

    @Override
    public int getItemCount() {
        return blockServiceArrayList.size();
    }

    public interface RecyclerBlockServiceCardClickListener {
        void onBlockServiceCardClickListener(int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        ImageView icon;
        MaterialTextView serviceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.blockServiceCard);
            icon = itemView.findViewById(R.id.blockServiceCardImage);
            serviceName = itemView.findViewById(R.id.blockServiceCardText);
        }

    }
}
