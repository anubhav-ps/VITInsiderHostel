package com.anubhav.vitinsiderhostel.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.activities.LoginActivity;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.models.AppError;
import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private final List<String> branchList = new ArrayList<>() {
        {
            add("Pick your branch");
            add("Computer Science and Engineering");
            add("Mechanical Engineering");
            add("Civil Engineering");
            add("Electronics Engineering");
            add("Fashion Institute of Technology");
            add("Electrical Engineering");
            add("Law");
            add("Business School");
            add("Advanced Sciences");
        }
    };
    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userSection = db.collection("Users");
    private final CollectionReference tenantsBioSection = db.collection("TenantsBio");
    private final CollectionReference reportSection = db.collection("Reports");
    private ArrayAdapter<String> branchAdapter;
    private boolean hasChanged = false;
    private Dialog dialog;
    private MaterialTextView userNameTxt;
    private MaterialTextView userMailIdTxt;
    private MaterialTextView contactNumberTxt;
    private MaterialTextView nativeLanguageTxt;
    private MaterialTextView branchTxt;
    private ProgressBar progressBar;

    private String branchInnerVal;

    public EditProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        MaterialTextView changeAvatarTxt = view.findViewById(R.id.editPgeAvatar);
        progressBar = view.findViewById(R.id.editPgeProgressBar);
        userNameTxt = view.findViewById(R.id.editPgeUserName);
        userMailIdTxt = view.findViewById(R.id.editPgeMailId);
        contactNumberTxt = view.findViewById(R.id.editPgeContactNumber);
        nativeLanguageTxt = view.findViewById(R.id.editPgeNativeLanguage);
        branchTxt = view.findViewById(R.id.editPgeBranch);

        branchAdapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_option, branchList);

        ImageButton cancelBtn = view.findViewById(R.id.editPgeCancelBtn);
        MaterialButton saveBtn = view.findViewById(R.id.editPgeSaveBtn);

        dialog = new Dialog(getContext());

        changeAvatarTxt.setOnClickListener(this);
        userNameTxt.setOnClickListener(this);
        userMailIdTxt.setOnClickListener(this);
        contactNumberTxt.setOnClickListener(this);
        nativeLanguageTxt.setOnClickListener(this);
        branchTxt.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        if (User.getInstance() != null) {
            userNameTxt.setText(User.getInstance().getUserName());
            userMailIdTxt.setText(User.getInstance().getUserMailID());
            contactNumberTxt.setText(User.getInstance().getUserContactNumber());
            nativeLanguageTxt.setText(User.getInstance().getStudentNativeLanguage());
            branchTxt.setText(User.getInstance().getStudentBranch());
        }


        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.editPgeAvatarIcon) {

        } else if (id == R.id.editPgeAvatar) {
            hasChanged = true;

        } else if (id == R.id.editPgeUserName) {
            hasChanged = true;
            openEditUserNameView();
        } else if (id == R.id.editPgeMailId) {
            openEditMailIdView();
        } else if (id == R.id.editPgeContactNumber) {
            hasChanged = true;
            openEditContactNumberView();
        } else if (id == R.id.editPgeNativeLanguage) {
            hasChanged = true;
            openEditNativeLanguageView();
        } else if (id == R.id.editPgeBranch) {
            hasChanged = true;
            openEditBranchView();
        } else if (id == R.id.editPgeCancelBtn) {
            if (hasChanged) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Unsaved changes")
                        .setMessage("You have unsaved changes. Are you sure you want to cancel?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editCancelBtn();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                editCancelBtn();
            }
        } else if (id == R.id.editPgeSaveBtn) {
            editSaveBtn();
        } else if (id == R.id.editNameViewCancelBtn || id == R.id.editContactNumberViewCancelBtn || id == R.id.editNativeLangViewCancelBtn || id == R.id.editBranchViewCancelBtn) {
            closeEditView();
        }
    }

    private void closeEditView() {
        dialog.dismiss();
    }

    private void editSaveBtn() {

        progressBar.setVisibility(View.VISIBLE);
        final String userName = userNameTxt.getText().toString().trim();
        final String userContactNumber = contactNumberTxt.getText().toString().trim();
        final String userNativeLanguage = nativeLanguageTxt.getText().toString().trim();
        final String userBranch = branchTxt.getText().toString().trim();

        DocumentReference documentReferenceToUserDetails = userSection
                .document("S")
                .collection(User.getInstance().getStudentBlock())
                .document(User.getInstance().getDoc_Id());

        DocumentReference documentReferenceToTenantBio = tenantsBioSection
                .document(User.getInstance().getStudentBlock())
                .collection(User.getInstance().getRoomNo())
                .document(User.getInstance().getUserMailID().toLowerCase(Locale.ROOT));

        documentReferenceToUserDetails
                .update(
                        "userName", userName,
                        "userContactNumber", userContactNumber,
                        "studentNativeLanguage", userNativeLanguage,
                        "studentBranch", userBranch
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    documentReferenceToTenantBio.update(
                            "tenantUserName", userName,
                            "tenantContactNumber", userContactNumber,
                            "tenantNativeLanguage", userNativeLanguage,
                            "tenantBranch", userBranch
                    ).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Successfully updated, Login Again....", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                hasChanged = false;
                                FirebaseAuth.getInstance().signOut();
                                LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(getActivity());
                                localSqlDatabase.deleteCurrentUser();
                                localSqlDatabase.deleteAllTenants();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                requireActivity().startActivity(intent);
                                requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                requireActivity().finish();

                            } else {
                                progressBar.setVisibility(View.GONE);
                                AppError appError = new AppError(
                                        ErrorCode.EPF002,
                                        User.getInstance().getUserMailID(),
                                        User.getInstance().getUserType(),
                                        new Timestamp(new Date()));
                                reportSection.document().set(appError);
                                Toast.makeText(getContext(), "Error-EPF002", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    AppError appError = new AppError(
                            ErrorCode.EPF001,
                            User.getInstance().getUserMailID(),
                            User.getInstance().getUserType(),
                            new Timestamp(new Date()));
                    reportSection.document().set(appError);
                    Toast.makeText(getContext(), "Error-EPF001", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void editCancelBtn() {
        ViewUserProfileFragment viewUserProfileFragment = new ViewUserProfileFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userProfilePageContainer, viewUserProfileFragment);
        fragmentTransaction.commit();
    }

    private void openEditBranchView() {
        dialog.setContentView(R.layout.edit_branch_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        AutoCompleteTextView branchEt = dialog.findViewById(R.id.editBranchViewEt);
        ImageButton cancelBtn = dialog.findViewById(R.id.editBranchViewCancelBtn);
        ImageButton saveBtn = dialog.findViewById(R.id.editBranchViewSaveBtn);
        branchEt.setAdapter(branchAdapter);

        branchEt.setOnItemClickListener((adapterView, view, i, l) -> {
            String val = adapterView.getItemAtPosition(i).toString();
            String branchName = "N/A";
            if (val.equalsIgnoreCase("Pick your branch")) {
                branchName = "N/A";
            } else {
                branchName = val;
            }
            branchInnerVal = branchName;
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextFieldValue(branchTxt, branchInnerVal);
                closeEditView();
            }
        });
        cancelBtn.setOnClickListener(this);
        dialog.show();
    }

    private void openEditNativeLanguageView() {
        dialog.setContentView(R.layout.edit_native_language_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText nativeLanguageEt = dialog.findViewById(R.id.editNativeLangViewEt);
        ImageButton cancelBtn = dialog.findViewById(R.id.editNativeLangViewCancelBtn);
        ImageButton saveBtn = dialog.findViewById(R.id.editNativeLangViewSaveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nativeLanguage = nativeLanguageEt.getText().toString().trim();
                if (TextUtils.isEmpty(nativeLanguage)) {
                    nativeLanguageEt.setError("Language cannot be empty");
                    return;
                }
                setTextFieldValue(nativeLanguageTxt, nativeLanguage);
                closeEditView();
            }
        });


        cancelBtn.setOnClickListener(this);
        dialog.show();
    }

    private void openEditContactNumberView() {
        dialog.setContentView(R.layout.edit_contact_number_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText contactNumberEt = dialog.findViewById(R.id.editContactNumberViewEt);
        ImageButton cancelBtn = dialog.findViewById(R.id.editContactNumberViewCancelBtn);
        ImageButton saveBtn = dialog.findViewById(R.id.editContactNumberViewSaveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String contactNumber = contactNumberEt.getText().toString().trim();
                final String pattern = "\\d{10}";
                Pattern p = Pattern.compile(pattern);
                if (contactNumber.length() != 10) {
                    contactNumberEt.setError("Contact number should be 10 digits");
                    return;
                }
                if (!p.matcher(contactNumber).matches()) {
                    contactNumberEt.setError("Contact number should not contain any special symbols");
                    return;
                }
                setTextFieldValue(contactNumberTxt, contactNumber);
                closeEditView();
            }
        });
        cancelBtn.setOnClickListener(this);
        dialog.show();
    }

    private void openEditMailIdView() {
        Toast.makeText(getContext(), "Cannot update user mail ID", Toast.LENGTH_LONG).show();
    }

    private void openEditUserNameView() {
        dialog.setContentView(R.layout.edit_name_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText usernameEt = dialog.findViewById(R.id.editNameViewNameEt);
        ImageButton cancelBtn = dialog.findViewById(R.id.editNameViewCancelBtn);
        ImageButton saveBtn = dialog.findViewById(R.id.editNameViewSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEt.getText().toString().trim();
                if (username.length() < 10 || username.length() > 20) {
                    usernameEt.setError("Username should be minimum 10 characters wide and   20 characters maximum");
                    return;
                }
                if (!TextUtils.isEmpty(username)) {
                    if (Character.isDigit(username.toCharArray()[0])) {
                        usernameEt.setError("Username cannot begin with digits");
                        return;
                    }
                }
                setTextFieldValue(userNameTxt, username);
                closeEditView();
            }
        });
        cancelBtn.setOnClickListener(this);
        dialog.show();
    }

    private void setTextFieldValue(MaterialTextView textView, String value) {
        textView.setText(value);
    }

}