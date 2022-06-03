package com.anubhav.vitinsiderhostel.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.fragments.OutingRequestFragment;
import com.google.android.material.textview.MaterialTextView;

public class FeaturedActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured);


        final  String section = getIntent().getStringExtra("Section");
        if (savedInstanceState == null) {
            if (section.equalsIgnoreCase("Apply")){
                OutingRequestFragment outingRequestFragment = new OutingRequestFragment();
                makeTransaction(outingRequestFragment);
            }
        }



        ImageButton backArrowToBlockFragment = findViewById(R.id.featuredActivityBackArrow);
        backArrowToBlockFragment.setOnClickListener(this);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id==R.id.featuredActivityBackArrow){
            Intent intent = new Intent();
            setResult(88, intent);
            finish();
        }
    }

    public void makeTransaction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.featuredMenuContainer, fragment);
        fragmentTransaction.commit();
    }
}