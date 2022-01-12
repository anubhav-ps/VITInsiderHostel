package com.anubhav.vitinsiderhostel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    // auto complete text view drop down entries
    private final String[] blockOptions = {" ", "A", "B", "C"};
    private final String[] userTypeOptions = {" ", "STUDENT", "FACULTY"};

    // auto complete text view declaration
    private AutoCompleteTextView blockTextView;
    private AutoCompleteTextView userTypeTextView;

    // input field views
    private TextInputEditText nameEt, mailEt, passwordEt;
    private ProgressBar progressBar;

    // input field string values
    private String inputName;
    private String inputMail;
    private String inputPassword;
    private String inputUserType = " ";
    private String inputBlock = " ";

    //input flags
    private boolean validMail = false;
    private boolean validName = false;
    private boolean validPassword = false;
    private boolean validUserType = false;
    private boolean validBlock = false;

    //mail domain pattern
    private final String studentMailPattern = "@vitstudent.ac.in";
    private final String facultyMailPattern = "@vit.ac.in";


    //firebase authentication declarations
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userSection = db.collection("Users");
    private final CollectionReference hostelersSection = db.collection("Hostelers");
    private final CollectionReference roomStructuresSection = db.collection("RoomStructure");
    private final CollectionReference reports = db.collection("Reports");

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

        TextInputLayout forBlock = findViewById(R.id.registerPgeBlockTxtLayout);
        MaterialButton createAccount = findViewById(R.id.registerPgeRegisterBtn);
        ImageButton toLogin = findViewById(R.id.registerPgeArrowBtn);
        progressBar = findViewById(R.id.registerPgeProgressBar);


        // student block declaration
        blockTextView = findViewById(R.id.registerPgeBlockTxt);
        ArrayAdapter<String> blockAdapter = new ArrayAdapter<>(this, R.layout.drop_down_option, blockOptions);
        blockTextView.setAdapter(blockAdapter);

        // user type declaration
        userTypeTextView = findViewById(R.id.registerPgeUserTypeTxt);
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(this, R.layout.drop_down_option, userTypeOptions);
        userTypeTextView.setAdapter(userTypeAdapter);

        // drop down menu click listener
        blockTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            String val = adapterView.getItemAtPosition(i).toString();
            switch (val) {
                case " ":
                    blockTextView.setError("Select Hostel Block");
                    validBlock = false;
                    return;
                case "A":
                    blockTextView.setError(null);
                    inputBlock = "A";
                    break;
                case "B":
                    blockTextView.setError(null);
                    inputBlock = "B";
                    break;
                case "C":
                    blockTextView.setError(null);
                    inputBlock = "C";
                    break;
            }
            validBlock = true;
        });

        userTypeTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            String val = adapterView.getItemAtPosition(i).toString();
            switch (val) {
                case " ":
                    forBlock.setVisibility(View.INVISIBLE);
                    blockTextView.setText(blockTextView.getAdapter().getItem(0).toString(), false);
                    validBlock = false;
                    inputBlock = "-";
                    userTypeTextView.setError("Select User Type");
                    validUserType = false;
                    validMail = false;
                    return;
                case "STUDENT":
                    forBlock.setVisibility(View.VISIBLE);
                    blockTextView.setText(blockTextView.getAdapter().getItem(0).toString(), false);
                    validBlock = false;
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
                    inputUserType = "S";
                    break;
                case "FACULTY":
                    forBlock.setVisibility(View.INVISIBLE);
                    blockTextView.setText(blockTextView.getAdapter().getItem(0).toString(), false);
                    inputBlock = "-";
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
                    inputUserType = "F";
                    break;
            }
            validUserType = true;

        });

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
                } else if (inputUserType.equalsIgnoreCase("S") && !inputMail.endsWith(studentMailPattern)) {
                    validMail = false;
                } else if (inputUserType.equalsIgnoreCase("F") && !inputMail.endsWith(facultyMailPattern)) {
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
            validation();
        } else if (id == R.id.registerPgeArrowBtn) {
            toLoginPage();
        }
    }


    // validation
    private void validation() {

        if (!validName && !validMail && !validPassword && !validBlock && !validUserType) {
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

        if (!validUserType) {
            userTypeTextView.setError("Select user type");
            return;
        }

        if (!validBlock) {
            blockTextView.setError("Select your block");
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
        if (inputUserType.equalsIgnoreCase("F")) {
            User user = User.getInstance();
            user.setUserName(inputName);
            user.setUserMailID(inputMail);
            user.setUserType(inputUserType);
            user.setStudentBlock(inputBlock);

        } else if (inputUserType.equalsIgnoreCase("S")) {

            hostelersSection
                    .document(inputBlock)
                    .collection("Hostelers")
                    .document(inputMail.toLowerCase(Locale.ROOT))
                    .get().addOnCompleteListener(task -> {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {

                    //register with mailID and password
                    //first onComplete listener
                    firebaseAuth
                            .createUserWithEmailAndPassword(inputMail, inputPassword)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {

                                    currentUser = firebaseAuth.getCurrentUser();
                                    assert currentUser != null;
                                    final String currentUserID = currentUser.getUid();
                                    final String roomNo = snapshot.getString("roomNo");

                                    assert roomNo != null;
                                    DocumentReference roomDocument = roomStructuresSection.document(inputBlock).collection("Rooms").document(roomNo);

                                    roomDocument
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {

                                                    final String acOrNonAc = Objects.requireNonNull(documentSnapshot.get("type")).toString();
                                                    final String beds = Objects.requireNonNull(documentSnapshot.get("bed")).toString();
                                                    final String type = beds + "|" + acOrNonAc;
                                                    //linking the username and user contact number with the respective userID using a HashMap named userObject
                                                    Map<String, String> userMap = new HashMap<>();

                                                    userMap.put("user_Id", currentUserID);
                                                    userMap.put("userName", inputName);
                                                    userMap.put("userMailID", inputMail.toLowerCase(Locale.ROOT));
                                                    userMap.put("userType", "S");
                                                    userMap.put("studentBlock", inputBlock);

                                                    userMap.put("studentBranch", "N/A");
                                                    userMap.put("studentNativeLanguage", "N/A");

                                                    userMap.put("userContactNumber", "N/A");

                                                    userMap.put("roomNo", roomNo);
                                                    userMap.put("roomType", type);
                                                    userMap.put("isAdmin","0");

                                                    // getting document id and uploading to fire store
                                                    DocumentReference documentReference = userSection.document("S").collection(inputBlock).document();
                                                    final String dID = documentReference.getId();
                                                    userMap.put("doc_Id", dID);

                                                    documentReference.set(userMap)
                                                            .addOnCompleteListener(task11 -> {
                                                                if (task11.isSuccessful()) {

                                                                    DocumentReference document = userSection.document("S").collection(inputBlock).document(dID);
                                                                    document
                                                                            .get()
                                                                            .addOnSuccessListener(documentSnapshot1 -> {
                                                                                Map<String, String> userBlock = new HashMap<>();
                                                                                userBlock.put("block", inputBlock);
                                                                                userBlock.put("userType",inputUserType);
                                                                                userBlock.put("doc_id",dID);
                                                                                db.collection("UserBlockRec").document(inputMail.toLowerCase(Locale.ROOT)).set(userBlock);
                                                                                if (documentSnapshot1.exists()) {

                                                                                    final String userID = Objects.requireNonNull(documentSnapshot1.get("user_Id")).toString();
                                                                                    final String userDocId = Objects.requireNonNull(documentSnapshot1.get("doc_Id")).toString();

                                                                                    final String userName = Objects.requireNonNull(documentSnapshot1.get("userName")).toString();
                                                                                    final String userMail = Objects.requireNonNull(documentSnapshot1.get("userMailID")).toString();

                                                                                    final String userContactNum = Objects.requireNonNull(documentSnapshot1.get("userContactNumber")).toString();

                                                                                    final String userType = Objects.requireNonNull(documentSnapshot1.get("userType")).toString();
                                                                                    final String studentBlock = Objects.requireNonNull(documentSnapshot1.get("studentBlock")).toString();

                                                                                    final String studentBranch = Objects.requireNonNull(documentSnapshot1.get("studentBranch")).toString();
                                                                                    final String studentNativeLanguage = Objects.requireNonNull(documentSnapshot1.get("studentNativeLanguage")).toString();
                                                                                    final String studentRoomNo = Objects.requireNonNull(documentSnapshot1.get("roomNo")).toString();
                                                                                    final String studentRoomType = Objects.requireNonNull(documentSnapshot1.get("roomType")).toString();
                                                                                    final String admin = Objects.requireNonNull(documentSnapshot1.get("isAdmin")).toString();

                                                                                    User user = User.getInstance();
                                                                                    user.setUser_Id(userID);
                                                                                    user.setDoc_Id(userDocId);
                                                                                    user.setUserName(userName);
                                                                                    user.setUserMailID(userMail);
                                                                                    user.setUserContactNumber(userContactNum);
                                                                                    user.setUserType(userType);
                                                                                    user.setStudentBlock(studentBlock);
                                                                                    user.setStudentBranch(studentBranch);
                                                                                    user.setStudentNativeLanguage(studentNativeLanguage);
                                                                                    user.setRoomNo(studentRoomNo);
                                                                                    user.setRoomType(studentRoomType);
                                                                                    boolean adminVal = false;
                                                                                    if (admin.equalsIgnoreCase("1")){
                                                                                        adminVal = true;
                                                                                    }
                                                                                    user.setAdmin(adminVal);

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
                                                                    callSnackBar(Objects.requireNonNull(task11.getException()).getMessage());
                                                                }
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                callSnackBar(e.getMessage());
                                                            });

                                                } else {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    callSnackBar("00RD");
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                callSnackBar(e.getMessage());
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
                                            callSnackBar(Objects.requireNonNull(task112.getException()).getMessage());
                                        }
                                    });

                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    callSnackBar(Objects.requireNonNull(task1.getException()).getMessage());
                                }
                            });

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RegisterActivity.this);
                    builder.setTitle("User Not Found");
                    builder.setMessage("User record corresponding to your input is not present.");
                    builder.setPositiveButton("But I'm Hosteler", (dialogInterface, i) -> Toast.makeText(RegisterActivity.this, "Complaint has been raised regarding this issue", Toast.LENGTH_SHORT).show());
                    builder.setNegativeButton("Back", (dialogInterface, i) -> {

                    });
                    builder.show();
                }  // in case user  record doesn't match with the hostlers section

            });

        }
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
        snackBarView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this,R.color.navy_blue));
        snackbar.show();
    }

    // go to login page
    private void toLoginPage() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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

}


