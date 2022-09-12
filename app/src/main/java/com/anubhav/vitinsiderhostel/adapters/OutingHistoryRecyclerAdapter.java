package com.anubhav.vitinsiderhostel.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.OutingFormStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingHistoryCardClicked;
import com.anubhav.vitinsiderhostel.models.OutingForm;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OutingHistoryRecyclerAdapter extends RecyclerView.Adapter<OutingHistoryRecyclerAdapter.ViewHolder> {


    private final List<OutingForm> list;
    private final iOnOutingHistoryCardClicked cardClicked;

    public OutingHistoryRecyclerAdapter(List<OutingForm> list, iOnOutingHistoryCardClicked cardClicked) {
        this.list = list;
        this.cardClicked = cardClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_outing_application, parent, false);
        return new OutingHistoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.statusTxt.setText(list.get(position).getStatus());
        holder.visitLocationTxt.setText(list.get(position).getVisitLocation());
        holder.visitPurposeTxt.setText(list.get(position).getVisitPurpose());
        holder.checkOutTxt.setText(list.get(position).getCheckOut());
        holder.checkInTxt.setText(list.get(position).getCheckIn());

        holder.appliedOnTxt.setText(getAppliedOn(position));
        holder.dateYearTxt.setText(getDate(position));
        holder.monthTxt.setText(getMonth(position));

        if (list.get(position).getStatus().equalsIgnoreCase(OutingFormStatus.APPROVED.toString())) {
            holder.viewDopBtn.setVisibility(View.VISIBLE);
            holder.viewDopBtn.setClickable(true);
        } else {
            holder.viewDopBtn.setVisibility(View.GONE);
            holder.viewDopBtn.setClickable(false);
        }

        holder.viewDopBtn.setOnClickListener(v -> {
            OutingForm outingForm = list.get(position);
            cardClicked.outingHistoryViewQRCodeClicked(outingForm.getCode(), outingForm.getVisitDate(), outingForm.getStudentRegisterNumber());
        });

        holder.cardView.setOnLongClickListener(v -> {
            cardClicked.outingHistoryCardLongPressed(position);
            return true;
        });
    }

    private String getAppliedOn(int position) {
        SimpleDateFormat formatToString = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String appliedDateString = formatToString.format(list.get(position).getTimestamp().toDate());
        return "Applied on : " + appliedDateString;
    }

    private String getDate(int position) {
        final String visitDate = list.get(position).getVisitDate().trim();
        final String[] splitDate = visitDate.split("-");
        return splitDate[0] + " - " + splitDate[2];
    }

    private String getMonth(int position) {
        final String visitDate = list.get(position).getVisitDate().trim();
        SimpleDateFormat formatToString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = null;

        try {
            date = formatToString.parse(visitDate);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        formatToString = new SimpleDateFormat("MMM", Locale.getDefault());
        assert date != null;
        return formatToString.format(date).toUpperCase(Locale.ROOT);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        MaterialTextView monthTxt, dateYearTxt, statusTxt, visitLocationTxt, visitPurposeTxt, checkOutTxt, checkInTxt, appliedOnTxt;
        MaterialButton viewDopBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cellOutingHistoryCard);
            monthTxt = itemView.findViewById(R.id.cellOutingHistoryMonthTxt);
            dateYearTxt = itemView.findViewById(R.id.cellOutingHistoryDateYearTxt);
            statusTxt = itemView.findViewById(R.id.cellOutingHistoryStatusTxt);
            visitLocationTxt = itemView.findViewById(R.id.cellOutingHistoryVisitLocationTxt);
            visitPurposeTxt = itemView.findViewById(R.id.cellOutingHistoryVisitPurposeTxt);
            checkOutTxt = itemView.findViewById(R.id.cellOutingHistoryCheckOutTxt);
            checkInTxt = itemView.findViewById(R.id.cellOutingHistoryCheckInTxt);
            appliedOnTxt = itemView.findViewById(R.id.cellOutingHistoryAppliedOnTxt);
            viewDopBtn = itemView.findViewById(R.id.cellOutingHistoryViewDopBtn);

        }

    }

}
