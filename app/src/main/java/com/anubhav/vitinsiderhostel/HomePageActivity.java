package com.anubhav.vitinsiderhostel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomePageActivity extends AppCompatActivity implements AccountFragment.onUserProfileCalledListener {

    //firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private ChipNavigationBar chipNavigationBar;
    private Toolbar toolbar;
    private MaterialTextView toolBarAccountText;
    private ImageView logo;

    public HomePageActivity() {

    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == 84) {
                chipNavigationBar.setItemSelected(R.id.menu_account, true);
            }
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        chipNavigationBar = findViewById(R.id.bottom_navigation_view);
        chipNavigationBar.setItemSelected(R.id.menu_room, true);
        bottomNavigationViewSetup();

        toolbar = findViewById(R.id.toolbar);
        toolBarAccountText = findViewById(R.id.tool_bar_account_txt);
        logo = findViewById(R.id.tool_bar_logo);


        if (savedInstanceState == null) {
            RoomFragment roomFragment = new RoomFragment();
            toolBarAccountText.setVisibility(View.INVISIBLE);
            logo.setVisibility(View.VISIBLE);
            makeTransaction(roomFragment);
        }

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {


            } else {

            }
        };

    }

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

    private void placeAccountFragment() {
        AccountFragment accountFragment = new AccountFragment();
        toolBarAccountText.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        makeTransaction(accountFragment);
    }

    //function to make the fragment transaction
    public void makeTransaction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homePageFragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    //process 0 and 1 operation
    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onUserProfileCalled() {
        Intent intent = new Intent(HomePageActivity.this, UserProfileActivity.class);
        activityResultLauncher.launch(intent);
    }

}