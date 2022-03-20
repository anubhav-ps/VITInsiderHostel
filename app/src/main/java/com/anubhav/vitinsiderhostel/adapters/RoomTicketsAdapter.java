package com.anubhav.vitinsiderhostel.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.models.Ticket;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RoomTicketsAdapter extends RecyclerView.Adapter<RoomTicketsAdapter.TicketViewHolder> {


    private ArrayList<Ticket> ticketArrayList;
    private iOnRoomTicketLongPressed ticketLongPressed;


    public RoomTicketsAdapter(ArrayList<Ticket> ticketArrayList,iOnRoomTicketLongPressed ticketLongPressed) {
        this.ticketArrayList = ticketArrayList;
        this.ticketLongPressed = ticketLongPressed;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_room_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final Ticket model = ticketArrayList.get(position);

        final String serviceName = "Service - " + model.getServiceName();
        final String description = model.getServiceDescription();
        SimpleDateFormat formatToString = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String dateString = formatToString.format(model.getItemTimeStamp().toDate());
        final String raisedOn = "Raised on : " + dateString;
        String raisedBy = "Raised by : ";
        if (model.getUploaderMailId().equalsIgnoreCase(User.getInstance().getUserMailID())){
            raisedBy = raisedBy+"You";
        }else{
            raisedBy = raisedBy + model.getUploaderMailId();
        }

        holder.serviceNameTxt.setText(serviceName);
        holder.descriptionTxt.setText(description);
        holder.raisedOnTxt.setText(raisedOn);
        holder.raisedByNameTxt.setText(raisedBy);

        final String status = model.getStatus();
        if (status.equalsIgnoreCase(TicketStatus.BOOKED.toString())) {
            holder.statusBarImg.setImageResource(R.drawable.status_submitted_bar);
        } else if (status.equalsIgnoreCase(TicketStatus.IN_REVIEW.toString())) {
            holder.statusBarImg.setImageResource(R.drawable.status_in_review_bar);
        } else if (status.equalsIgnoreCase(TicketStatus.SOLVED.toString())) {
            holder.statusBarImg.setImageResource(R.drawable.status_completed_bar);
        }

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ticketLongPressed.onRoomTicketClicked(position,status,model.getDocId());
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.ticketArrayList.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        MaterialTextView serviceNameTxt, descriptionTxt, raisedByNameTxt, raisedOnTxt;
        ImageView statusBarImg;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cellRoomTicketHistoryCard);
            serviceNameTxt = itemView.findViewById(R.id.cellRoomTicketServiceName);
            descriptionTxt = itemView.findViewById(R.id.cellRoomTicketDescription);
            raisedByNameTxt = itemView.findViewById(R.id.cellRoomTicketRaisedBy);
            raisedOnTxt = itemView.findViewById(R.id.cellRoomTicketRaisedOn);
            statusBarImg = itemView.findViewById(R.id.cellRoomTicketStatus);

        }
    }


    public interface iOnRoomTicketLongPressed{
           void onRoomTicketClicked(int pos,String ticketStatus,String docID);
    }

}
