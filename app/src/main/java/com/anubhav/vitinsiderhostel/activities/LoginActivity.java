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
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.enums.NotifyStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, iOnNotifyDbProcess {

    final List<Tenant> tenantList = new ArrayList<>();
    private final String studentMailPattern = "@vitstudent.ac.in";
    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userDetailsSection = db.collection(Mod.USD.toString());
    private final CollectionReference hostelDetailsSection = db.collection(Mod.HOD.toString());
    //firebase declarations
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;
    private LocalSqlDatabase sqlDatabase;
    private String inputMail;
    private String inputPassword;
    private boolean validMail = false;
    private boolean validPassword = false;
    private TextInputEditText mailEt, passwordEt;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mailEt = findViewById(R.id.loginPgeMailTxt);
        passwordEt = findViewById(R.id.loginPgePasswordTxt);

        MaterialButton login = findViewById(R.id.loginPgeLoginBtn);
        ImageButton toRegister = findViewById(R.id.loginPgeArrowBtn);
        progressBar = findViewById(R.id.loginPgeProgressBar);
        MaterialTextView reset = findViewById(R.id.loginPgeReset);
        //firebase auth instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> firebaseUser = firebaseAuth.getCurrentUser();

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
            validation();
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


    private void validation() {

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

                                                    final String beds = studentRoomType.split("\\|")[0].trim();
                                                    int tNum = Integer.parseInt(beds);

                                                    //Toast.makeText(LoginActivity.this,tNum , Toast.LENGTH_SHORT).show();

                                                    sqlDatabase = new LocalSqlDatabase(LoginActivity.this, tNum, LoginActivity.this);

                                                    //todo insert user to database
                                                    boolean userResult = sqlDatabase.addUser(user);

                                                    if (!userResult) {
                                                        //todo : Report user list download
                                                    }


                                                    String scrambleValue = "";
                                                    try {
                                                        scrambleValue = Scramble.getScramble(studentRoomNo);
                                                    } catch (NoSuchAlgorithmException e) {
                                                        e.printStackTrace();
                                                    }

                                                    hostelDetailsSection
                                                            .document(Mod.HOT.toString())
                                                            .collection(Mod.getBlock(studentBlock))
                                                            .document(scrambleValue)
                                                            .get()
                                                            .addOnCompleteListener(
                                                                    new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                                if (documentSnapshot.exists()) {
                                                                                    final String tenantsRaw = Objects.requireNonNull(documentSnapshot.get("list")).toString();
                                                                                    final String[] tenantMailList = tenantsRaw.split("%");
                                                                                    for (String mId : tenantMailList) {
                                                                                        Tenant tenant = new Tenant(mId);
                                                                                        tenantList.add(tenant);
                                                                                        sqlDatabase.addTenant(tenant);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
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
                );

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


    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(LoginActivity.this, findViewById(R.id.loginPge), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.navy_blue));
        snackbar.show();
    }

    @Override
    public void onNotifyCompleteDataDownload(NotifyStatus notifyStatus) {
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LocalSqlDatabase.executors != null) {
            LocalSqlDatabase.stopExecutors();
        }
    }

    @Override
    public void onNotifyCompleteListDownload() throws NoSuchAlgorithmException {
        LocalSqlDatabase.tenantCollectionSize = tenantList.size();
        for (Tenant tenant : tenantList) {
            hostelDetailsSection
                    .document(Mod.TED.toString())
                    .collection(Mod.DET.toString())
                    .document(Scramble.getScramble(tenant.getTenantMailID()))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    Tenant updatedTenant = documentSnapshot.toObject(Tenant.class);
                                    sqlDatabase.updateTenant(updatedTenant);
                                } else {
                                    sqlDatabase.updateTenant(tenant);
                                }
                            }
                        }
                    });
        }
    }


}