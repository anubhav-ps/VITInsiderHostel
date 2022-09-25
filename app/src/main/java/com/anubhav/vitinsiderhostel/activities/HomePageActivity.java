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
import com.anubhav.vitinsiderhostel.enums.FeaturedMenu;
import com.anubhav.vitinsiderhostel.enums.SettingsMenu;
import com.anubhav.vitinsiderhostel.fragments.AccountFragment;
import com.anubhav.vitinsiderhostel.fragments.BlockFragment;
import com.anubhav.vitinsiderhostel.fragments.RoomFragment;
import com.anubhav.vitinsiderhostel.interfaces.iOnAccountMenuClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnFeaturedActivityCalled;
import com.anubhav.vitinsiderhostel.interfaces.iOnNoticeActivityCalled;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingSectionClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnTicketSectionClicked;
import com.anubhav.vitinsiderhostel.models.User;
import com.anubhav.vitinsiderhostel.notifications.AppNotification;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomePageActivity extends AppCompatActivity implements iOnAccountMenuClicked, iOnTicketSectionClicked, iOnOutingSectionClicked, iOnNoticeActivityCalled, iOnFeaturedActivityCalled {


    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    //view
    private MaterialTextView toolBarAccountText;
    private ImageView logo;
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
                logOutUser();
            } else if (result.getResultCode() == 86) {   // to come back to Home Page Activity and place the block fragment , if the back arrow or back pressed is called in ticket history activity
                chipNavigationBar.setItemSelected(R.id.menu_account, true);
            } else if (result.getResultCode() == 88) {   // to come back to Home Page Activity and place the block fragment , if the back arrow or back pressed is called in outing activity
                chipNavigationBar.setItemSelected(R.id.menu_block, true);
            } else if (result.getResultCode() == 90) {
                chipNavigationBar.setItemSelected(R.id.menu_block, true);
            }
        }
    });


    // empty constructor
    public HomePageActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();

        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic(User.getInstance().getRoomNo());


        // view
        chipNavigationBar = findViewById(R.id.bottom_navigation_view);
        chipNavigationBar.setItemSelected(R.id.menu_block, true);
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

    @Override
    public void roomTicketClicked() {
        Intent intent = new Intent(HomePageActivity.this, TicketHistoryActivity.class);
        intent.putExtra("Section", "ROOM");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void blockTicketClicked() {
        Intent intent = new Intent(HomePageActivity.this, TicketHistoryActivity.class);
        intent.putExtra("Section", "BLOCK");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void outingTicketClicked() {
        Intent intent = new Intent(HomePageActivity.this, TicketHistoryActivity.class);
        intent.putExtra("Section", "OUTING");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void applyOraSectionClicked() {
        Intent intent = new Intent(HomePageActivity.this, FeaturedActivity.class);
        intent.putExtra("Section", "ApplyOuting");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void oraHistorySectionClicked() {
        Intent intent = new Intent(HomePageActivity.this, FeaturedActivity.class);
        intent.putExtra("Section", "OutingHistory");
        activityResultLauncher.launch(intent);
    }

    @Override
    public void noticeActivityCalled(String docId) {
        Intent intent = new Intent(HomePageActivity.this, NoticeActivity.class);
        System.out.println("In Home Page ID =  " + docId);
        intent.putExtra("DOC_ID", docId);
        activityResultLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void logOutUser() {
        AppNotification.getInstance().unSubscribeAllTopics();
        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();
    }


    @Override
    public void userProfileClicked() {
        intentTo(SettingsMenu.PROFILE);
    }

    @Override
    public void publicProfileClicked() {
        intentTo(SettingsMenu.PUBLIC_PROFILE);
    }

    @Override
    public void ticketHistoryClicked() {

    }

    @Override
    public void notificationsClicked() {
        intentTo(SettingsMenu.NOTIFICATIONS);
    }

    @Override
    public void aboutAppClicked() {
        intentTo(SettingsMenu.ABOUT);
    }

    @Override
    public void reportIssueClicked() {

    }

    @Override
    public void spottedBugClicked() {

    }

    @Override
    public void haveSuggestionsClicked() {

    }

    private void intentTo(SettingsMenu settingsMenu) {
        Intent intent = new Intent(HomePageActivity.this, AccountMenuActivity.class);
        intent.putExtra("SECTION", settingsMenu.getValue());
        activityResultLauncher.launch(intent);
    }

    private void intentToFeaturedActivity(FeaturedMenu featuredMenu) {
        Intent intent = new Intent(HomePageActivity.this, FeaturedActivity.class);
        intent.putExtra("SECTION", featuredMenu.toString());
        activityResultLauncher.launch(intent);
    }

    @Override
    public void requestOutingSectionFragment() {
        intentToFeaturedActivity(FeaturedMenu.OUTING_REQUEST);
    }

    @Override
    public void requestTravelCompanion() {
        intentToFeaturedActivity(FeaturedMenu.TRAVEL_COMPANION);
    }

}