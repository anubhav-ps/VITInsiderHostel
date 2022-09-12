package com.anubhav.vitinsiderhostel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.fragments.BlockTicketFragment;
import com.anubhav.vitinsiderhostel.fragments.OutingHistoryFragment;
import com.anubhav.vitinsiderhostel.fragments.RoomTicketFragment;

public class TicketHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_history);

        final String section = getIntent().getStringExtra("Section");
        if (savedInstanceState == null) {
            Fragment fragment = null;
            if (section.equalsIgnoreCase("ROOM")) {
                fragment = new RoomTicketFragment();
            } else if (section.equalsIgnoreCase("BLOCK")) {
                fragment = new BlockTicketFragment();
            } else if (section.equalsIgnoreCase("OUTING")) {
                fragment = new OutingHistoryFragment();
            }
            makeTransaction(fragment);
        }

        ImageButton backArrowToAccount = findViewById(R.id.ticketHistoryBackArrow);
        backArrowToAccount.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ticketHistoryBackArrow) {
            Intent intent = new Intent();
            setResult(86, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //method to make the fragment transaction
    public void makeTransaction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ticketHistoryContainer, fragment);
        fragmentTransaction.commit();
    }

}