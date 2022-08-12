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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.Branch;
import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnAppErrorCreated;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.AppError;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, iOnAppErrorCreated {


    //mail domain pattern
    private final String studentMailPattern = "@vitstudent.ac.in";

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userDetailsSection = db.collection(Mod.USD.toString());
    private final CollectionReference hostelDetailsSection = db.collection(Mod.HOD.toString());
    private final CollectionReference feedbackSection = db.collection(Mod.FBK.toString());

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

        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameEt.setError(null);
                inputName = Objects.requireNonNull(nameEt.getText()).toString().trim();
                if (inputName.isEmpty()) {
                    nameEt.setError("Username cannot be Empty");
                    validName = false;
                } else if (inputName.contains(" ")) {
                    nameEt.setError("Username cannot have blank space");
                    validName = false;
                } else if (inputName.contains("/") || inputName.contains("\\\\")) {
                    nameEt.setError("Username cannot have slashes");
                    validName = false;
                } else if (Character.isDigit(inputName.toCharArray()[0])) {
                    nameEt.setError("Username cannot start with digit");
                    validName = false;
                } else if (inputName.contains("&")) {
                    nameEt.setError("Username cannot have ampersand");
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
                } else if (!inputMail.endsWith(studentMailPattern)) {
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
    private void uploadData() throws NoSuchAlgorithmException {

        final String scrambleValue = Scramble.getScramble(inputMail.toLowerCase(Locale.ROOT));

        // verifying if user is hosteler
        hostelDetailsSection
                .document(Mod.HOS.toString())
                .collection(Mod.DET.toString())
                .document(scrambleValue)
                .get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    //register with mailID and password
                    //creating user account
                    firebaseAuth
                            .createUserWithEmailAndPassword(inputMail, inputPassword)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {

                                    currentUser = firebaseAuth.getCurrentUser();
                                    assert currentUser != null;
                                    final String currentUserID = currentUser.getUid();
                                    final String registerNum = snapshot.getString("studentRegisterNumber");
                                    final String block = snapshot.getString("studentBlock");
                                    final String roomNo = snapshot.getString("roomNo");

                                    assert registerNum != null;
                                    final String subString = registerNum.substring(2, 5).toUpperCase(Locale.ROOT);
                                    final String studentBranch = Branch.getValue(subString);

                                    assert roomNo != null;
                                    assert block != null;
                                    DocumentReference roomDocument = hostelDetailsSection.document(Mod.HOR.toString()).collection(Mod.getBlock(block)).document(roomNo);

                                    // fetching all room details
                                    roomDocument
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {

                                                    final String acOrNonAc = Objects.requireNonNull(documentSnapshot.get("type")).toString();
                                                    final String beds = Objects.requireNonNull(documentSnapshot.get("bed")).toString();
                                                    final String type = beds + "|" + acOrNonAc;
                                                    //linking the username and user contact number with the respective userID using a HashMap named userObject
                                                    Map<String, Object> userMap = new HashMap<>();

                                                    userMap.put("user_Id", currentUserID);
                                                    userMap.put("userName", inputName);
                                                    userMap.put("avatar", 100);
                                                    userMap.put("userMailID", inputMail.toLowerCase(Locale.ROOT));
                                                    userMap.put("studentRegisterNumber", registerNum.toUpperCase(Locale.ROOT));
                                                    userMap.put("userType", "STUDENT");
                                                    userMap.put("studentBlock", block);

                                                    userMap.put("studentBranch", studentBranch);

                                                    userMap.put("studentNativeLanguage", "N/A");
                                                    userMap.put("userContactNumber", "N/A");

                                                    userMap.put("roomNo", roomNo);
                                                    userMap.put("roomType", type);
                                                    userMap.put("isAdmin", "0");

                                                    // uploading user details
                                                    userDetailsSection
                                                            .document(Mod.USSTU.toString())
                                                            .collection(Mod.DET.toString())
                                                            .document(currentUserID)
                                                            .set(userMap)
                                                            .addOnCompleteListener(task11 -> {
                                                                if (task11.isSuccessful()) {
                                                                    // update the tenant details
                                                                    hostelDetailsSection
                                                                            .document(Mod.TED.toString())
                                                                            .collection(Mod.DET.toString())
                                                                            .document(scrambleValue)
                                                                            .update(
                                                                                    "tenantUserName", inputName,
                                                                                    "tenantAvatar", 100
                                                                            )
                                                                            .addOnCompleteListener(task2 -> {
                                                                                if (!task2.isSuccessful()) {
                                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                                    AlertDisplay alertDisplay = new AlertDisplay("ERROR CODE "+ErrorCode.RA005.getErrorCode(), ErrorCode.RA005.getErrorMessage(), RegisterActivity.this);
                                                                                    alertDisplay.displayAlert();
                                                                                    AppError appError = new AppError(ErrorCode.RA005.getErrorCode(), inputMail);
                                                                                    onAppErrorCreated.checkIfAlreadyReported(appError,"Issue Has Been Reported");
                                                                                }
                                                                            });

                                                                    //second onComplete listener ( to send verification link)
                                                                    Objects.requireNonNull(firebaseAuth.getCurrentUser())
                                                                            .sendEmailVerification().addOnCompleteListener(task112 -> {
                                                                        if (task112.isSuccessful()) {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            //create intent for going to login activity
                                                                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RegisterActivity.this);
                                                                            builder.setTitle("Registered Successfully");
                                                                            builder.setMessage("Verify the link that is sent to your VIT mail and then login");
                                                                            builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                                                                                // Intent to login page
                                                                                toLoginPage();
                                                                            });
                                                                            builder.show();
                                                                        } else {
                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                            AlertDisplay alertDisplay = new AlertDisplay("ERROR CODE "+ErrorCode.RA006.getErrorCode(), ErrorCode.RA006.getErrorMessage(), RegisterActivity.this);
                                                                            alertDisplay.displayAlert();
                                                                            AppError appError = new AppError(ErrorCode.RA006.getErrorCode(), inputMail);
                                                                            onAppErrorCreated.checkIfAlreadyReported(appError,"Issue has been reported,You will be contacted soon");
                                                                        }
                                                                    });

                                                                } else {

                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    AlertDisplay alertDisplay = new AlertDisplay("ERROR CODE "+ErrorCode.RA004.getErrorCode(), ErrorCode.RA004.getErrorMessage(), RegisterActivity.this);
                                                                    alertDisplay.displayAlert();
                                                                    AppError appError = new AppError(ErrorCode.RA004.getErrorCode(), inputMail);
                                                                    onAppErrorCreated.checkIfAlreadyReported(appError,"Issue has been reported,You will be contacted soon");
                                                                }
                                                            });

                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    AlertDisplay alertDisplay = new AlertDisplay("ERROR CODE "+ErrorCode.RA003.getErrorCode(), ErrorCode.RA003.getErrorMessage(), RegisterActivity.this);
                                                    alertDisplay.displayAlert();
                                                    AppError appError = new AppError(ErrorCode.RA003.getErrorCode(), inputMail);
                                                    onAppErrorCreated.checkIfAlreadyReported(appError,"Issue has been reported,You will be contacted soon");
                                                }
                                            });


                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    AlertDisplay alertDisplay = new AlertDisplay("ERROR CODE "+ErrorCode.RA002.getErrorCode(), Objects.requireNonNull(task1.getException()).getMessage(), RegisterActivity.this);
                                    alertDisplay.displayAlert();
                                    AppError appError = new AppError(ErrorCode.RA002.getErrorCode(), inputMail);
                                    onAppErrorCreated.checkIfAlreadyReported(appError,"Issue has been reported,You will be contacted soon");
                                }
                            });

                } else {
                    // in case user  record doesn't match with the hostlers section
                    progressBar.setVisibility(View.INVISIBLE);
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RegisterActivity.this);
                    builder.setTitle("ERROR CODE "+ErrorCode.RA001.getErrorCode());
                    builder.setMessage(ErrorCode.RA001.getErrorMessage());
                    builder.setPositiveButton("But I'm Hosteler", (dialogInterface, i) -> {
                        AppError appError = new AppError(ErrorCode.RA001.getErrorCode(), inputMail);
                        onAppErrorCreated.checkIfAlreadyReported(appError,"Issue has been reported,You will be contacted soon");
                    });
                    builder.setNegativeButton("Back", (dialogInterface, i) -> {
                    });
                    builder.show();
                }

            }


        });


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
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


    @Override
    public void checkIfAlreadyReported(AppError appError,String message) {
        feedbackSection
                .document(Mod.REPISSU.toString())
                .collection(Mod.USSTU.toString()).whereEqualTo("errorCode", appError.getErrorCode()).whereEqualTo("reporter", appError.getReporter()).whereEqualTo("status", TicketStatus.BOOKED.toString())
                .get().addOnCompleteListener(task -> {
            boolean flag = false;
            if (task.isSuccessful()) {
                flag = task.getResult().size() > 0;
            }
            onAppErrorCreated.getQueryResult(appError,message,flag);
        });
    }

    @Override
    public void getQueryResult(AppError appError,String message,boolean flag) {
        if (flag) {
            callSnackBar("Issue has already been reported");
        } else {
            reportIssue(appError,message);
        }
    }


    private void reportIssue(AppError appError,String message){
        feedbackSection
                .document(Mod.REPISSU.toString())
                .collection(Mod.USSTU.toString())
                .document()
                .set(appError).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        onAppErrorCreated.IssueReported(message);
                    }
                });
    }

    @Override
    public void IssueReported(String message) {
        callSnackBar(message);
    }


}


