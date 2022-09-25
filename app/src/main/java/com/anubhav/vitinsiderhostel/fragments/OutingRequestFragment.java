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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.Path;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingDuplicateChecked;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.Timings;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;


public class OutingRequestFragment extends Fragment implements View.OnClickListener, iOnNotifyDbProcess, iOnOutingDuplicateChecked {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference outingFormSection = db.collection(Path.OUTING_BASE.getPath());

    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //local database
    private LocalSqlDatabase localSqlDatabase;

    //views
    private View rootView;
    private TextInputEditText studentNameEt, parentMailIdEt, studentRegisterNumberEt, studentMailIdEt, studentContactNumberEt, studentRoomDetailsEt, visitLocationEt, visitDateEt, checkOutTimeEt, checkInTimeEt, visitDescriptionEt;
    private MaterialTextView userNameTxt;
    private AutoCompleteTextView visitPurposeDropDown;
    private ProgressBar progressBar;
    private MaterialButton applyBtn;
    private Dialog dialog;
    private Date currentTimeStamp;
    private Date submissionTimeStamp;

    //objects
    private HashMap<String, Object> outingForm = new HashMap<>();

    //string objects
    private String studentName, parentMailId, studentMailId, studentRegisterNumber, studentContactNumber, studentRoomNum, studentBlock;
    private String selectedVisitPurpose = "SELECT", selectedCheckIn, selectedCheckOut, selectedDay;
    private Date visitTimeStamp = null, checkInTimeStamp = null, checkOutTimeStamp = null;
    private String[] visitPurposesList;


    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private iOnOutingDuplicateChecked onOutingDuplicateChecked;


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

        localSqlDatabase = new LocalSqlDatabase(getContext(), this);

        dialog = new Dialog(getContext());

        userNameTxt = rootView.findViewById(R.id.oraUserNameTxt);
        studentNameEt = rootView.findViewById(R.id.oraStudentNameEt);
        studentRegisterNumberEt = rootView.findViewById(R.id.oraStudentRegisterNumberEt);
        studentMailIdEt = rootView.findViewById(R.id.oraStudentMailIdEt);
        studentRoomDetailsEt = rootView.findViewById(R.id.oraStudentRoomDetailEt);
        studentContactNumberEt = rootView.findViewById(R.id.oraStudentContactNumberEt);
        parentMailIdEt = rootView.findViewById(R.id.oraStudentParentMailIdEt);
        visitLocationEt = rootView.findViewById(R.id.oraVisitLocationEt);
        visitPurposeDropDown = rootView.findViewById(R.id.oraVisitPurposeEt);
        visitDescriptionEt = rootView.findViewById(R.id.oraVisitDescriptionEt);
        checkInTimeEt = rootView.findViewById(R.id.oraVisitCheckInEt);
        checkOutTimeEt = rootView.findViewById(R.id.oraVisitCheckOutEt);
        visitDateEt = rootView.findViewById(R.id.oraVisitDateEt);
        progressBar = rootView.findViewById(R.id.oraProgressBar);
        applyBtn = rootView.findViewById(R.id.oraApplyButton);

        if (User.getInstance() != null) {
            studentMailId = User.getInstance().getUserMailId();
            studentRegisterNumber = User.getInstance().getStudentRegisterNumber();
            studentContactNumber = User.getInstance().getUserContactNumber();
            studentRoomNum = User.getInstance().getRoomNo();
            studentBlock = User.getInstance().getStudentBlock();
            studentName = User.getInstance().getStudentName();
            parentMailId = User.getInstance().getParentMailId();
        }


        studentNameEt.setText(studentName);
        studentMailIdEt.setText(studentMailId);
        studentRegisterNumberEt.setText(studentRegisterNumber);
        studentContactNumberEt.setText(studentContactNumber);
        parentMailIdEt.setText(parentMailId);
        final String roomDetail = studentRoomNum + "-" + studentBlock;
        studentRoomDetailsEt.setText(roomDetail);

        outingForm.put("user_UID", User.getInstance().getUser_UID());
        outingForm.put("studentName", studentName);
        outingForm.put("studentMailId", studentMailId);
        outingForm.put("studentRegisterNumber", studentRegisterNumber);
        outingForm.put("studentContactNumber", studentContactNumber);
        outingForm.put("hostelRoomNumber", studentRoomNum);
        outingForm.put("hostelBlock", studentBlock);
        outingForm.put("parentMailId", parentMailId);


        onOutingDuplicateChecked = this;

        visitPurposeDropDown.setOnItemClickListener((parent, view, position, id) -> callPurposeChange(position));


