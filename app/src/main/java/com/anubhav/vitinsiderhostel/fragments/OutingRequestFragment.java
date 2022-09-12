package com.anubhav.vitinsiderhostel.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnAppErrorCreated;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.AppError;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OutingRequestFragment extends Fragment implements View.OnClickListener, iOnAppErrorCreated {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference feedbackSection = db.collection(Mod.FBK.toString());
    private final CollectionReference outingFormSection = db.collection(Mod.OUSEC.toString());

    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //views
    private View rootView;
    private TextInputEditText studentRegisterNumberEt, studentMailIdEt, studentContactNumberEt, studentRoomDetailsEt, parentContactNumberEt, visitLocationEt, visitPurposeEt, checkOutTimeEt, visitDateEt;
    private MaterialTextView userNameTxt;
    private ImageView locationPickerBtn;
    private ProgressBar progressBar;
    private MaterialButton applyBtn;
    private Dialog dialog;

    //objects
    private HashMap<String, Object> outingForm = new HashMap<>();

    //string objects
    private String userName, studentMailId, studentRegisterNumber, studentContactNumber, studentRoomNum, studentBlock, fdate = null;
    private int outHr;

    //listeners
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private iOnAppErrorCreated onAppErrorCreated;


    //flags
    private boolean flagOutTime = false;


    public OutingRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_outing_request, container, false);

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();


        dialog = new Dialog(getContext());
        userNameTxt = rootView.findViewById(R.id.oraUserNameTxt);
        studentRegisterNumberEt = rootView.findViewById(R.id.oraStudentRegisterNumberEt);
        studentMailIdEt = rootView.findViewById(R.id.oraStudentMailIdEt);
        studentRoomDetailsEt = rootView.findViewById(R.id.oraStudentRoomDetailEt);
        studentContactNumberEt = rootView.findViewById(R.id.oraStudentContactNumberEt);
        parentContactNumberEt = rootView.findViewById(R.id.oraStudentParentContactNumberEt);
        visitLocationEt = rootView.findViewById(R.id.oraVisitLocationEt);
        locationPickerBtn = rootView.findViewById(R.id.oraVisitLocationPickerBtn);
        visitPurposeEt = rootView.findViewById(R.id.oraVisitPurposeEt);
        visitDateEt = rootView.findViewById(R.id.oraVisitDateEt);
        checkOutTimeEt = rootView.findViewById(R.id.oraVisitCheckOutEt);
        progressBar = rootView.findViewById(R.id.oraProgressBar);
        applyBtn = rootView.findViewById(R.id.oraApplyButton);

        if (User.getInstance() != null) {
            userName = User.getInstance().getUserName();
            studentMailId = User.getInstance().getUserMailID();
            studentRegisterNumber = User.getInstance().getStudentRegisterNumber();
            studentContactNumber = User.getInstance().getUserContactNumber();
            studentRoomNum = User.getInstance().getRoomNo();
            studentBlock = User.getInstance().getStudentBlock();
        }

        String name = " ";
        String delimiter = "[^a-zA-Z]+";
        String[] splitPhrase = studentMailId.split("@")[0].split(delimiter);

        if (splitPhrase.length > 1) {
            name = firstCaps(splitPhrase[0]) + " " + firstCaps(splitPhrase[1]);
        } else {
            name = firstCaps(splitPhrase[0]);
        }

        userNameTxt.setText(name);
        studentMailIdEt.setText(studentMailId);
        studentRegisterNumberEt.setText(studentRegisterNumber);
        studentContactNumberEt.setText(studentContactNumber);
        final String roomDetail = studentRoomNum + "-" + studentBlock;
        studentRoomDetailsEt.setText(roomDetail);

        outingForm.put("userId", User.getInstance().getUser_Id());
        outingForm.put("studentMailId", studentMailId);
        outingForm.put("studentRegisterNumber", studentRegisterNumber);
        outingForm.put("studentContactNumber", studentContactNumber);
        outingForm.put("studentRoomDetails", roomDetail);
        outingForm.put("studentBlock", studentBlock);

        //listeners
        onAppErrorCreated = this;
        locationPickerBtn.setOnClickListener(this);
        visitDateEt.setOnClickListener(this);
        checkOutTimeEt.setOnClickListener(this);
        applyBtn.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.oraVisitLocationPickerBtn) {
            openGoogleMap();
        } else if (id == R.id.oraVisitDateEt) {
            processDateSelection();
        } else if (id == R.id.oraVisitCheckOutEt) {
            processCheckOutTimeSelection();
        } else if (id == R.id.oraApplyButton) {
            validateAll();
        }
    }

    private String firstCaps(String name) {
        char[] arr = name.toCharArray();
        arr[0] = String.valueOf(arr[0]).toUpperCase(Locale.ROOT).charAt(0);
        return String.valueOf(arr);
    }


    private void validateAll() {
        if (validateContactNumber(parentContactNumberEt)
                && validateVisitLocation(visitLocationEt) && validateVisitPurpose(visitPurposeEt)
                && validateDate(visitDateEt)
                && validateTime(checkOutTimeEt) && flagOutTime) {

            openTermsNCondition();

        }
    }

    private void openGoogleMap() {
        callSnackBar("Will be available from next app update");
    }

    //function to be performed when the select Date button is clicked
    private void processDateSelection() {

        Calendar calendar;

        DatePickerDialog datePickerDialog;

        calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH);
        int cDayInNum = calendar.get(Calendar.DAY_OF_MONTH);

        dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            final String date = dayOfMonth + "-" + month + "-" + year;
            visitDateEt.setText(date);
            setDate(date);
        };

        datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, cYear, cMonth, cDayInNum);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        long milis = getDatesBlocked().getTime();
        datePickerDialog.getDatePicker().setMinDate(milis);
        datePickerDialog.show();


    }

    private void setDate(String val) {
        fdate = val;
    }

    private void processCheckOutTimeSelection() {

        Calendar calendar;
        int hours;
        int min;
        TimePickerDialog timePickerDialog;

        calendar = Calendar.getInstance();
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        timeSetListener = (view, hourOfDay, minute) -> {
            outHr = hourOfDay;
            if (outHr >= 5 && outHr < 20) {
                checkOutTimeEt.setError(null);
                flagOutTime = true;
            } else {
                checkOutTimeEt.setError(" ");
                flagOutTime = false;
            }
            final String time = hourOfDay + ":" + minute;
            checkOutTimeEt.setText(time);
        };

        timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hours, min, true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();


    }

    private Date getDatesBlocked() {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.add(java.util.Calendar.DATE, 1);
        return tempCalendar.getTime();
    }

    private String generateCaptcha() {
        final int hr = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        final int min = java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE);
        Random r = new Random();
        char c1 = (char) (r.nextInt(25) + 'a');

        char c2 = (char) (r.nextInt(25) + 'a');
        return String.valueOf(hr) + c1 + min + c2;
    }

    private void openTermsNCondition() {

        dialog.setContentView(R.layout.outing_terms_n_condition);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView declare = dialog.findViewById(R.id.outingTnCAcceptTxt);
        MaterialTextView captchaTxt = dialog.findViewById(R.id.outingTnCCaptchaTxt);
        TextInputEditText captchaInputEt = dialog.findViewById(R.id.outingTnCInputCaptchaEt);
        MaterialButton proceedBtn = dialog.findViewById(R.id.outingTnCProceedButton);

        proceedBtn.setEnabled(false);

        final String genCaptcha = generateCaptcha();
        captchaTxt.setText(genCaptcha);

        captchaInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(captchaInputEt.getText()).toString().equals(genCaptcha)) {
                    proceedBtn.setEnabled(true);
                    captchaInputEt.setTextColor(Color.parseColor("#FFFF4444"));
                    declare.setVisibility(View.VISIBLE);
                } else {
                    proceedBtn.setEnabled(false);
                    captchaInputEt.setTextColor(Color.parseColor("#E6626161"));
                    declare.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        proceedBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.GONE);
            initiateUploadingToAllBucket();
        });
        dialog.show();
    }

    private void initiateUploadingToAllBucket() {
        dialog.dismiss();
        outingForm.put("timestamp", new Timestamp(new Date()));
        checkForDuplicateNApply(Objects.requireNonNull(outingForm.get("visitDate")).toString());
    }

    private void checkForDuplicateNApply(String visitDate) {
        outingFormSection.
                document(Mod.OFORM.toString()).collection(Mod.DET.toString()).whereEqualTo("studentMailId", studentMailId).whereEqualTo("visitDate", visitDate).get().addOnCompleteListener(task2 -> {
            if (task2.getResult().isEmpty()) {

                DocumentReference formDoc = outingFormSection.
                        document(Mod.OSTACK.toString()).collection(Mod.DET.toString())
                        .document();
                outingForm.put("docID", formDoc.getId());

                outingFormSection.
                        document(Mod.OSTACK.toString()).collection(Mod.DET.toString())
                        .document(formDoc.getId()).set(outingForm).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AlertDisplay alertDisplay = new AlertDisplay("Application Submitted", "Outing application has been submitted, track your application status in Outing Status Section.", getContext());
                        alertDisplay.displayAlert();
                    }
                    progressBar.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    callSnackBar(e.getMessage());
                });

            } else {
                progressBar.setVisibility(View.GONE);
                AlertDisplay alertDisplay = new AlertDisplay("Application Already Exist!", "Outing Application on this day is already present.Cannot submit new application on this day again.", getContext());
                alertDisplay.displayAlert();
            }
        });
    }

    private boolean validateContactNumber(TextInputEditText editText) {
        final String number = Objects.requireNonNull(editText.getText()).toString().trim();

        if (TextUtils.isEmpty(number)) {
            editText.setError("Contact number is required");
            editText.requestFocus();
            return false;
        }

        final String pattern = "[+][0-9]{11,14}";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(number);
        if (!m.matches()) {
            editText.setError("Invalid or Incorrect Number");
            editText.requestFocus();
            return false;
        }
        outingForm.put("parentNumber", number);
        return true;
    }

    private boolean validateVisitLocation(TextInputEditText editText) {
        final String location = Objects.requireNonNull(editText.getText()).toString().trim();
        if (TextUtils.isEmpty(location)) {
            editText.setError("Location is required");
            editText.requestFocus();
            return false;
        }

        if (location.length() < 12) {
            editText.setError("Too short,try bit more descriptive");
            editText.requestFocus();
            return false;
        }

        outingForm.put("visitLocation", location);
        return true;
    }

    private boolean validateVisitPurpose(TextInputEditText editText) {
        final String purpose = Objects.requireNonNull(editText.getText()).toString().trim();
        if (TextUtils.isEmpty(purpose)) {
            editText.setError("Location is required");
            editText.requestFocus();
            return false;
        }

        if (purpose.length() < 50) {
            editText.setError("Too short description, minimum 50 characters");
            editText.requestFocus();
            return false;
        }

        outingForm.put("visitPurpose", purpose);
        return true;
    }

    private boolean validateTime(TextInputEditText editText) {
        final String time = Objects.requireNonNull(editText.getText()).toString().trim();
        if (TextUtils.isEmpty(time)) {
            editText.setError("Pick a time");
            editText.requestFocus();
            return false;
        }

        outingForm.put("checkOut", time);

        editText.setError(null);
        return true;
    }

    private boolean validateDate(TextInputEditText editText) {
        final String date = Objects.requireNonNull(editText.getText()).toString().trim();
        if (TextUtils.isEmpty(date)) {
            editText.setError("Pick a date");
            editText.requestFocus();
            return false;
        }
        editText.setError(null);


        outingForm.put("visitDate", date);
        return true;
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
                .make(requireContext(), rootView.findViewById(R.id.outingRequestFragment), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navy_blue));
        snackbar.show();
    }


    //process 0 and process 1 functions
    @Override
    public void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


}



