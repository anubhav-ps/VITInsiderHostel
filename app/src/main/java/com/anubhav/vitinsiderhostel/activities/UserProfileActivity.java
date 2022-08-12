package com.anubhav.vitinsiderhostel.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.fragments.ViewUserProfileFragment;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserAccountDeleted;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.snackbar.Snackbar;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, iOnUserAccountDeleted{


    public UserProfileActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (savedInstanceState == null) {
            ViewUserProfileFragment viewUserProfileFragment = new ViewUserProfileFragment();
            makeTransaction(viewUserProfileFragment);
        }

        ImageButton backArrowToAccount = findViewById(R.id.userProfileBackArrow);
        backArrowToAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.userProfileBackArrow) {
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
        fragmentTransaction.replace(R.id.userProfilePageContainer, fragment);
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
                .make(UserProfileActivity.this, findViewById(R.id.userProfileActivity), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(UserProfileActivity.this, R.color.navy_blue));
        snackbar.show();
    }




}