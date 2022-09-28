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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.Path;
import com.anubhav.vitinsiderhostel.interfaces.iOnFCMTokenGenerated;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.interfaces.iOnRoomTenantListDownloaded;
import com.anubhav.vitinsiderhostel.models.RoomTenants;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;
import com.anubhav.vitinsiderhostel.notifications.AppNotification;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, iOnNotifyDbProcess, iOnRoomTenantListDownloaded, iOnFCMTokenGenerated {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference accountSection = db.collection(Path.ACCOUNTS.getPath());
    private final CollectionReference roomMatesSection = db.collection(Path.ROOM_MATES.getPath());
    private final CollectionReference roomMatesDetailSection = db.collection(Path.ROOM_MATE_DETAILS.getPath());
    private final CollectionReference tokenSection = db.collection(Path.FCM_TOKEN.getPath());

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
    private List<String> roomMatesList;
    private String inputMail, inputPassword;

    //flags
    private boolean validMail = false, validPassword = false;

    //local database
    private LocalSqlDatabase localSqlDatabase;

    //listeners
    private iOnRoomTenantListDownloaded onRoomTenantListDownloaded;
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
    private void loginUser(String mailID, String password) {

        firebaseAuth
                .signInWithEmailAndPassword(mailID, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {

                            firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;

                            final String currentUserMailId = firebaseUser.getEmail();
                            final String currentUserId = firebaseUser.getUid();

                            assert currentUserMailId != null;



                            accountSection.document(Path.STUDENTS.getPath())
                                    .collection(Path.FILES.getPath())
                                    .document(currentUserId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                User user = User.getInstance();

                                                DocumentSnapshot documentSnapshot = task.getResult();

                                                final String user_UID = Objects.requireNonNull(documentSnapshot.get("user_UID")).toString();
                                                final String userMailId = Objects.requireNonNull(documentSnapshot.get("userMailId")).toString();
                                                final String studentRegisterNumber = Objects.requireNonNull(documentSnapshot.get("studentRegisterNumber")).toString();
                                                final String studentName = Objects.requireNonNull(documentSnapshot.get("studentName")).toString();
                                                final String userName = Objects.requireNonNull(documentSnapshot.get("userName")).toString();
                                                final String studentBranch = Objects.requireNonNull(documentSnapshot.get("studentBranch")).toString();
                                                final String userContactNumber = Objects.requireNonNull(documentSnapshot.get("userContactNumber")).toString();
                                                final String studentNativeState = Objects.requireNonNull(documentSnapshot.get("studentNativeState")).toString();
                                                final Long nativeStateChanges = (Long) Objects.requireNonNull(documentSnapshot.get("nativeStateChanges"));
                                                final String userType = Objects.requireNonNull(documentSnapshot.get("userType")).toString();
                                                final Long avatar = (Long) Objects.requireNonNull(documentSnapshot.get("avatar"));
                                                final String parentMailId = Objects.requireNonNull(documentSnapshot.get("parentMailId")).toString();
                                                final String studentBlock = Objects.requireNonNull(documentSnapshot.get("studentBlock")).toString();
                                                final String roomNo = Objects.requireNonNull(documentSnapshot.get("roomNo")).toString();
                                                final Long beds = (Long) Objects.requireNonNull(documentSnapshot.get("beds"));
                                                final boolean ac = (boolean) Objects.requireNonNull(documentSnapshot.get("ac"));
                                                final String mess = Objects.requireNonNull(documentSnapshot.get("mess")).toString();
                                                final boolean hasPublicProfile = (boolean) Objects.requireNonNull(documentSnapshot.get("hasPublicProfile"));
                                                final String publicBio = Objects.requireNonNull(documentSnapshot.get("publicBio")).toString();
                                                final Long publicColor = (Long) Objects.requireNonNull(documentSnapshot.get("publicColor"));
                                                final String privateProfileId = Objects.requireNonNull(documentSnapshot.get("privateProfileId")).toString();
                                                final String fcmToken = Objects.requireNonNull(documentSnapshot.get("fcmToken")).toString();

                                                user.setUser_UID(user_UID);
                                                user.setUserMailId(userMailId);
                                                user.setStudentRegisterNumber(studentRegisterNumber);
                                                user.setStudentName(studentName);
                                                user.setUserName(userName);
                                                user.setStudentBranch(studentBranch);
                                                user.setUserContactNumber(userContactNumber);
                                                user.setStudentNativeState(studentNativeState);
                                                user.setNativeStateChanges(nativeStateChanges.intValue());
                                                user.setUserType(userType);
                                                user.setAvatar(avatar.intValue());
                                                user.setParentMailId(parentMailId);
                                                user.setStudentBlock(studentBlock);
                                                user.setRoomNo(roomNo);
                                                user.setBeds(beds.intValue());
                                                user.setAc(ac);
                                                user.setMess(mess);
                                                user.setHasPublicProfile(hasPublicProfile);
                                                user.setPublicBio(publicBio);
                                                user.setPublicColor(publicColor.intValue());
                                                user.setPrivateProfileId(privateProfileId);
                                                user.setFcmToken(fcmToken);

                                                localSqlDatabase = new LocalSqlDatabase(LoginActivity.this, LoginActivity.this);
                                                assert user != null;
                                                localSqlDatabase.addUser(user);

                                                roomMatesList = new ArrayList<>();

                                                roomMatesSection.document(User.getInstance().getStudentBlock())
                                                        .collection(Path.FILES.getPath())
                                                        .document(User.getInstance().getRoomNo())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                                    if (documentSnapshot.exists()) {
                                                                        final RoomTenants tenantMailList = documentSnapshot.toObject(RoomTenants.class);
                                                                        assert tenantMailList != null;
                                                                        localSqlDatabase.setTotalTenants(tenantMailList.getList().size() - 1);
                                                                        for (String t : tenantMailList.getList()) {
                                                                            if (t.equals(User.getInstance().getUserMailId()))
                                                                                continue;
                                                                            roomMatesList.add(t);
                                                                        }
                                                                        onRoomTenantListDownloaded.notifyCompleteListDownload();
                                                                    }
                                                                } else {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    callSnackBar("Room Not Found");
                                                                }
                                                            }
                                                        }).addOnFailureListener(LoginActivity.this::onFailure);
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                callSnackBar("User Record Not Found");
                                            }
                                        }
                                    }).addOnFailureListener(this::onFailure);

                        } else {
                            // prompt to verify mail
                            promptVerifyMail();
                        }
                    }
                })
                .addOnFailureListener(this::onFailure);
    }

    private void onFailure(Exception e) {
        progressBar.setVisibility(View.INVISIBLE);
        callSnackBar(e.getMessage());
    }

    private void promptVerifyMail() {
        progressBar.setVisibility(View.INVISIBLE);
        //Display an alert dialog for  not verifying the mail ID
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(
                LoginActivity.this);
        materialAlertDialogBuilder.setTitle("User Not Verified");
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
                                    .setMessage("Please check your V.I.T mail for verification, and then login again")
                                    .setPositiveButton("ok",null)
                                    .setCancelable(false)
                                    .show();
                        } else {
                            callSnackBar(Objects.requireNonNull(task1.getException()).getMessage());
                        }
                    });
        });
        materialAlertDialogBuilder.setNegativeButton("Ok", (dialog, which) -> {

        });
        materialAlertDialogBuilder.setCancelable(false);
        materialAlertDialogBuilder.show();
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
        tokenSection.document(User.getInstance().getUser_UID()).set(tokenMap).addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                progressBar.setVisibility(View.INVISIBLE);
                callSnackBar("Try logging in again after sometime");
                logoutUser();
            } else {
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
            if (!task.isSuccessful()) {
                progressBar.setVisibility(View.INVISIBLE);
                callSnackBar(Objects.requireNonNull(task.getException()).getMessage());
                logoutUser();
            } else {
                onFCMTokenGenerated.onTokenGenerated(task.getResult());
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            callSnackBar(e.getMessage());
            logoutUser();
        });
    }

    @Override
    public void notifyUserUpdated() {

    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        localSqlDatabase.deleteAllTenants();
        localSqlDatabase.deleteCurrentUser();
    }

    @Override
    public void notifyCompleteListDownload() {
        for (String mail : roomMatesList) {
            roomMatesDetailSection
                    .document(mail)
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
        roomMatesList.clear();
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
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


}