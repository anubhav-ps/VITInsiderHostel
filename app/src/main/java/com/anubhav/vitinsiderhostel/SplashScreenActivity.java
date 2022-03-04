package com.anubhav.vitinsiderhostel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.anubhav.vitinsiderhostel.adapters.TypeWriter;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    //firebase declarations
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;
    private boolean proceed = false;

    private LocalSqlDatabase localSqlDatabase ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        localSqlDatabase = new LocalSqlDatabase(SplashScreenActivity.this);

        //firebase auth instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> {
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                firebaseUser = firebaseAuth.getCurrentUser();
                proceed = true;
            } else if (firebaseUser == null) {
                proceed = false;
            }
        };

        TypeWriter typeWriter = findViewById(R.id.splashScreenPgeText);
        typeWriter.setText("");
        typeWriter.setCharacterDelay(120);

        int WAIT_TIME = 1800;
        new Handler().postDelayed(() -> typeWriter.animateText("I miss your maggi :("), WAIT_TIME);

        int SPLASH_SCREEN = 4400;

        new Handler().postDelayed(() -> {
            if (proceed) {
                // todo retrieve user data
                User user = User.getInstance();
                user = localSqlDatabase.getCurrentUser();
                Intent intent = new Intent(SplashScreenActivity.this, HomePageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();

            } else {
                Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
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


}