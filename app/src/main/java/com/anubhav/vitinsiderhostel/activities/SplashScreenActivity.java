package com.anubhav.vitinsiderhostel.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.interfaces.iOnStartActivityCheckDone;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity implements iOnStartActivityCheckDone {

    //firebase  declarations
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference hostelDetailsSection = db.collection(Mod.HOD.toString());
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

        //firebase auth instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        onStartActivityCheckDone = this;

        //firebase authState listener definition
        authStateListener = firebaseAuth -> {
            firebaseUser = firebaseAuth.getCurrentUser();
        };

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            User user = User.getInstance();
            user = localSqlDatabase.getCurrentUser();

            String scrambleMailValue = "";
            try {
                scrambleMailValue = Scramble.getScramble(Objects.requireNonNull(firebaseUser.getEmail()).toLowerCase(Locale.ROOT));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            hostelDetailsSection
                    .document(Mod.HOS.toString())
                    .collection(Mod.DET.toString())
                    .document(scrambleMailValue)
                    .get().addOnSuccessListener(documentSnapshot -> {
                boolean proceed = false;
                if (documentSnapshot.exists()) {
                    String roomNo = Objects.requireNonNull(documentSnapshot.get("roomNo")).toString();
                    String studentBlock = Objects.requireNonNull(documentSnapshot.get("studentBlock")).toString();
                    String registerNum = Objects.requireNonNull(documentSnapshot.get("studentRegisterNumber")).toString();
                    if (!roomNo.equals(User.getInstance().getRoomNo()) || !studentBlock.equals(User.getInstance().getStudentBlock()) || !registerNum.equals(User.getInstance().getStudentRegisterNumber())) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(SplashScreenActivity.this, "Updates in room details, Login again!", Toast.LENGTH_SHORT).show();
                        localSqlDatabase.deleteCurrentUser();
                        localSqlDatabase.deleteAllTenants();
                    } else {
                        proceed = true;
                    }

                } else {
                    FirebaseAuth.getInstance().signOut();
                    proceed = false;
                    localSqlDatabase.deleteCurrentUser();
                    localSqlDatabase.deleteAllTenants();

                }
                onStartActivityCheckDone.initiatePageChange(proceed);
            });

        } else if (firebaseUser == null) {
            onStartActivityCheckDone.initiatePageChange(false);

        }
    }


    @Override
    public void initiatePageChange(boolean proceed) {
        int SPLASH_SCREEN = 600;

        new Handler().postDelayed(() -> {
            if (proceed) {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (User.getInstance() == null) {
            User user = null;
            user = localSqlDatabase.getCurrentUser();
        }
    }

}