        visitDateEt.setOnClickListener(this);
        checkOutTimeEt.setOnClickListener(this);
        checkInTimeEt.setOnClickListener(this);
        applyBtn.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.oraVisitCheckOutEt) {

            processCheckOutTimeSelection();


        } else if (id == R.id.oraVisitCheckInEt) {

            processCheckInTimeSelection();

        } else if (id == R.id.oraVisitDateEt) {

            processDateSelection();

        } else if (id == R.id.oraApplyButton) {
            if (validateAll()) {
                checkForDuplicateNApply(visitTimeStamp);
            }
        }
    }

    private void processCheckOutTimeSelection() {

        Calendar calendar;
        int hours;
        int min;

        calendar = Calendar.getInstance();
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);


        Date minCheckOutTime = Timings.getMinCheckOutTime(visitTimeStamp);
        Date maxCheckOutTime = Timings.getMaxCheckOutTime(visitTimeStamp);

        timeSetListener = (view, hourOfDay, minute) -> {
            final String checkOut = " " + hourOfDay + ":" + minute + ":00 ";
            checkOutTimeStamp = Timings.parseTimeToDate(visitTimeStamp, checkOut);
            assert checkOutTimeStamp != null;
            if (checkOutTimeStamp.after(maxCheckOutTime) || checkOutTimeStamp.before(minCheckOutTime)) {
                callSnackBar("Exceeding the maximum or minimum limit for Check Out");
                checkOutTimeEt.setError(" ");
                checkOutTimeEt.requestFocus();
            } else {
                checkOutTimeEt.setError(null);
            }
            selectedCheckOut = Timings.formatToTimeString(checkOutTimeStamp);
            checkOutTimeEt.setText(selectedCheckOut);
        };

        timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hours, min, true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();


    }

    private void processCheckInTimeSelection() {

        Calendar calendar;
        int hours;
        int min;

        calendar = Calendar.getInstance();
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);


        Date minCheckInTime = Timings.getMinCheckInTime(visitTimeStamp);
        Date maxCheckInTime = Timings.getMaxCheckInTime(visitTimeStamp);

        timeSetListener = (view, hourOfDay, minute) -> {
            final String checkIn = " " + hourOfDay + ":" + minute + ":00 ";
            checkInTimeStamp = Timings.parseTimeToDate(visitTimeStamp, checkIn);
            assert checkInTimeStamp != null;
            if (checkInTimeStamp.after(maxCheckInTime) || checkInTimeStamp.before(minCheckInTime)) {
                callSnackBar("Exceeding the maximum or minimum limit for Check In");
                checkInTimeEt.setError(" ");
                checkInTimeEt.requestFocus();
            } else {
                checkInTimeEt.setError(null);
            }
            selectedCheckIn = Timings.formatToTimeString(checkInTimeStamp);
            checkInTimeEt.setText(selectedCheckIn);
        };

        timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hours, min, true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();


    }


    private void processDateSelection() {

        int blockDays = 1;
        if (currentTimeStamp.after(submissionTimeStamp)) {
            blockDays = 2;
        }


        Calendar calendar;


        calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH);
        int cDayInNum = calendar.get(Calendar.DAY_OF_MONTH);

        final String[] months = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

        //listeners
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String dateString = dayOfMonth + " " + months[month - 1] + " " + year;
            visitTimeStamp = Timings.getVisitDateTimeStamp(dateString);
            selectedDay = Timings.formatToDateString(visitTimeStamp);
            visitDateEt.setText(selectedDay);
        };

        datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, cYear, cMonth, cDayInNum);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        long milis = getDatesBlocked(blockDays).getTime();
        datePickerDialog.getDatePicker().setMinDate(milis);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.show();

    }

    private Date getDatesBlocked(int val) {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.add(java.util.Calendar.DATE, val);
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
            uploadToCloud();
        });

        dialog.show();
    }

    private void uploadToCloud() {
        dialog.dismiss();
        DocumentReference formDoc = outingFormSection.
                document(Path.CLIENT_OUTING_Q.getPath())
                .collection(Path.FILES.getPath())
                .document();

        outingForm.put("visitDateStr", getDate(visitTimeStamp));
        outingForm.put("checkOutStr", getTime(checkOutTimeStamp));
        outingForm.put("checkInStr", getTime(checkInTimeStamp));
        outingForm.put("timeStamp", new Timestamp(new Date()));
        outingForm.put("docId", formDoc.getId());

        outingFormSection.
                document(Path.CLIENT_OUTING_Q.getPath())
                .collection(Path.FILES.getPath())
                .document(formDoc.getId())
                .set(outingForm).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AlertDisplay alertDisplay = new AlertDisplay("Application Submitted", "Outing application has been submitted, track your application status in Outing Status Section.", getContext());
                        alertDisplay.displayAlert();
                    }
                    progressBar.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    callSnackBar(e.getMessage());
                });
    }

    private String getDate(Date date) {
        SimpleDateFormat getDate = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
        return getDate.format(date);
    }

    private String getTime(Date date) {
        SimpleDateFormat getDate = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return getDate.format(date);
    }

    private void checkForDuplicateNApply(Date visitTimeStamp) {

        outingFormSection.
                document(Path.OUTING_FORM.getPath())
                .collection(Path.FILES.getPath())
                .whereEqualTo("studentMailId", studentMailId)
                .whereEqualTo("visitDateStr", getDate(visitTimeStamp))
                .get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        if (task2.getResult().isEmpty()) {
                            onOutingDuplicateChecked.outingIsNew();
                        } else {
                            onOutingDuplicateChecked.outingExist();
                        }
                    }

                });


    }

    private void callPurposeChange(int position) {
        String purpose = visitPurposesList[position];
        if (purpose.equalsIgnoreCase("SELECT")) {
            selectedVisitPurpose = purpose;
            visitPurposeDropDown.setError("Please enter your purpose of visit");
            visitPurposeDropDown.requestFocus();
        } else {
            visitPurposeDropDown.setError(null);
            selectedVisitPurpose = purpose;
            Toast.makeText(getContext(), "Visit Purpose is " + purpose, Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validateAll() {
        return validateVisitLocation(visitLocationEt) && validateVisitPurpose(visitPurposeDropDown) && validateVisitDescription(visitDescriptionEt) && validateVisitDay() && validateTime();
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

    private boolean validateVisitPurpose(AutoCompleteTextView visitPurposeDropDown) {
        if (selectedVisitPurpose.equalsIgnoreCase("SELECT")) {
            visitPurposeDropDown.setError("Please enter your purpose of visit");
            visitPurposeDropDown.requestFocus();
            return false;
        }
        outingForm.put("visitPurpose", selectedVisitPurpose);
        visitPurposeDropDown.setError(null);
        return true;
    }

    private boolean validateVisitDescription(TextInputEditText editText) {
        final String purpose = Objects.requireNonNull(editText.getText()).toString().trim();
        if (TextUtils.isEmpty(purpose)) {
            editText.setError("Description is required");
            editText.requestFocus();
            return false;
        }

        if (purpose.length() < 50) {
            editText.setError("Too short description, minimum 50 characters");
            editText.requestFocus();
            return false;
        }

        outingForm.put("visitDescription", purpose);
        return true;
    }

    private boolean validateVisitDay() {

        if (visitTimeStamp == null) {
            callSnackBar("Select visit day");
            return false;
        }
        outingForm.put("visitDate", visitTimeStamp);
        return true;
    }

    private boolean validateTime() {


        if (checkOutTimeStamp == null || checkInTimeStamp == null) {
            callSnackBar("Select timings");
            return false;
        }

        System.out.println(checkOutTimeStamp);

        System.out.println(checkInTimeStamp);

        if (checkOutTimeStamp.after(checkInTimeStamp)) {
            callSnackBar("Check Out time cannot be after Check In time");
            return false;
        }

        if (checkOutTimeStamp.compareTo(checkInTimeStamp) == 0) {
            callSnackBar("Check Out time cannot be same as Check In time");
            return false;
        }

        long difference = checkInTimeStamp.getTime() - checkOutTimeStamp.getTime();
        long hrs = difference / (60 * 60 * 1000);


        if (hrs > 4) {
            callSnackBar("Your Outing time is exceeding the 4 hrs limit");
            return false;
        }


        outingForm.put("checkIn", checkInTimeStamp);
        outingForm.put("checkOut", checkOutTimeStamp);
        return true;
    }

    @Override
    public void outingExist() {
        progressBar.setVisibility(View.GONE);
        AlertDisplay alertDisplay = new AlertDisplay("Application Already Exist!", "Outing Application on this day is already present.Cannot submit new application for the same day again.", getContext());
        alertDisplay.displayAlert();
    }

    @Override
    public void outingIsNew() {
        openTermsNCondition();
    }

    @Override
    public void notifyCompleteDataDownload() {

    }

    @Override
    public void notifyUserUpdated() {

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
        if (localSqlDatabase == null) {
            localSqlDatabase = new LocalSqlDatabase(getContext(), this);
        }
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

    @Override
    public void onResume() {
        super.onResume();

        Date today = new Date();  // current timestamp ir respective of country timezone;

        currentTimeStamp = Timings.toIST_TimeStamp(today);  // current timestamp with respect to Indian Timezone

        final String submissionTime = " 20:00:00 ";              // submission deadline
        submissionTimeStamp = Timings.toIST_TimeStamp(Timings.parseTimeToDate(today, submissionTime));  //submission deadline with respect to Indian Timezone


        Calendar cal = Calendar.getInstance();
        if (currentTimeStamp.after(submissionTimeStamp) || currentTimeStamp.compareTo(submissionTimeStamp) == 0) {
            cal.add(Calendar.DAY_OF_MONTH, 2);
        } else {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        visitTimeStamp = cal.getTime();
        visitDateEt.setText(Timings.formatToDateString(visitTimeStamp));


        visitPurposesList = getResources().getStringArray(R.array.visit_purpose);
        ArrayAdapter<String> visitPurposesAdapter = new ArrayAdapter<>(requireContext(), R.layout.visit_purpose_drop_down_item, visitPurposesList);
        visitPurposeDropDown.setAdapter(visitPurposesAdapter);
        visitPurposeDropDown.setSelection(0);


    }


}



