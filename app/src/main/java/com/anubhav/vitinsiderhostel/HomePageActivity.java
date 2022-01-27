package com.anubhav.vitinsiderhostel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textview.MaterialTextView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomePageActivity extends AppCompatActivity implements AccountFragment.onUserProfileCalledListener {


    // main view declarations
    private ChipNavigationBar chipNavigationBar;
    private MaterialTextView toolBarAccountText;
    private ImageView logo;

    // empty constructor
    public HomePageActivity() {

    }

    // callback to userProfile Activity and its child fragment destruction
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == 84) {  // to come back to Home Page Activity and place the account fragment , if the back arrow or back pressed is called in user profile activity
                chipNavigationBar.setItemSelected(R.id.menu_account, true);
            } else if (result.getResultCode() == 99) {  // to come back to Home Page Activity and then Open the Login Activity when user account is deleted
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // bottom view navigation setup
        chipNavigationBar = findViewById(R.id.bottom_navigation_view);
        chipNavigationBar.setItemSelected(R.id.menu_room, true);
        bottomNavigationViewSetup();

        // toolbar views
        toolBarAccountText = findViewById(R.id.tool_bar_account_txt);
        logo = findViewById(R.id.tool_bar_logo);

        // placing the room fragment on initial entry
        if (savedInstanceState == null) {
            RoomFragment roomFragment = new RoomFragment();
            toolBarAccountText.setVisibility(View.INVISIBLE);
            logo.setVisibility(View.VISIBLE);
            makeTransaction(roomFragment);
        }


    }

    // bottom navigation setup process
    private void bottomNavigationViewSetup() {
        chipNavigationBar.setOnItemSelectedListener
                (i -> {
                    if (i == R.id.menu_room) {
                        RoomFragment roomFragment = new RoomFragment();
                        toolBarAccountText.setVisibility(View.INVISIBLE);
                        logo.setVisibility(View.VISIBLE);
                        makeTransaction(roomFragment);
                    } else if (i == R.id.menu_block) {
                        BlockFragment blockFragment = new BlockFragment();
                        toolBarAccountText.setVisibility(View.INVISIBLE);
                        logo.setVisibility(View.VISIBLE);
                        makeTransaction(blockFragment);
                    } else if (i == R.id.menu_account) {
                        placeAccountFragment();
                    }
                });

    }

    // method to place the account fragment in the frame layout
    private void placeAccountFragment() {
        AccountFragment accountFragment = new AccountFragment();
        toolBarAccountText.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        makeTransaction(accountFragment);
    }

    //method to make the fragment transaction
    public void makeTransaction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homePageFragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    // callback to show the user profile activity when user clicks on the user profile text view in the account fragment
    @Override
    public void onUserProfileCalled() {
        Intent intent = new Intent(HomePageActivity.this, UserProfileActivity.class);
        activityResultLauncher.launch(intent);
    }

}