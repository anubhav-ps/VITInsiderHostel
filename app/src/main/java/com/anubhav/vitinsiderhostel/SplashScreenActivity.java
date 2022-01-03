package com.anubhav.vitinsiderhostel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.anubhav.vitinsiderhostel.ui.TypeWriter;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        TypeWriter typeWriter = findViewById(R.id.splashScreenPgeText);
        typeWriter.setText("");
        typeWriter.setCharacterDelay(120);

        int WAIT_TIME = 1800;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typeWriter.animateText("I miss your maggi :(");
            }
        }, WAIT_TIME);

        int SPLASH_SCREEN = 4400;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }

}