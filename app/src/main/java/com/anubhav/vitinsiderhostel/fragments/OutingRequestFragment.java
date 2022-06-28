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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.ORAStatus;
import com.anubhav.vitinsiderhostel.models.LinkEnds;
import com.anubhav.vitinsiderhostel.models.ORAStudentLink;
import com.anubhav.vitinsiderhostel.models.ORApp;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OutingRequestFragment extends Fragment implements View.OnClickListener {


    TextInputEditText studentNameEt, studentRegisterNumberEt, studentMailIdEt, studentContactNumberEt, studentRoomDetailsEt, parentContactNumberEt, proctorContactNumberEt, visitLocationEt, visitPurposeEt, checkInTimeEt, checkOutTimeEt, visitDateEt;
    MaterialTextView userNameTxt;
    ImageView locationPickerBtn;
    ProgressBar progressBar;
    MaterialButton applyBtn;
    LinkEnds coupleEnds = new LinkEnds();
    ORApp oraUpload = new ORApp();
    private String userName, studentMailId, studentContactNumber, studentRoomNum, studentBlock;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private int outHr, outMin, inHr, inMin;
    private boolean flagInTime = false, flagOutTime = false;
    private String fdate = null;
    private Dialog dialog;


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
        View rootView = inflater.inflate(R.layout.fragment_outing_request, container, false);

        dialog = new Dialog(getContext());
        userNameTxt = rootView.findViewById(R.id.oraUserNameTxt);
        studentNameEt = rootView.findViewById(R.id.oraStudentNameEt);
        studentRegisterNumberEt = rootView.findViewById(R.id.oraStudentRegisterNumberEt);
        studentMailIdEt = rootView.findViewById(R.id.oraStudentMailIdEt);
        studentRoomDetailsEt = rootView.findViewById(R.id.oraStudentRoomDetailEt);
        studentContactNumberEt = rootView.findViewById(R.id.oraStudentContactNumberEt);
        parentContactNumberEt = rootView.findViewById(R.id.oraStudentParentContactNumberEt);
        proctorContactNumberEt = rootView.findViewById(R.id.oraStudentProctorContactNumberEt);

        visitLocationEt = rootView.findViewById(R.id.oraVisitLocationEt);
        locationPickerBtn = rootView.findViewById(R.id.oraVisitLocationPickerBtn);
        visitPurposeEt = rootView.findViewById(R.id.oraVisitPurposeEt);
        visitDateEt = rootView.findViewById(R.id.oraVisitDateEt);
        checkOutTimeEt = rootView.findViewById(R.id.oraVisitCheckOutEt);
        checkInTimeEt = rootView.findViewById(R.id.oraVisitCheckInEt);

        progressBar = rootView.findViewById(R.id.oraProgressBar);
        applyBtn = rootView.findViewById(R.id.oraApplyButton);

        if (User.getInstance() != null) {
            userName = User.getInstance().getUserName();
            studentMailId = User.getInstance().getUserMailID();
            studentContactNumber = User.getInstance().getUserContactNumber();
            studentRoomNum = User.getInstance().getRoomNo();
            studentBlock = User.getInstance().getStudentBlock();
        }

        userNameTxt.setText(userName);
        studentMailIdEt.setText(studentMailId);
        studentContactNumberEt.setText(studentContactNumber);
        final String roomDetail = studentRoomNum + "-" + studentBlock;
        studentRoomDetailsEt.setText(roomDetail);

        oraUpload.setStudentMailId(studentMailId);
        oraUpload.setStudentContactNumber(studentContactNumber);
        oraUpload.setStudentRoomDetails(roomDetail);


        locationPickerBtn.setOnClickListener(this);
        visitDateEt.setOnClickListener(this);
        checkOutTimeEt.setOnClickListener(this);
        checkInTimeEt.setOnClickListener(this);
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
        } else if (id == R.id.oraVisitCheckInEt) {
            processCheckInTimeSelection();
        } else if (id == R.id.oraApplyButton) {
            validateAll();
        }
    }

    private void validateAll() {
        if (validateStudentName(studentNameEt)
                && validateRegisterNumber(studentRegisterNumberEt)
                && validateContactNumber(parentContactNumberEt) && validateContactNumber(proctorContactNumberEt)
                && validateVisitLocation(visitLocationEt) && validateVisitPurpose(visitPurposeEt)
                && validateDate(visitDateEt)
                && validateTime(checkOutTimeEt) && validateTime(checkInTimeEt) && flagOutTime && flagInTime) {

            if (validateTimeInterval(checkInTimeEt)) {
                openTermsNCondition();
            }
        }
    }

    private void openGoogleMap() {
        Toast.makeText(getContext(), "Will be available from next app update", Toast.LENGTH_LONG).show();
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

    // function definition for the process to be performed when the select Time button is clicked
    private void processCheckInTimeSelection() {

        Calendar calendar;
        int hours;
        int min;
        TimePickerDialog timePickerDialog;

        calendar = Calendar.getInstance();
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        timeSetListener = (view, hourOfDay, minute) -> {
            inHr = hourOfDay;
            inMin = minute;
            if (inHr >= 6 && inHr < 22) {
                checkInTimeEt.setError(null);
                flagInTime = true;
            } else {
                checkInTimeEt.setError(" ");
                flagInTime = false;
            }
            final String time = hourOfDay + ":" + minute;
            checkInTimeEt.setText(time);
        };

        timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hours, min, true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

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
            outMin = minute;
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
        //todo display entering of ticket description

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
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateUploadingToAllBucket();
            }
        });
        dialog.show();
    }

    private void initiateUploadingToAllBucket() {
        dialog.dismiss();
        final String[] splitDate = fdate.split("-");
        DocumentReference oraDocRef = coupleEnds.insertOREK(studentBlock, splitDate[2], splitDate[1], splitDate[0]).document();
        final String docId = oraDocRef.getId();
        final Timestamp uploadTime = new Timestamp(new Date());
        oraUpload.setOraDocId(docId);
        oraUpload.setOraStatus(ORAStatus.APPLIED.toString());
        oraUpload.setUploadTimestamp(uploadTime);

        oraDocRef.set(oraUpload)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ORAStudentLink oraStudentLink = coupleEnds.getStudentLinkId(docId, uploadTime);
                            DocumentReference studentLinkDocRef = coupleEnds.insertLinkStudentOREK(studentMailId ,studentBlock, splitDate[2], splitDate[1], splitDate[0], docId);
                            studentLinkDocRef.set(oraStudentLink).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Successfully submitted your application.Check OREK history for application status.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "Couldn't submit your application for outing! :(", Toast.LENGTH_LONG).show();
                                        oraDocRef.delete();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Couldn't submit your application for outing! :(", Toast.LENGTH_LONG).show();
                            //todo report outing form couldn't be applied
                        }
                    }
                });


    }

    private boolean validateStudentName(TextInputEditText editText) {
        final String name = Objects.requireNonNull(editText.getText()).toString().trim();
        if (TextUtils.isEmpty(name)) {
            editText.setError("Student Name is required");
            editText.requestFocus();
            return false;
        }
        char[] letters = name.toCharArray();
        for (char l : letters) {
            if (Character.isDigit(l)) {
                editText.setError("Name cannot contain digits");
                editText.requestFocus();
                return false;
            }
        }
        oraUpload.setStudentName(name);
        return true;
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

        if (editText.getId() == R.id.oraStudentParentContactNumberEt) {
            oraUpload.setParentNumber(number);
        } else if (editText.getId() == R.id.oraStudentProctorContactNumberEt) {
            oraUpload.setProctorNumber(number);
        }

        return true;
    }

    private boolean validateRegisterNumber(TextInputEditText editText) {
        final String pattern = "[1-2][0-9][A-Z]{3}[0-9]{4}";
        final String registerNum = Objects.requireNonNull(editText.getText()).toString().trim().toUpperCase(Locale.ROOT).trim();

        if (TextUtils.isEmpty(registerNum)) {
            editText.setError("Register Number is required");
            editText.requestFocus();
            return false;
        }

        if (registerNum.length() < 9) {
            editText.setError("Invalid Register Number");
            editText.requestFocus();
            return false;
        }

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(registerNum);

        if (!m.matches()) {
            editText.setError("Incorrect Pattern");
            editText.requestFocus();
            return false;
        }
        oraUpload.setStudentRegisterNumber(registerNum);
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
        oraUpload.setVisitLocation(location);
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
        oraUpload.setVisitPurpose(purpose);
        return true;
    }

    private boolean validateTime(TextInputEditText editText) {
        final String time = Objects.requireNonNull(editText.getText()).toString().trim();
        if (TextUtils.isEmpty(time)) {
            editText.setError("Pick a time");
            editText.requestFocus();
            return false;
        }
        if (editText.getId() == R.id.oraVisitCheckOutEt) {
            oraUpload.setCheckOut(time);
        } else if (editText.getId() == R.id.oraVisitCheckInEt) {
            oraUpload.setCheckIn(time);
        }
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
        oraUpload.setVisitDate(date);
        return true;
    }

    private boolean validateTimeInterval(TextInputEditText editText) {

        final String inDate = inHr + ":" + inMin;
        final String outDate = outHr + ":" + outMin;
        long diffMin = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date dIn = simpleDateFormat.parse(inDate);
            Date dOut = simpleDateFormat.parse(outDate);
            assert dIn != null;
            assert dOut != null;
            long diff = dIn.getTime() - dOut.getTime();
            diffMin = TimeUnit.MILLISECONDS.toMinutes(diff);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        if (diffMin < 60) {
            editText.setError(" ");
            editText.requestFocus();
            return false;
        }

        editText.setError(null);
        return true;

    }


}



