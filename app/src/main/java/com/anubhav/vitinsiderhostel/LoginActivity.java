package com.anubhav.vitinsiderhostel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String inputMail;
    private String inputPassword;

    private boolean validMail = false;
    private boolean validPassword = false;

    private final String studentMailPattern = "@vitstudent.ac.in";
    private final String facultyMailPattern = "@vit.ac.in";

    private TextInputEditText mailEt, passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mailEt = findViewById(R.id.loginPgeMailTxt);
        passwordEt = findViewById(R.id.loginPgePasswordTxt);

        MaterialButton login = findViewById(R.id.loginPgeLoginBtn);
        ImageButton toRegister = findViewById(R.id.loginPgeArrowBtn);

        // mail id change listeners
        mailEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Objects.requireNonNull(mailEt.getText()).toString().trim().isEmpty()) {
                        mailEt.setError("Mail ID is required !");
                    }
                }
            }
        });

        mailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mailEt.setError(null);
                inputMail = Objects.requireNonNull(mailEt.getText()).toString().trim();
                if (inputMail.isEmpty()) {
                    mailEt.setError("Mail ID is required !");
                    validMail = false;
                } else {
                    validMail = inputMail.endsWith(studentMailPattern) || inputMail.endsWith(facultyMailPattern);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // password change listeners
        passwordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Objects.requireNonNull(passwordEt.getText()).toString().trim().isEmpty()) {
                        passwordEt.setError("Minimum 10 characters");
                    }
                }
            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordEt.setError(null);
                inputPassword = Objects.requireNonNull(passwordEt.getText()).toString().trim();
                if (inputPassword.isEmpty()) {
                    passwordEt.setError("Password is required !");
                    validPassword = false;
                } else validPassword = inputPassword.length() >= 10;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // button click listeners
        login.setOnClickListener(this);
        toRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.loginPgeLoginBtn) {
            validation();
        } else if (id == R.id.loginPgeArrowBtn) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
    }

    private void validation() {
        if (!validMail) {
            mailEt.setError("Invalid Mail ID");
            return;
        }

        if (!validPassword) {
            passwordEt.setError("Password cannot be less than 10 characters");
            return;
        }

        checkUser();
    }

    private void checkUser() {
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}