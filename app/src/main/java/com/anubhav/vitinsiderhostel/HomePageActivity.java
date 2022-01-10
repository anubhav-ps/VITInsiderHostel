package com.anubhav.vitinsiderhostel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePageActivity extends AppCompatActivity {

    //firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {


            } else {

            }
        };


        MaterialButton logOut = findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(HomePageActivity.this, "Logging out, see you soon !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomePageActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
            }
        });
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

}