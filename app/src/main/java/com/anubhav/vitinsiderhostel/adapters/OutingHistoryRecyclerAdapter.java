package com.anubhav.vitinsiderhostel.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.ORAStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingHistoryCardClicked;
import com.anubhav.vitinsiderhostel.models.ORApp;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OutingHistoryRecyclerAdapter extends RecyclerView.Adapter<OutingHistoryRecyclerAdapter.ViewHolder> {


    private final List<ORApp> list;
    private final iOnOutingHistoryCardClicked cardClicked;

    public OutingHistoryRecyclerAdapter(List<ORApp> list, iOnOutingHistoryCardClicked cardClicked) {
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




        holder.statusTxt.setText(list.get(position).getOraStatus());
        holder.regNumTxt.setText(list.get(position).getStudentRegisterNumber());
        holder.visitLocationTxt.setText(list.get(position).getVisitLocation());
        holder.checkOutTxt.setText(list.get(position).getCheckOut());
        holder.checkInTxt.setText(list.get(position).getCheckIn());

        SimpleDateFormat formatToString = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String appliedDateString = formatToString.format(list.get(position).getUploadTimestamp().toDate());
        final String appliedOn = "Applied on : " + appliedDateString;

        holder.appliedOnTxt.setText(appliedOn);

        final String visitDate = list.get(position).getVisitDate().trim();

        final String[] splitDate = visitDate.split("-");
        final String dateYear = splitDate[0] + " - " + splitDate[2];
        holder.dateYearTxt.setText(dateYear);

        formatToString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = null;

        try {
            date = formatToString.parse(visitDate);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        formatToString = new SimpleDateFormat("MMM", Locale.getDefault());
        assert date != null;
        final String monthText = formatToString.format(date).toUpperCase(Locale.ROOT);

        holder.monthTxt.setText(monthText);

        if (list.get(position).getOraStatus().equalsIgnoreCase(ORAStatus.APPROVED.toString())) {
            holder.viewDopBtn.setVisibility(View.VISIBLE);
            holder.viewDopBtn.setClickable(true);
        } else {
            holder.viewDopBtn.setVisibility(View.GONE);
            holder.viewDopBtn.setClickable(false);
        }

        holder.viewDopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getOraStatus().equalsIgnoreCase(ORAStatus.APPROVED.toString())) {
                    cardClicked.outingHistoryViewDopClicked(list.get(position).getOraDocId(), position);
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!list.get(position).getOraStatus().equalsIgnoreCase(ORAStatus.APPROVED.toString())) {
                    cardClicked.outingHistoryCardLongPressed(position);
                }
                return true;
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClicked.outingHistoryCardClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        MaterialTextView monthTxt, dateYearTxt, statusTxt, regNumTxt, visitLocationTxt, checkOutTxt, checkInTxt, appliedOnTxt;
        MaterialButton viewDopBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cellOutingHistoryCard);
            monthTxt = itemView.findViewById(R.id.cellOutingHistoryMonthTxt);
            dateYearTxt = itemView.findViewById(R.id.cellOutingHistoryDateYearTxt);
            statusTxt = itemView.findViewById(R.id.cellOutingHistoryStatusTxt);
            regNumTxt = itemView.findViewById(R.id.cellOutingHistoryRegNumTxt);
            visitLocationTxt = itemView.findViewById(R.id.cellOutingHistoryVisitLocationTxt);
            checkOutTxt = itemView.findViewById(R.id.cellOutingHistoryCheckOutTxt);
            checkInTxt = itemView.findViewById(R.id.cellOutingHistoryCheckInTxt);
            appliedOnTxt = itemView.findViewById(R.id.cellOutingHistoryAppliedOnTxt);
            viewDopBtn = itemView.findViewById(R.id.cellOutingHistoryViewDopBtn);

        }

    }

}
