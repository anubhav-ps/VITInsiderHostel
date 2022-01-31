package com.anubhav.vitinsiderhostel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anubhav.vitinsiderhostel.models.User;
import com.anubhav.vitinsiderhostel.adapters.TypeWriter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {


    //firebase declarations
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;


    private boolean proceed = false;

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userSection = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


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
                final String user_mail_id = firebaseUser.getEmail();

                assert user_mail_id != null;
                final DocumentReference documentReference = db.collection("UserBlockRec").document(user_mail_id.toLowerCase(Locale.ROOT));

                documentReference
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                final String tempBlock = Objects.requireNonNull(documentSnapshot.get("block")).toString();
                                final String tempUserType = Objects.requireNonNull(documentSnapshot.get("userType")).toString();
                                final String tempDocID = Objects.requireNonNull(documentSnapshot.get("doc_id")).toString();
                                final DocumentReference document = userSection.document(tempUserType).collection(tempBlock).document(tempDocID);
                                document
                                        .get()
                                        .addOnSuccessListener(documentSnapshot1 -> {
                                            if (documentSnapshot1.exists()) {

                                                final String userID = Objects.requireNonNull(documentSnapshot1.get("user_Id")).toString();
                                                final String userDocId = Objects.requireNonNull(documentSnapshot1.get("doc_Id")).toString();

                                                final String userName = Objects.requireNonNull(documentSnapshot1.get("userName")).toString();
                                                final String userMail = Objects.requireNonNull(documentSnapshot1.get("userMailID")).toString();

                                                final String userContactNum = Objects.requireNonNull(documentSnapshot1.get("userContactNumber")).toString();

                                                final String userType = Objects.requireNonNull(documentSnapshot1.get("userType")).toString();
                                                final String studentBlock = Objects.requireNonNull(documentSnapshot1.get("studentBlock")).toString();

                                                final String studentBranch = Objects.requireNonNull(documentSnapshot1.get("studentBranch")).toString();
                                                final String studentNativeLanguage = Objects.requireNonNull(documentSnapshot1.get("studentNativeLanguage")).toString();
                                                final String studentRoomNo = Objects.requireNonNull(documentSnapshot1.get("roomNo")).toString();
                                                final String studentRoomType = Objects.requireNonNull(documentSnapshot1.get("roomType")).toString();
                                                final String admin = Objects.requireNonNull(documentSnapshot1.get("isAdmin")).toString();

                                                User user = User.getInstance();
                                                user.setUser_Id(userID);
                                                user.setDoc_Id(userDocId);
                                                user.setUserName(userName);
                                                user.setUserMailID(userMail);
                                                user.setUserContactNumber(userContactNum);
                                                user.setUserType(userType);
                                                user.setStudentBlock(studentBlock);
                                                user.setStudentBranch(studentBranch);
                                                user.setStudentNativeLanguage(studentNativeLanguage);
                                                user.setRoomNo(studentRoomNo);
                                                user.setRoomType(studentRoomType);
                                                boolean adminVal = false;
                                                if (admin.equalsIgnoreCase("1")) {
                                                    adminVal = true;
                                                }
                                                user.setAdmin(adminVal);

                                                Intent intent = new Intent(SplashScreenActivity.this, HomePageActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                                finish();

                                            } else {
                                                Toast.makeText(SplashScreenActivity.this, "User Record not found", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(e -> callSnackBar(e.getMessage()));
                            }
                        });
            } else {
                Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
        }, SPLASH_SCREEN);
    }

    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(SplashScreenActivity.this, findViewById(R.id.splashScreenPge), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(SplashScreenActivity.this,R.color.navy_blue));
        snackbar.show();
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