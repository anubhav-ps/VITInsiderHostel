package com.anubhav.vitinsiderhostel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.models.Featured;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class FeaturedMenuAdapter extends RecyclerView.Adapter<FeaturedMenuAdapter.ViewHolder> {


    private List<Featured> list;
    private Context context;


    public FeaturedMenuAdapter(List<Featured> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_menus, parent, false);
        return new FeaturedMenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedMenuAdapter.ViewHolder holder, int position) {


        holder.icon.setImageResource(list.get(position).getIconId());
        holder.title.setText(list.get(position).getTitle());

        holder.icon.setOnClickListener(v -> {

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        MaterialTextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.middle_menu_icon);
            title = itemView.findViewById(R.id.middle_menu_title);
        }

    }
}
