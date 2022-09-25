package com.anubhav.vitinsiderhostel.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.interfaces.iOnStartActivityCheckDone;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity implements iOnStartActivityCheckDone {

    //firebase auth declarations
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;

    //listeners
    iOnStartActivityCheckDone onStartActivityCheckDone;

    //local database
    private LocalSqlDatabase localSqlDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        localSqlDatabase = new LocalSqlDatabase(SplashScreenActivity.this);

        /*FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());*/

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        //firebase auth instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        onStartActivityCheckDone = this;

        //firebase authState listener definition
        authStateListener = firebaseAuth -> firebaseUser = firebaseAuth.getCurrentUser();

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null && firebaseUser.isEmailVerified()) {

            User user = User.getInstance();
            user = localSqlDatabase.getCurrentUser();
            onStartActivityCheckDone.initiatePageChange(true);

        } else if (firebaseUser == null) {
            localSqlDatabase.deleteCurrentUser();
            localSqlDatabase.deleteAllTenants();
            onStartActivityCheckDone.initiatePageChange(false);
        }
    }


    @Override
    public void initiatePageChange(boolean proceed) {
        int SPLASH_SCREEN = 600;

        new Handler().postDelayed(() -> {
            Intent intent;
            if (proceed) {
                intent = new Intent(SplashScreenActivity.this, HomePageActivity.class);
            } else {
                intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();
        }, SPLASH_SCREEN);

    }

    //process 0 and 1
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (User.getInstance() == null) {
            User user = null;
            user = localSqlDatabase.getCurrentUser();
        }
    }

}