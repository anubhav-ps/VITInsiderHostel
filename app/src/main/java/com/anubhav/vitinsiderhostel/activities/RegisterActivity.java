package com.anubhav.vitinsiderhostel.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.Path;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnAppErrorCreated;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.AppError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, iOnAppErrorCreated {


    //mail domain pattern
    private final String studentMailPattern = "@vitstudent.ac.in";

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference hostelStudentsSection = db.collection(Path.HOSTEL_STUDENTS.getPath());
    private final CollectionReference accountsSection = db.collection(Path.ACCOUNTS.getPath());
    private final CollectionReference feedbackSection = db.collection(Path.FEEDBACKS.getPath());


    // input field views
    private TextInputEditText nameEt, mailEt, passwordEt;
    private ProgressBar progressBar;
    // input field string values
    private String inputName;
    private String inputMail;
    private String inputPassword;

    //input flags
    private boolean validMail = false;
    private boolean validName = false;
    private boolean validPassword = false;
    //firebase authentication declarations
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;

    //listeners
    private iOnAppErrorCreated onAppErrorCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //firebase auth instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        // authStateListener definition
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {

            } else {

            }
        };

        nameEt = findViewById(R.id.registerPgeNameTxt);
        mailEt = findViewById(R.id.registerPgeMailTxt);
        passwordEt = findViewById(R.id.registerPgePasswordTxt);
        progressBar = findViewById(R.id.registerPgeProgressBar);

        MaterialButton createAccount = findViewById(R.id.registerPgeRegisterBtn);
        ImageButton toLogin = findViewById(R.id.registerPgeArrowBtn);


        onAppErrorCreated = this;

        // name change listeners
        nameEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (Objects.requireNonNull(nameEt.getText()).toString().trim().isEmpty()) {
                    nameEt.setError("Name is required");
                }
            }
        });

        final String username_pattern = "^(?=.{8,20}$)(?!.*[_]{2})[a-zA-Z0-9_]+";

        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameEt.setError(null);
                inputName = Objects.requireNonNull(nameEt.getText()).toString().trim();

                Pattern p = Pattern.compile(username_pattern);
                Matcher m = p.matcher(inputName);

                if (!m.matches()) {
                    nameEt.setError("Username must be 8 to 20 characters wide.No special characters allowed except '-'");
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
        mailEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (Objects.requireNonNull(mailEt.getText()).toString().trim().isEmpty()) {
                    mailEt.setError("Mail ID is required !");
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
                } else validMail = inputMail.endsWith(studentMailPattern);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // password change listeners
        passwordEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (Objects.requireNonNull(passwordEt.getText()).toString().trim().isEmpty()) {
                    passwordEt.setError("Minimum 10 characters");
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


    //onclick listener
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.registerPgeRegisterBtn) {
            try {
                validation();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.registerPgeArrowBtn) {
            toLoginPage();
        }
    }


    // validation
    private void validation() throws NoSuchAlgorithmException {

        if (!validName && !validMail && !validPassword) {
            final String message = "Fill all the fields to proceed !";
            callSnackBar(message);
            return;
        }

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

        progressBar.setVisibility(View.VISIBLE);
        uploadData();

    }

    // push to cloud
    private void uploadData() {

        Map<String, Object> request = new HashMap<>();
        request.put("userName", inputName);
        request.put("mailId", inputMail.toLowerCase(Locale.ROOT));


        // verifying if user is hosteler
        hostelStudentsSection
                .whereEqualTo("studentMailId", inputMail.toLowerCase(Locale.ROOT))
                .limit(1).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // not an hosteller
                            progressBar.setVisibility(View.INVISIBLE);
                            promptNonHosteler();
                        } else {
                            // is a hosteler proceed with account creation

                            firebaseAuth.createUserWithEmailAndPassword(inputMail, inputPassword)
                                    .addOnCompleteListener(task13 -> {
                                        if (task13.isSuccessful()) {
                                            currentUser = firebaseAuth.getCurrentUser();
                                            assert currentUser != null;
                                            String userId = currentUser.getUid();
                                            request.put("user_UID", userId);

                                            accountsSection.document("REQUEST_Q").collection("FILES").document()
                                                    .set(request)
                                                    .addOnCompleteListener(task12 -> {
                                                        if (task12.isSuccessful()) {
                                                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                                                    .addOnCompleteListener(task1 -> {
                                                                        if (task1.isSuccessful()) {
                                                                            promptRegisteredSuccessfully();
                                                                        } else {
                                                                            promptFailedToSendVerification();
                                                                        }
                                                                    }).addOnFailureListener(RegisterActivity.this::onFailure);
                                                        } else {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    }).addOnFailureListener(RegisterActivity.this::onFailure);
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }).addOnFailureListener(RegisterActivity.this::onFailure);
                        }
                    }
                }).addOnFailureListener(RegisterActivity.this::onFailure);
    }

    // on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(RegisterActivity.this, findViewById(R.id.registerPge), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.navy_blue));
        snackbar.show();
    }


    // go to login page
    private void toLoginPage() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
    }

    //---process 0 and process 1 definition
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        currentUser = firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void promptNonHosteler() {
        progressBar.setVisibility(View.INVISIBLE);

        AlertDisplay alertDisplay = new AlertDisplay("ERROR CODE " + ErrorCode.RA001.getErrorCode(), ErrorCode.RA001.getErrorMessage(), RegisterActivity.this);
        alertDisplay.getBuilder().setPositiveButton("But I'm Hosteler", (dialogInterface, i) -> {
            AppError appError = new AppError(ErrorCode.RA001.getErrorCode(), inputMail);
            onAppErrorCreated.checkIfAlreadyReported(appError, "Issue has been reported,You will be contacted soon");
        });
        alertDisplay.getBuilder().setNegativeButton("Back", null);
        alertDisplay.getBuilder().setCancelable(false);
        alertDisplay.display();

    }

    private void promptRegisteredSuccessfully() {
        progressBar.setVisibility(View.INVISIBLE);
        AlertDisplay alertDisplay = new AlertDisplay("Registered Successfully", "Verify the link that is sent to your VIT mail and then login", RegisterActivity.this);
        alertDisplay.getBuilder().setPositiveButton("Ok", (dialogInterface, i) -> toLoginPage());
        alertDisplay.getBuilder().setCancelable(false);
        alertDisplay.display();
    }

    private void promptFailedToSendVerification() {
        progressBar.setVisibility(View.INVISIBLE);
        AlertDisplay alertDisplay = new AlertDisplay("ERROR CODE " + ErrorCode.RA006.getErrorCode(), ErrorCode.RA006.getErrorMessage(), RegisterActivity.this);
        alertDisplay.getBuilder().setCancelable(false);
        alertDisplay.displayAlert();
        AppError appError = new AppError(ErrorCode.RA006.getErrorCode(), inputMail);
        onAppErrorCreated.checkIfAlreadyReported(appError, "Issue has been reported,You will be contacted soon");
    }

    private void onFailure(Exception e) {
        progressBar.setVisibility(View.INVISIBLE);
        callSnackBar(e.getMessage());
    }


    @Override
    public void checkIfAlreadyReported(AppError appError, String message) {
        feedbackSection
                .document(Path.ISSUES.getPath())
                .collection(Path.FILES.getPath())
                .whereEqualTo("errorCode", appError.getErrorCode()).whereEqualTo("reporter", appError.getReporter()).whereEqualTo("status", TicketStatus.BOOKED.toString())
                .get().addOnCompleteListener(task -> {
                    boolean flag = false;
                    if (task.isSuccessful()) {
                        flag = task.getResult().size() > 0;
                    }
                    onAppErrorCreated.getQueryResult(appError, message, flag);
                });
    }

    @Override
    public void getQueryResult(AppError appError, String message, boolean flag) {
        if (flag) {
            callSnackBar("Issue has already been reported");
        } else {
            reportIssue(appError, message);
        }
    }


    private void reportIssue(AppError appError, String message) {
        feedbackSection
                .document(Path.ISSUES.getPath())
                .collection(Path.FILES.getPath())
                .document()
                .set(appError).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onAppErrorCreated.IssueReported(message);
                    }
                });
    }

    @Override
    public void IssueReported(String message) {
        callSnackBar(message);
    }


}


