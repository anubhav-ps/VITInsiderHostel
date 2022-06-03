package com.anubhav.vitinsiderhostel.activities;

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

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.fragments.AccountFragment;
import com.anubhav.vitinsiderhostel.fragments.BlockFragment;
import com.anubhav.vitinsiderhostel.fragments.RoomFragment;
import com.anubhav.vitinsiderhostel.interfaces.iOnFeaturedMenuClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingSectionChosen;
import com.anubhav.vitinsiderhostel.interfaces.iOnTicketSectionChosen;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserProfileClicked;
import com.google.android.material.textview.MaterialTextView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomePageActivity extends AppCompatActivity implements iOnUserProfileClicked, iOnTicketSectionChosen , iOnOutingSectionChosen{

    // main view declarations
    private ChipNavigationBar chipNavigationBar;
    // callback to userProfile Activity and its child fragment destruction
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == 84) {  // to come back to Home Page Activity and place the account fragment , if the back arrow or back pressed is called in user profile activity
                chipNavigationBar.setItemSelected(R.id.menu_account, true);
            } else if (result.getResultCode() == 99) {  // to come back to Home Page Activity and then Open the Login Activity when user account is deleted
                LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(HomePageActivity.this);
                localSqlDatabase.deleteCurrentUser();
                localSqlDatabase.deleteAllTenants();
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (result.getResultCode() == 86) {   // to come back to Home Page Activity and place the block fragment , if the back arrow or back pressed is called in ticket history activity
                chipNavigationBar.setItemSelected(R.id.menu_account, true);
            } else if (result.getResultCode() == 88) {   // to come back to Home Page Activity and place the block fragment , if the back arrow or back pressed is called in outing activity
                chipNavigationBar.setItemSelected(R.id.menu_block, true);
            }
        }
    });

    private MaterialTextView toolBarAccountText;
    private ImageView logo;

    // empty constructor
    public HomePageActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // bottom view navigation setup
        chipNavigationBar = findViewById(R.id.bottom_navigation_view);
        chipNavigationBar.setItemSelected(R.id.menu_block, true);
        // toolbar views
        toolBarAccountText = findViewById(R.id.tool_bar_account_txt);
        logo = findViewById(R.id.tool_bar_logo);

        bottomNavigationViewSetup();


        // placing the room fragment on initial entry
        if (savedInstanceState == null) {
            BlockFragment blockFragment = new BlockFragment();
            toolBarAccountText.setVisibility(View.INVISIBLE);
            logo.setVisibility(View.VISIBLE);
            makeTransaction(blockFragment);
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


    @Override
    public void onRoomTicketClicked() {
        Intent intent = new Intent(HomePageActivity.this, TicketHistoryActivity.class);
        intent.putExtra("Section", "ROOM");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onBlockTicketClicked() {
        Intent intent = new Intent(HomePageActivity.this, TicketHistoryActivity.class);
        intent.putExtra("Section", "BLOCK");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onApplyOraSectionClicked() {
        Intent intent = new Intent(HomePageActivity.this, FeaturedActivity.class);
        intent.putExtra("Section", "Apply");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onOraHistorySectionClicked() {
        Intent intent = new Intent(HomePageActivity.this, FeaturedActivity.class);
        intent.putExtra("Section", "History");
        activityResultLauncher.launch(intent);
    }

}