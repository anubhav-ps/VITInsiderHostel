package com.anubhav.vitinsiderhostel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.models.PublicProfile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class TravelCompanionAdapter extends RecyclerView.Adapter<TravelCompanionAdapter.ViewHolder> {

    private final Context context;
    private List<PublicProfile> publicProfiles;

    public TravelCompanionAdapter(List<PublicProfile> publicProfiles, Context context) {
        this.publicProfiles = publicProfiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_public_profile, parent, false);
        return new TravelCompanionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PublicProfile publicProfile = publicProfiles.get(position);

        String image = "av_" + publicProfile.getAvatar();
        int imageId = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
        holder.avatar.setImageResource(imageId);
        holder.usernameTxt.setText(publicProfiles.get(position).getUserName());

    }

    @Override
    public int getItemCount() {
        return this.publicProfiles.size();
    }

    public void setPublicProfiles(List<PublicProfile> publicProfiles) {
        this.publicProfiles = publicProfiles;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        ImageView avatar;
        MaterialTextView usernameTxt;
        MaterialButton pingBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cellPublicProfileCardView);
            avatar = itemView.findViewById(R.id.cellPublicProfileAvatarImg);
            usernameTxt = itemView.findViewById(R.id.cellPublicProfileUsernameTxt);
            pingBtn = itemView.findViewById(R.id.cellPublicProfilePingBtn);

        }


    }
}
