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
import com.google.firebase.Timestamp;

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
        holder.visitPurposeTxt.setText(list.get(position).getVisitPurpose());

        holder.appliedOnTxt.setText(getAppliedOn(position));
        holder.dateYearTxt.setText(getDate(position));
        holder.monthTxt.setText(getMonth(position));

        holder.parentTxt.setText(list.get(position).getParent());
        holder.proctorTxt.setText(list.get(position).getProctor());
        holder.chiefWardenTxt.setText(list.get(position).getChiefWarden());

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

        holder.cardView.setOnClickListener(v -> cardClicked.outingHistoryCardPressed(position));
    }

    private String getAppliedOn(int position) {
        SimpleDateFormat formatToString = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String appliedDateString = formatToString.format(list.get(position).getTimeStamp().toDate());
        return "Applied on : " + appliedDateString;
    }

    private String getDate(int position) {
        Timestamp timestamp = list.get(position).getVisitDate();
        Date date = timestamp.toDate();
        SimpleDateFormat getDate = new SimpleDateFormat("dd, yyyy", Locale.getDefault());
        return getDate.format(date);
    }

    private String getMonth(int position) {
        Timestamp timestamp = list.get(position).getVisitDate();
        Date date = timestamp.toDate();
        SimpleDateFormat getMonth = new SimpleDateFormat("MMM", Locale.getDefault());
        return getMonth.format(date);
    }

    private String getTime(int position, int val) {
        Timestamp timestamp = null;
        if (val == 1) {
            //check in
            timestamp = list.get(position).getCheckIn();
        } else if (val == 0) {
            //check out
            timestamp = list.get(position).getCheckOut();

        }
        assert timestamp != null;
        Date date = timestamp.toDate();
        SimpleDateFormat getMonth = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return getMonth.format(date);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        MaterialTextView monthTxt, dateYearTxt, statusTxt, visitPurposeTxt, appliedOnTxt, parentTxt, proctorTxt, chiefWardenTxt;
        MaterialButton viewDopBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cellOutingHistoryCard);
            monthTxt = itemView.findViewById(R.id.cellOutingHistoryMonthTxt);
            dateYearTxt = itemView.findViewById(R.id.cellOutingHistoryDateYearTxt);
            statusTxt = itemView.findViewById(R.id.cellOutingHistoryStatusTxt);
            visitPurposeTxt = itemView.findViewById(R.id.cellOutingHistoryVisitPurposeTxt);
            proctorTxt = itemView.findViewById(R.id.cellOutingHistoryProctorTxt);
            chiefWardenTxt = itemView.findViewById(R.id.cellOutingHistoryChiefWardenTxt);
            parentTxt = itemView.findViewById(R.id.cellOutingHistoryParentTxt);
            appliedOnTxt = itemView.findViewById(R.id.cellOutingHistoryAppliedOnTxt);
            viewDopBtn = itemView.findViewById(R.id.cellOutingHistoryViewDopBtn);

        }

    }

}
