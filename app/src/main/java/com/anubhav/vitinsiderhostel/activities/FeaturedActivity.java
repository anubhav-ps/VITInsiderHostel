package com.anubhav.vitinsiderhostel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.FeaturedMenu;
import com.anubhav.vitinsiderhostel.fragments.OutingHistoryFragment;
import com.anubhav.vitinsiderhostel.fragments.OutingRequestFragment;
import com.anubhav.vitinsiderhostel.fragments.TravelCompanionHomeFragment;

public class FeaturedActivity extends AppCompatActivity implements View.OnClickListener {


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == 90) {
            OutingHistoryFragment outingHistoryFragment = new OutingHistoryFragment();
            makeTransaction(outingHistoryFragment);
        }

    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured);
        if (savedInstanceState == null) {
            final String section = getIntent().getStringExtra("SECTION");
            Fragment fragment = null;
            if (section.equalsIgnoreCase(FeaturedMenu.OUTING_REQUEST.toString())) {
                fragment = new OutingRequestFragment();
            } else if (section.equalsIgnoreCase(FeaturedMenu.TRAVEL_COMPANION.toString())) {
                fragment = new TravelCompanionHomeFragment();
            }
            makeTransaction(fragment);
        }

        ImageButton backArrowToBlockFragment = findViewById(R.id.featuredActivityBackArrow);
        backArrowToBlockFragment.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.featuredActivityBackArrow) {
            Intent intent = new Intent();
            setResult(88, intent);
            finish();
        }
    }

    public void makeTransaction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.featuredMenuContainer, fragment);
        fragmentTransaction.commit();
    }


}