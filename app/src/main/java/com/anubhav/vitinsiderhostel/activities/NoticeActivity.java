package com.anubhav.vitinsiderhostel.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.Path;
import com.anubhav.vitinsiderhostel.enums.Urgency;
import com.anubhav.vitinsiderhostel.interfaces.iOnNoticeDownloaded;
import com.anubhav.vitinsiderhostel.models.Notice;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoticeActivity extends AppCompatActivity implements View.OnClickListener, iOnNoticeDownloaded {

    //firebase fireStore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference noticeSection = db.collection(Path.NOTICE.getPath());

    //listeners
    private iOnNoticeDownloaded onNoticeDownloaded;

    //views
    private MaterialTextView titleTxt, postedOnTxt, postedByTxt, siteLinkTxt, urgencyTxt;
    private TextView bodyTxt;
    private ImageView imageView;
    private LinearLayout siteLinkLayout, urgencyLayout;
    private ProgressBar progressBar;

    //objects
    private Notice notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        /*WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(layoutParams);
*/
        if (Build.VERSION.SDK_INT >= 30) {
            this.getWindow().getDecorView().findViewById(android.R.id.content).getWindowInsetsController().hide(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            this.getWindow().getDecorView().findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }


        //get intent value
        final String docId = getIntent().getStringExtra("DOC_ID");


        //views
        MaterialCardView closeCard = findViewById(R.id.noticePgeCloseCardView);
        titleTxt = findViewById(R.id.noticePgeTitleTxt);
        bodyTxt = findViewById(R.id.noticePgeBodyTxt);
        postedByTxt = findViewById(R.id.noticePgePostedByTxt);
        postedOnTxt = findViewById(R.id.noticePgePostedOnTxt);
        siteLinkTxt = findViewById(R.id.noticePgeLinkTxt);
        urgencyTxt = findViewById(R.id.noticePgeUrgencyStatusTxt);
        progressBar = findViewById(R.id.noticePgeProgressBar);
        imageView = findViewById(R.id.noticePgeImageView);
        siteLinkLayout = findViewById(R.id.noticePgeLinkStatusLinearLayout);
        urgencyLayout = findViewById(R.id.noticePgeUrgencyStatusLinearLayout);

        //listeners
        onNoticeDownloaded = this;
        closeCard.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        siteLinkLayout.setVisibility(View.GONE);
        urgencyLayout.setVisibility(View.GONE);

        makeItInVisible();
        downloadNotice(docId);
    }

    private void downloadNotice(String docID) {
        noticeSection.document(User.getInstance().getStudentBlock())
                .collection(Path.FILES.getPath())
                .document(docID).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        notice = documentSnapshot.toObject(Notice.class);
                        onNoticeDownloaded.noticeDownloaded();
                    } else {
                        //todo no notice
                        progressBar.setVisibility(View.GONE);
                        displayError();
                    }

                });
    }

    private void displayError() {
        Toast.makeText(NoticeActivity.this, "Error has occurred while fetching data", Toast.LENGTH_LONG).show();
        returnToHomePage();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.noticePgeCloseCardView) {
            returnToHomePage();
        }
    }


    @Override
    public void noticeDownloaded() {
        progressBar.setVisibility(View.GONE);
        makeItVisible();

        imageView.setVisibility(View.VISIBLE);

        if (notice.getSiteLink() == null) {
            siteLinkLayout.setVisibility(View.GONE);
        } else {
            siteLinkLayout.setVisibility(View.VISIBLE);
            siteLinkTxt.setVisibility(View.VISIBLE);
            siteLinkTxt.setText(notice.getSiteLink());
        }

        if (notice.getUrgency().equalsIgnoreCase(Urgency.LOW.toString())) {
            urgencyLayout.setVisibility(View.GONE);
        } else if (notice.getUrgency().equalsIgnoreCase(Urgency.HIGH.toString())) {
            urgencyLayout.setVisibility(View.VISIBLE);
            urgencyTxt.setVisibility(View.VISIBLE);
            urgencyTxt.setText(notice.getUrgency());
        }

        SimpleDateFormat formatToString = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String dateString = formatToString.format(notice.getPostedOn().toDate());

        Picasso.get()
                .load(notice.getImageUri())
                .placeholder(R.drawable.notice_pic)
                .fit()
                .into(imageView);

        titleTxt.setText(notice.getTitle());
        bodyTxt.setText(notice.getBody());
        postedByTxt.setText(notice.getPostedBy());
        postedOnTxt.setText(dateString);

    }

    private void makeItVisible() {
        titleTxt.setVisibility(View.VISIBLE);
        bodyTxt.setVisibility(View.VISIBLE);
        postedByTxt.setVisibility(View.VISIBLE);
        postedOnTxt.setVisibility(View.VISIBLE);
    }

    private void makeItInVisible() {
        titleTxt.setVisibility(View.INVISIBLE);
        bodyTxt.setVisibility(View.INVISIBLE);
        postedByTxt.setVisibility(View.INVISIBLE);
        postedOnTxt.setVisibility(View.INVISIBLE);
    }

    private void returnToHomePage() {
        Intent intent = new Intent();
        setResult(90, intent);
        finish();
    }
}