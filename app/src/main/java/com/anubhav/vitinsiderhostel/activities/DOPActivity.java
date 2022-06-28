package com.anubhav.vitinsiderhostel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.interfaces.iOnIssuedOReqFetched;
import com.anubhav.vitinsiderhostel.models.IssuedOREK;
import com.anubhav.vitinsiderhostel.models.LinkEnds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class DOPActivity extends AppCompatActivity implements iOnIssuedOReqFetched {


    iOnIssuedOReqFetched onIssuedOReqFetched;
    private MaterialTextView issuedOnTxt, validityTxt, registerNumTxt, studentNameTxt, visitDateTxt, checkOutTxt, checkInTxt, checkedOutAtTxt, checkedInAtTxt;
    private ProgressBar progressBar;
    private LinearLayout loadingLinearLayout, dopCardLinearLayout;
    private ImageButton backArrow;
    private LinkEnds linkEnds = new LinkEnds();
    private IssuedOREK issuedOREK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dopactivity);

        final String block = getIntent().getStringExtra("Block");
        final String year = getIntent().getStringExtra("Year");
        final String month = getIntent().getStringExtra("Month");
        final String date = getIntent().getStringExtra("Date");
        final String docId = getIntent().getStringExtra("DocId");

        onIssuedOReqFetched = this;


        progressBar = findViewById(R.id.dopActivityProgressBar);
        loadingLinearLayout = findViewById(R.id.dopActivityLoadingLinearLayout);
        backArrow = findViewById(R.id.dopActivityBackArrow);

        issuedOnTxt = findViewById(R.id.dopCardIssuedOnTxt);
        validityTxt = findViewById(R.id.dopCardValidityStatusTxt);
        registerNumTxt = findViewById(R.id.dopCardRegisterNumberTxt);
        studentNameTxt = findViewById(R.id.dopCardStudentNameTxt);
        visitDateTxt = findViewById(R.id.dopCardVisitDateTxt);
        checkOutTxt = findViewById(R.id.dopCardCheckOutTxt);
        checkInTxt = findViewById(R.id.dopCardCheckInTxt);
        checkedOutAtTxt = findViewById(R.id.dopCardCheckedOutAtTxt);
        checkedInAtTxt = findViewById(R.id.dopCardCheckedInAtTxt);
        dopCardLinearLayout = findViewById(R.id.dopActivityCardLinearLayout);

        CollectionReference issueId = linkEnds.getIssuedDoc(block, year, month, date);

        issueId.whereEqualTo("reqDocId", docId).limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean isThere = false;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        issuedOREK = documentSnapshot.toObject(IssuedOREK.class);
                    }
                    isThere = true;
                } else {
                    //todo no such issue orek
                    progressBar.setVisibility(View.GONE);
                    loadingLinearLayout.setVisibility(View.GONE);
                }
                onIssuedOReqFetched.onIssuedOReqFetched(isThere);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(90, intent);
                finish();
            }
        });

    }

    @Override
    public void onIssuedOReqFetched(boolean flag) {

        loadingLinearLayout.setVisibility(View.GONE);
        dopCardLinearLayout.setVisibility(View.VISIBLE);

        if (flag && issuedOREK!=null) {

            progressBar.setVisibility(View.GONE);
            final String issuedOn = "Issued on : " + issuedOREK.getIssuedOn();
            issuedOnTxt.setText(issuedOn);

            validityTxt.setText(issuedOREK.getValidity().toUpperCase(Locale.ROOT));
            registerNumTxt.setText(issuedOREK.getStudentRegisterNumber().trim());
            studentNameTxt.setText(issuedOREK.getStudentName().trim());
            visitDateTxt.setText(issuedOREK.getVisitDate());
            checkInTxt.setText(issuedOREK.getCheckInTime());
            checkOutTxt.setText(issuedOREK.getCheckOutTime());

            if (issuedOREK.getHasCheckedOut() != null) {
                final Timestamp checkedOutAtTime = issuedOREK.getHasCheckedOut();
                checkedOutAtTxt.setText(checkedOutAtTime.toString());
            }
            if (issuedOREK.getHasCheckedIn() != null) {
                final Timestamp checkedInAtTime = issuedOREK.getHasCheckedIn();
                checkedInAtTxt.setText(checkedInAtTime.toString());
            }
        }


    }
}