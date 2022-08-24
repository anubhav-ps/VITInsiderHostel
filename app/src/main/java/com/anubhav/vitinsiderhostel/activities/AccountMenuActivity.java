package com.anubhav.vitinsiderhostel.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.SettingsMenu;
import com.anubhav.vitinsiderhostel.fragments.AboutFragment;
import com.anubhav.vitinsiderhostel.fragments.NotificationFragment;
import com.anubhav.vitinsiderhostel.fragments.ViewUserProfileFragment;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserAccountDeleted;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

public class AccountMenuActivity extends AppCompatActivity implements View.OnClickListener, iOnUserAccountDeleted {


    public AccountMenuActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);

        ImageButton backArrowToAccount = findViewById(R.id.accountMenuPgeBackArrow);
        MaterialTextView toolbarTitle = findViewById(R.id.accountMenuPgeToolBarTxt);
        MaterialToolbar toolbar = findViewById(R.id.accountMenuPgeToolBar);

        if (savedInstanceState == null) {
            final String section = getIntent().getStringExtra("SECTION");
            Fragment fragment = null;
            if (section.equalsIgnoreCase(SettingsMenu.PROFILE.getValue())) {
                toolbarTitle.setText(SettingsMenu.PROFILE.getValue());
                fragment = new ViewUserProfileFragment();
            } else if (section.equalsIgnoreCase(SettingsMenu.NOTIFICATIONS.getValue())) {
                toolbarTitle.setText(SettingsMenu.NOTIFICATIONS.getValue());
                fragment = new NotificationFragment();
            } else if (section.equalsIgnoreCase(SettingsMenu.ABOUT.toString())) {
                toolbarTitle.setText(SettingsMenu.ABOUT.getValue());
                toolbarTitle.setTextColor(Color.WHITE);
                toolbar.setBackgroundColor(Color.parseColor("#181717"));
                toolbar.setElevation(0);
                backArrowToAccount.setVisibility(View.GONE);
                fragment = new AboutFragment();
            } else if (section.equalsIgnoreCase(SettingsMenu.REPORT.toString())) {
                toolbarTitle.setText(SettingsMenu.REPORT.getValue());
            } else if (section.equalsIgnoreCase(SettingsMenu.BUG.toString())) {
                toolbarTitle.setText(SettingsMenu.BUG.getValue());
            } else if (section.equalsIgnoreCase(SettingsMenu.SUGGESTION.toString())) {
                toolbarTitle.setText(SettingsMenu.SUGGESTION.getValue());
            }
            makeTransaction(fragment);
        }

        backArrowToAccount.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accountMenuPgeBackArrow) {
            Intent intent = new Intent();
            setResult(84, intent);
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
        fragmentTransaction.replace(R.id.accountMenuPgeFragmentContainer, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void userAccountDeleted() {
        callSnackBar("User Account Deleted");
        Intent intent = new Intent();
        setResult(99, intent);
        finish();
    }

    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(AccountMenuActivity.this, findViewById(R.id.userProfileActivity), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(AccountMenuActivity.this, R.color.navy_blue));
        snackbar.show();
    }


}