package com.anubhav.vitinsiderhostel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    private final String[] genderOptions = {" ", "MALE", "FEMALE", "OTHER"};
    private final String[] userTypeOptions = {" ", "STUDENT", "FACULTY"};

    private AutoCompleteTextView genderTextView;
    private ArrayAdapter<String> genderAdapter;

    private AutoCompleteTextView userTypeTextView;
    private ArrayAdapter<String> userTypeAdapter;

    private String inputName;
    private String inputMail;
    private String inputPassword;
    private char inputUserType;
    private char inputGender;

    private boolean validMail = false;
    private boolean validName = false;
    private boolean validPassword = false;
    private boolean validUserType = false;
    private boolean validGender = false;


    private final String studentMailPattern = "@vitstudent.ac.in";
    private final String facultyMailPattern = "@vit.ac.in";

    private TextInputEditText nameEt, mailEt, passwordEt;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference forHosteler = db.collection("Hostellers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEt = findViewById(R.id.registerPgeNameTxt);
        mailEt = findViewById(R.id.registerPgeMailTxt);
        passwordEt = findViewById(R.id.registerPgePasswordTxt);

        MaterialButton createAccount = findViewById(R.id.registerPgeRegisterBtn);
        ImageButton toLogin = findViewById(R.id.registerPgeArrowBtn);

        genderTextView = findViewById(R.id.registerPgeGenderTxt);
        genderAdapter = new ArrayAdapter<>(this, R.layout.drop_down_option, genderOptions);
        genderTextView.setAdapter(genderAdapter);

        userTypeTextView = findViewById(R.id.registerPgeUserTypeTxt);
        userTypeAdapter = new ArrayAdapter<>(this, R.layout.drop_down_option, userTypeOptions);
        userTypeTextView.setAdapter(userTypeAdapter);

        // drop down menu click listener
        genderTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String val = adapterView.getItemAtPosition(i).toString();
                switch (val) {
                    case " ":
                        genderTextView.setError("Select Gender");
                        validGender = false;
                        return;
                    case "MALE":
                        genderTextView.setError(null);
                        inputGender = 'M';
                        break;
                    case "FEMALE":
                        genderTextView.setError(null);
                        inputGender = 'F';
                        break;
                    case "OTHER":
                        genderTextView.setError(null);
                        inputGender = 'O';
                        break;
                }
                validGender = true;
            }
        });

        userTypeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String val = adapterView.getItemAtPosition(i).toString();
                switch (val) {
                    case " ":
                        userTypeTextView.setError("Select User Type");
                        validUserType = false;
                        validMail = false;
                        return;
                    case "STUDENT":
                        userTypeTextView.setError(null);
                        String temp = Objects.requireNonNull(mailEt.getText()).toString().trim();
                        if (temp.isEmpty()) {
                            mailEt.setError("Enter student Mail ID");
                            validMail = false;
                        } else if (!temp.endsWith(studentMailPattern)) {
                            mailEt.setError("Incorrect student Mail ID !");
                            validMail = false;
                        } else {
                            mailEt.setError(null);
                            validMail = true;
                        }
                        inputUserType = 'S';
                        break;
                    case "FACULTY":
                        userTypeTextView.setError(null);
                        temp = Objects.requireNonNull(mailEt.getText()).toString().trim();
                        if (temp.isEmpty()) {
                            mailEt.setError("Enter faculty Mail ID");
                            validMail = false;
                        } else if (!temp.endsWith(facultyMailPattern)) {
                            mailEt.setError("Incorrect faculty Mail ID !");
                            validMail = false;
                        } else {
                            mailEt.setError(null);
                            validMail = true;
                        }
                        inputUserType = 'F';
                        break;
                }
                validUserType = true;

            }
        });

        // name change listeners
        nameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Objects.requireNonNull(nameEt.getText()).toString().trim().isEmpty()) {
                        nameEt.setError("Name is required");
                    }
                }
            }
        });

        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameEt.setError(null);
                inputName = Objects.requireNonNull(nameEt.getText()).toString().trim();
                if (inputName.isEmpty()) {
                    nameEt.setError("Name Cannot be Empty");
                    validName = false;
                } else {
                    validName = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                if (TextUtils.isEmpty(inputMail)) {
                    mailEt.setError("Mail ID is required !");
                    validMail = false;
                } else if (inputUserType == 'S' && !inputMail.endsWith(studentMailPattern)) {
                    validMail = false;
                } else if (inputUserType == 'F' && !inputMail.endsWith(facultyMailPattern)) {
                    validMail = false;
                } else {
                    validMail = true;
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
        createAccount.setOnClickListener(this);
        toLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.registerPgeRegisterBtn) {
            validation();
        } else if (id == R.id.registerPgeArrowBtn) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
    }

    private void validation() {

        if (!validName) {
            nameEt.setError("Name is required");
            return;
        }

        if (!validMail) {
            mailEt.setError("Invalid Mail ID");
            return;
        }

        if (!validPassword) {
            passwordEt.setError("Minimum 10 characters");
            return;
        }

        if (!validGender) {
            return;
        }

        if (!validUserType) {
            return;
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RegisterActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Gender option cannot be modified later.\nPlease proceed if you have selected the correct gender option.");
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();


    }

    private void uploadData() {
        if(inputUserType=='F'){

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}