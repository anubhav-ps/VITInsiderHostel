package com.anubhav.vitinsiderhostel.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.R;
import com.google.android.material.card.MaterialCardView;


public class AboutFragment extends Fragment implements View.OnClickListener {


    private View rootView;
    private Dialog dialog;


    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        dialog = new Dialog(getContext());

        MaterialCardView connectCard = rootView.findViewById(R.id.aboutFragmentConnectBtn);

        connectCard.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.aboutFragmentConnectBtn) {
            openConnectDialog();
        } else if (id == R.id.dialogConnectLinkedinImgBtn) {
            final String url = "https://www.linkedin.com/in/anubhav-p-sahoo/";
            connectSocialAppLink(url);
        } else if (id == R.id.dialogConnectInstagramImgBtn) {
            final String url = "https://www.instagram.com/anubhav_p_s/";
            connectSocialAppLink(url);
        } else if (id == R.id.dialogConnectSnapchatImgBtn) {
            final String url = "https://www.snapchat.com/add/anubhav_ps?share_id=5EFlHWCCAnM&locale=en-GB";
            connectSocialAppLink(url);
        } else if (id == R.id.dialogConnectYoutubeImgBtn) {
            final String url = "https://www.youtube.com/c/RUDCODEBootcamp_anubhav_ps";
            connectSocialAppLink(url);
        }

    }

    private void openConnectDialog() {
        dialog.setContentView(R.layout.dialog_choose_connect);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton linkedInBtn, instagramBtn, youtubeBtn, snapchatBtn;
        linkedInBtn = dialog.findViewById(R.id.dialogConnectLinkedinImgBtn);
        instagramBtn = dialog.findViewById(R.id.dialogConnectInstagramImgBtn);
        youtubeBtn = dialog.findViewById(R.id.dialogConnectYoutubeImgBtn);
        snapchatBtn = dialog.findViewById(R.id.dialogConnectSnapchatImgBtn);
        linkedInBtn.setOnClickListener(this);
        instagramBtn.setOnClickListener(this);
        youtubeBtn.setOnClickListener(this);
        snapchatBtn.setOnClickListener(this);

        dialog.show();
    }

    private void connectSocialAppLink(String link) {
        dialog.dismiss();
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}