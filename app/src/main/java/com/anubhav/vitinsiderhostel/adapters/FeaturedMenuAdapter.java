package com.anubhav.vitinsiderhostel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.interfaces.iOnFeaturedMenuClicked;
import com.anubhav.vitinsiderhostel.models.Featured;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class FeaturedMenuAdapter extends RecyclerView.Adapter<FeaturedMenuAdapter.ViewHolder> {


    private final iOnFeaturedMenuClicked onFeaturedMenuClicked;
    private final List<Featured> list;
    private final Context context;


    public FeaturedMenuAdapter(List<Featured> list, Context context, iOnFeaturedMenuClicked onFeaturedMenuClicked) {
        this.list = list;
        this.context = context;
        this.onFeaturedMenuClicked = onFeaturedMenuClicked;
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
        holder.cardView.setOnClickListener(v -> {
            if (list.get(position).getTitle().equalsIgnoreCase("Outing Request")){
                onFeaturedMenuClicked.outingRequestClicked();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        MaterialTextView title;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.middle_menu_icon);
            title = itemView.findViewById(R.id.middle_menu_title);
            cardView = itemView.findViewById(R.id.featuredMenuCardView);
        }

    }
}
