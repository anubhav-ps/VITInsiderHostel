package com.anubhav.vitinsiderhostel.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.interfaces.iOnRoomMateCardClicked;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Locale;

public class RoomMateAdapter extends RecyclerView.Adapter<RoomMateAdapter.ViewHolder> {

    private final Context context;
    private final iOnRoomMateCardClicked onRoomMateCardClicked;
    private List<Tenant> roomMateList;

    public RoomMateAdapter(List<Tenant> roomMateList, Context context, iOnRoomMateCardClicked listener) {
        this.roomMateList = roomMateList;
        this.context = context;
        this.onRoomMateCardClicked = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_room_mates, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        Tenant tenant = roomMateList.get(position);

        String image = "av_" + tenant.getTenantAvatar();
        int imageId = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
        holder.avatar.setImageResource(imageId);


        holder.name.setText(firstCaps(tenant.getTenantName().split(" ")[0]));

        holder.cardView.setOnClickListener(v -> {
            onRoomMateCardClicked.callTenantDialog(position);
        });

    }

    private String firstCaps(String name) {
        name = name.toLowerCase(Locale.ROOT);
        char[] arr = name.toCharArray();
        arr[0] = String.valueOf(arr[0]).toUpperCase(Locale.ROOT).charAt(0);
        return String.valueOf(arr);
    }

    @Override
    public int getItemCount() {
        return roomMateList.size();
    }

    public void setRoomMateList(List<Tenant> roomMateList) {
        this.roomMateList = roomMateList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        ImageView avatar;
        MaterialTextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cellRoomMateCardView);
            avatar = itemView.findViewById(R.id.cellRoomMateAvatar);
            name = itemView.findViewById(R.id.cellRoomMateUserName);
        }


    }


}
