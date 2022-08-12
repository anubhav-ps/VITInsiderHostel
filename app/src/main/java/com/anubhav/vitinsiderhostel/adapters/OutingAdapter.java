package com.anubhav.vitinsiderhostel.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingCardClicked;
import com.anubhav.vitinsiderhostel.models.Outing;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class OutingAdapter extends RecyclerView.Adapter<OutingAdapter.OutingViewHolder> {

    private final iOnOutingCardClicked onOutingCardClicked;
    private final List<Outing> outingList;
    private final Context context;

    public OutingAdapter(List<Outing> outingList, Context context, iOnOutingCardClicked listener) {
        this.outingList = outingList;
        this.context = context;
        this.onOutingCardClicked = listener;
    }


    @NonNull
    @Override
    public OutingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_outing, parent, false);
        return new OutingViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OutingViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Outing outing = outingList.get(position);
        String date = outing.getDate();
        String dayTxt = outing.getDay();


        holder.dateTxt.setText(date);
        holder.dayTxt.setText(dayTxt);

        holder.cardView.setOnClickListener(v -> onOutingCardClicked.outingCardClicked(position));


    }

    @Override
    public int getItemCount() {
        return outingList.size();
    }


    static class OutingViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        MaterialTextView dayTxt, dateTxt;

        public OutingViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cell_outing_card_view);
            dateTxt = itemView.findViewById(R.id.cell_outing_date_txt);
            dayTxt = itemView.findViewById(R.id.cell_outing_day_txt);
        }
    }
}
