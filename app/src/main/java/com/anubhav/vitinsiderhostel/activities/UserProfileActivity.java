package com.anubhav.vitinsiderhostel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.fragments.ViewUserProfileFragment;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserAccountDeleted;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, iOnUserAccountDeleted {

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
       // todo delete the user details from the database
        Toast.makeText(UserProfileActivity.this, "User Account Deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(99, intent);
        finish();
    }

}