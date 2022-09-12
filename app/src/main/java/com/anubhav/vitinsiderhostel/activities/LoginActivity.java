package com.anubhav.vitinsiderhostel.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnAppErrorCreated;
import com.anubhav.vitinsiderhostel.interfaces.iOnFCMTokenGenerated;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.interfaces.iOnRoomTenantListDownloaded;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.AppError;
import com.anubhav.vitinsiderhostel.models.RoomTenants;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;
import com.anubhav.vitinsiderhostel.notifications.AppNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, iOnNotifyDbProcess, iOnRoomTenantListDownloaded, iOnAppErrorCreated, iOnFCMTokenGenerated {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userDetailsSection = db.collection(Mod.USD.toString());
    private final CollectionReference hostelDetailsSection = db.collection(Mod.HOD.toString());
    private final CollectionReference feedbackSection = db.collection(Mod.FBK.toString());
    private final CollectionReference tokenSection = db.collection(Mod.FCM.toString());

    //string objects
    private final String studentMailPattern = "@vitstudent.ac.in";
    //firebase auth  declarations
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;
    //views
    private TextInputEditText mailEt, passwordEt;
    private ProgressBar progressBar;
    //array list
    private List<String> roomMatesHashList;
    private String inputMail, inputPassword;

    //flags
    private boolean validMail = false, validPassword = false;

    //local database
    private LocalSqlDatabase localSqlDatabase;

    //listeners
    private iOnRoomTenantListDownloaded onRoomTenantListDownloaded;
    private iOnAppErrorCreated onAppErrorCreated;
    private iOnFCMTokenGenerated onFCMTokenGenerated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mailEt = findViewById(R.id.loginPgeMailTxt);
        passwordEt = findViewById(R.id.loginPgePasswordTxt);
        progressBar = findViewById(R.id.loginPgeProgressBar);


        MaterialButton login = findViewById(R.id.loginPgeLoginBtn);
        ImageButton toRegister = findViewById(R.id.loginPgeArrowBtn);
        MaterialTextView reset = findViewById(R.id.loginPgeReset);

        //firebase auth instantiation
        firebaseAuth = FirebaseAuth.getInstance();
        //firebase authState listener definition
        authStateListener = firebaseAuth -> firebaseUser = firebaseAuth.getCurrentUser();

        //listeners
        onRoomTenantListDownloaded = this;
        onAppErrorCreated = this;
        onFCMTokenGenerated = this;

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
                } else {
                    validMail = inputMail.endsWith(studentMailPattern);
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
        login.setOnClickListener(this);
        toRegister.setOnClickListener(this);
        reset.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.loginPgeLoginBtn) {
            try {
                validation();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.loginPgeArrowBtn) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            finish();
        } else if (id == R.id.loginPgeReset) {
            processForgotPassword();
        }
    }

    private void processForgotPassword() {
        //Alert box to get and check the security key
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Password Reset");
        alert.setMessage("Enter your V.I.T mail ID");
        //Set an EditText view to get the security key from the user
        final EditText input = new EditText(LoginActivity.this);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.requestFocus();
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Enter E-mail ID");
        input.setMaxLines(1);
        alert.setView(input);
        alert.setPositiveButton("Submit", (dialog, whichButton) -> {

            //variable to store the value entered by user
            progressBar.setVisibility(View.VISIBLE);
            final String value = input.getText().toString().trim();

            if (value.isEmpty()) {
                progressBar.setVisibility(View.INVISIBLE);
                String warning = "Mail ID is required";
                new MaterialAlertDialogBuilder(this)
                        .setMessage(warning).show();
            } else if ((value).endsWith(studentMailPattern)) {              //change this length
                firebaseAuth.sendPasswordResetEmail(value)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                String message = "Link to reset password has been sent to your V.I.T mail ID";
                                new MaterialAlertDialogBuilder(this)
                                        .setTitle("Password Reset Link Sent")
                                        .setMessage(message).show();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                String message = Objects.requireNonNull(task.getException()).getMessage();
                                new MaterialAlertDialogBuilder(this)
                                        .setTitle("Something went wrong :( !")
                                        .setMessage(message).show();
                            }
                        });
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                String warning = "Improper VIT mail ID";
                new MaterialAlertDialogBuilder(this)
                        .setMessage(warning).show();
            }

        });

        alert.show();
    }


    private void validation() throws NoSuchAlgorithmException {

        if (!validMail && !validPassword) {
            final String message = "Fill all the fields to proceed !";
            callSnackBar(message);
            return;
        }

        if (!validMail) {
            mailEt.setError("Invalid Mail ID");
            return;
        }

        if (!validPassword) {
            passwordEt.setError("Password cannot be less than 10 characters");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        loginUser(inputMail, inputPassword);

    }


    //logging user using firebase
    private void loginUser(String mailID, String password) throws NoSuchAlgorithmException {

        final String scrambleMailValue = Scramble.getScramble(mailID.toLowerCase(Locale.ROOT));

        firebaseAuth
                .signInWithEmailAndPassword(mailID, password)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {

                                    hostelDetailsSection
                                            .document(Mod.HOS.toString())
                                            .collection(Mod.DET.toString())
                                            .document(scrambleMailValue)
                                            .get().addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                            assert firebaseUser != null;

                                            final String currentUserMailId = firebaseUser.getEmail();
                                            final String currentUserId = firebaseUser.getUid();

                                            assert currentUserMailId != null;

                                            DocumentReference document = userDetailsSection.document(Mod.USSTU.toString()).collection(Mod.DET.toString()).document(currentUserId);
                                            document
                                                    .get()
                                                    .addOnSuccessListener(documentSnapshot1 -> {
                                                        if (documentSnapshot1.exists()) {

                                                            final String userID = Objects.requireNonNull(documentSnapshot1.get("user_Id")).toString();
                                                            final String userName = Objects.requireNonNull(documentSnapshot1.get("userName")).toString();
                                                            final String userMail = Objects.requireNonNull(documentSnapshot1.get("userMailID")).toString();
                                                            final Long userAvatar = (Long) Objects.requireNonNull(documentSnapshot1.get("avatar"));

                                                            final String userContactNum = Objects.requireNonNull(documentSnapshot1.get("userContactNumber")).toString();

                                                            final String userType = Objects.requireNonNull(documentSnapshot1.get("userType")).toString();
                                                            final String studentBlock = Objects.requireNonNull(documentSnapshot1.get("studentBlock")).toString();

                                                            final String studentRegisterNumber = Objects.requireNonNull(documentSnapshot1.get("studentRegisterNumber")).toString();
                                                            final String studentBranch = Objects.requireNonNull(documentSnapshot1.get("studentBranch")).toString();
                                                            final String studentNativeLanguage = Objects.requireNonNull(documentSnapshot1.get("studentNativeLanguage")).toString();
                                                            final String studentRoomNo = Objects.requireNonNull(documentSnapshot1.get("roomNo")).toString();
                                                            final String studentRoomType = Objects.requireNonNull(documentSnapshot1.get("roomType")).toString();
                                                            final String admin = Objects.requireNonNull(documentSnapshot1.get("isAdmin")).toString();
                                                            final boolean hasPublicProfile = (boolean) Objects.requireNonNull(documentSnapshot1.get("hasPublicProfile"));
                                                            final String  privateDocId = Objects.requireNonNull(documentSnapshot1.get("privateProfileID")).toString();

                                                            User user = User.getInstance();
                                                            user.setUser_Id(userID);
                                                            user.setUserName(userName);
                                                            user.setUserMailID(userMail);
                                                            user.setAvatar(userAvatar.intValue());
                                                            user.setStudentRegisterNumber(studentRegisterNumber);
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
                                                            user.setHasPublicProfile(hasPublicProfile);
                                                            user.setPrivateProfileID(privateDocId);


                                                            //Toast.makeText(LoginActivity.this,tNum , Toast.LENGTH_SHORT).show();

                                                            localSqlDatabase = new LocalSqlDatabase(LoginActivity.this, LoginActivity.this);

                                                            localSqlDatabase.addUser(user);

                                                            String scrambleRoomValue = "";

                                                            try {
                                                                scrambleRoomValue = Scramble.getScramble(studentRoomNo);
                                                            } catch (NoSuchAlgorithmException e) {
                                                                e.printStackTrace();
                                                            }

                                                            roomMatesHashList = new ArrayList<>();
                                                            hostelDetailsSection
                                                                    .document(Mod.HOT.toString())
                                                                    .collection(Mod.getBlock(studentBlock))
                                                                    .document(scrambleRoomValue)
                                                                    .get()
                                                                    .addOnCompleteListener(
                                                                            task12 -> {
                                                                                if (task12.isSuccessful()) {
                                                                                    DocumentSnapshot documentSnapshot22 = task12.getResult();
                                                                                    if (documentSnapshot22.exists()) {
                                                                                        final RoomTenants tenantHashMailList = documentSnapshot22.toObject(RoomTenants.class);
                                                                                        assert tenantHashMailList != null;
                                                                                        localSqlDatabase.setTotalTenants(tenantHashMailList.getList().size() - 1);
                                                                                        for (String t : tenantHashMailList.getList()) {
                                                                                            if (t.equals(scrambleMailValue))
                                                                                                continue;
                                                                                            roomMatesHashList.add(t);
                                                                                        }

                                                                                    }
                                                                                }
                                                                                onRoomTenantListDownloaded.notifyCompleteListDownload();
                                                                            }
                                                                    );

                                                        } else {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            callSnackBar("User Record Not Found");
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        callSnackBar(e.getMessage());
                                                    });
                                        } else {
                                            // in case user  record doesn't match with the hostlers section
                                            progressBar.setVisibility(View.INVISIBLE);
                                            AlertDisplay alertDisplay = new AlertDisplay("ERROR CODE " + ErrorCode.LA001.getErrorCode(), ErrorCode.LA001.getErrorMessage(), LoginActivity.this);
                                            alertDisplay.getBuilder().setPositiveButton("But I'm Hosteler", (dialogInterface, i) -> {
                                                AppError appError = new AppError(ErrorCode.RA001.getErrorCode(), mailID);
                                                onAppErrorCreated.checkIfAlreadyReported(appError, "Issue Has Been Reported,You Will Be Contacted Soon");
                                            });
                                            alertDisplay.getBuilder().setNegativeButton("Back", null);
                                            alertDisplay.display();
                                        }
                                    }).addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        callSnackBar(e.getMessage());
                                    });


                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    //Display an alert dialog for  not verifying the mail ID
                                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(
                                            LoginActivity.this);
                                    materialAlertDialogBuilder.setTitle("Not Verified User");
                                    materialAlertDialogBuilder.setMessage("Verify the link sent to your V.I.T mail during registration");
                                    materialAlertDialogBuilder.setPositiveButton("Resend Verification Link", (dialog, which) -> {
                                        //second onComplete listener
                                        Objects.requireNonNull(firebaseAuth.getCurrentUser())
                                                .sendEmailVerification()
                                                .addOnCompleteListener(task1 -> {

                                                    //goTo login activity on successful email verification
                                                    if (task1.isSuccessful()) {
                                                        //create intent for going to login activity
                                                        new MaterialAlertDialogBuilder(LoginActivity.this)
                                                                .setTitle("Verification Link has been sent")
                                                                .setMessage("Please check your V.I.T mail for verification, and then login again").show();
                                                    } else {
                                                        callSnackBar(Objects.requireNonNull(task1.getException()).getMessage());
                                                    }
                                                });
                                    });
                                    materialAlertDialogBuilder.setNegativeButton("Ok", (dialog, which) -> {

                                    });
                                    materialAlertDialogBuilder.show();
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                callSnackBar(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                ).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            callSnackBar(e.getMessage());
        });
    }


    @Override
    public void notifyCompleteDataDownload() {
        uploadToken();
    }

    @Override
    public void onTokenGenerated(String token) {
        //FCM_Tokens/UserId/-> token : value
        //-> lastUpdated : timestamp
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("lastUpdated", new Timestamp(new Date()));
        tokenSection.document(User.getInstance().getUser_Id()).set(tokenMap).addOnCompleteListener(task -> {

            if(!task.isSuccessful()){
                progressBar.setVisibility(View.INVISIBLE);
                callSnackBar("Try logging in again after sometime");
                logoutUser();
            }else{
                progressBar.setVisibility(View.INVISIBLE);
                onFCMTokenGenerated.onTokenPushed();
            }

        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            callSnackBar("Try logging in again after sometime");
            logoutUser();
        });
    }

    @Override
    public void onTokenPushed() {
        AppNotification.getInstance().subscribeAllTopics();
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
    }

    private void uploadToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    callSnackBar(Objects.requireNonNull(task.getException()).getMessage());
                    callSnackBar("Try logging in again after sometime");
                    logoutUser();
                }else{
                     onFCMTokenGenerated.onTokenGenerated(task.getResult());
                }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            callSnackBar(e.getMessage());
            callSnackBar("Try logging in again after sometime");
            logoutUser();
        });
    }
    @Override
    public void notifyUserUpdated() {

    }

    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        localSqlDatabase.deleteAllTenants();
        localSqlDatabase.deleteCurrentUser();
    }

    @Override
    public void notifyCompleteListDownload() {
        for (String mailHash : roomMatesHashList) {
            hostelDetailsSection
                    .document(Mod.TED.toString())
                    .collection(Mod.DET.toString())
                    .document(mailHash)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Tenant tenant = documentSnapshot.toObject(Tenant.class);
                                localSqlDatabase.addTenantInBackground(tenant);
                            }
                        }
                    });
        }
        roomMatesHashList.clear();
    }

    @Override
    public void checkIfAlreadyReported(AppError appError, String message) {
        feedbackSection
                .document(Mod.REPISSU.toString())
                .collection(Mod.USSTU.toString()).whereEqualTo("errorCode", appError.getErrorCode()).whereEqualTo("reporter", appError.getReporter()).whereEqualTo("status", TicketStatus.BOOKED.toString())
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
                .document(Mod.REPISSU.toString())
                .collection(Mod.USSTU.toString())
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

    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(LoginActivity.this, findViewById(R.id.loginPge), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.navy_blue));
        snackbar.show();
    }

    //process 0 and 1 operation
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
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