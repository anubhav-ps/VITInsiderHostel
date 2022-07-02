package com.anubhav.vitinsiderhostel.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserAccountDeleted;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserAccountEdited;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditProfileFragment extends Fragment implements View.OnClickListener {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userDetailsSection = db.collection(Mod.USD.toString());
    private final CollectionReference hostelDetailsSection = db.collection(Mod.HOD.toString());
    private final CollectionReference reportSection = db.collection(Mod.RES.toString());


    private boolean hasChanged = false;
    private Dialog dialog;
    private MaterialTextView userNameTxt;
    private MaterialTextView userMailIdTxt;
    private MaterialTextView contactNumberTxt;
    private MaterialTextView nativeLanguageTxt;
    private MaterialTextView branchTxt;
    private MaterialTextView registerNumberTxt;

    private ProgressBar progressBar;

    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;


    private iOnUserAccountEdited onUserAccountEdited;

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

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();


        MaterialTextView changeAvatarTxt = view.findViewById(R.id.editPgeAvatar);
        progressBar = view.findViewById(R.id.editPgeProgressBar);
        userNameTxt = view.findViewById(R.id.editPgeUserName);
        userMailIdTxt = view.findViewById(R.id.editPgeMailId);
        contactNumberTxt = view.findViewById(R.id.editPgeContactNumber);
        nativeLanguageTxt = view.findViewById(R.id.editPgeNativeLanguage);
        branchTxt = view.findViewById(R.id.editPgeBranch);
        registerNumberTxt = view.findViewById(R.id.editPgeRegisterNumber);

        ImageButton cancelBtn = view.findViewById(R.id.editPgeCancelBtn);
        MaterialButton saveBtn = view.findViewById(R.id.editPgeSaveBtn);

        dialog = new Dialog(getContext());

        changeAvatarTxt.setOnClickListener(this);
        userNameTxt.setOnClickListener(this);
        userMailIdTxt.setOnClickListener(this);
        contactNumberTxt.setOnClickListener(this);
        nativeLanguageTxt.setOnClickListener(this);
        branchTxt.setOnClickListener(this);
        registerNumberTxt.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        if (User.getInstance() != null) {
            userNameTxt.setText(User.getInstance().getUserName());
            userMailIdTxt.setText(User.getInstance().getUserMailID());
            contactNumberTxt.setText(User.getInstance().getUserContactNumber());
            nativeLanguageTxt.setText(User.getInstance().getStudentNativeLanguage());
            branchTxt.setText(User.getInstance().getStudentBranch());
            registerNumberTxt.setText(User.getInstance().getStudentRegisterNumber());
        }


        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.editPgeAvatarIcon) {
            hasChanged = true;

        } else if (id == R.id.editPgeAvatar) {
            hasChanged = true;

        } else if (id == R.id.editPgeUserName) {
            hasChanged = true;
            openEditUserNameView();
        } else if (id == R.id.editPgeMailId) {
            hasChanged = false;
            openEditMailIdView();
        } else if (id == R.id.editPgeContactNumber) {
            hasChanged = true;
            openEditContactNumberView();
        } else if (id == R.id.editPgeNativeLanguage) {
            hasChanged = true;
            openEditNativeLanguageView();
        } else if (id == R.id.editPgeBranch) {
            hasChanged = false;
            openEditBranchView();
        } else if (id == R.id.editPgeRegisterNumber) {
            hasChanged = false;
            openEditRegisterNumberView();
        } else if (id == R.id.editPgeCancelBtn) {
            if (hasChanged) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Unsaved changes")
                        .setMessage("You have unsaved changes. Are you sure you want to cancel?")
                        .setPositiveButton("Yes", (dialog, which) -> editCancelBtn())
                        .setNegativeButton("No", null)
                        .show();
            } else {
                editCancelBtn();
            }
        } else if (id == R.id.editPgeSaveBtn) {
            try {
                editSaveBtn();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.editNameViewCancelBtn || id == R.id.editContactNumberViewCancelBtn || id == R.id.editNativeLangViewCancelBtn) {
            closeEditView();
        }
    }

    private void openEditRegisterNumberView() {
        Toast.makeText(getContext(), "Cannot update register number", Toast.LENGTH_LONG).show();
    }

    private void closeEditView() {
        dialog.dismiss();
    }

    private void editSaveBtn() throws NoSuchAlgorithmException {

        if (hasChanged) {


            progressBar.setVisibility(View.VISIBLE);
            final String userName = userNameTxt.getText().toString().trim();
            final String userContactNumber = contactNumberTxt.getText().toString().trim();
            final String userNativeLanguage = nativeLanguageTxt.getText().toString().trim();
            final int avatar = 100;

            final String scrambleValue = Scramble.getScramble(User.getInstance().getUserMailID());

            DocumentReference docUserRef = userDetailsSection
                    .document(Mod.USSTU.toString())
                    .collection(Mod.DET.toString())
                    .document(User.getInstance().getUser_Id());

            DocumentReference docTenantRef = hostelDetailsSection
                    .document(Mod.TED.toString())
                    .collection(Mod.DET.toString())
                    .document(scrambleValue);

            docUserRef.update(
                    "userName", userName,
                    "userContactNumber", userContactNumber,
                    "studentNativeLanguage", userNativeLanguage,
                    "avatar", avatar)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            docTenantRef.update(
                                    "tenantUserName", userName,
                                    "tenantContactNumber", userContactNumber,
                                    "tenantNativeLanguage", userNativeLanguage,
                                    "tenantAvatar", avatar
                            ).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(getContext(), "Successfully updated, Login Again....", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    hasChanged = false;
                                    FirebaseAuth.getInstance().signOut();
                                    LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(getActivity());
                                    localSqlDatabase.deleteCurrentUser();
                                    localSqlDatabase.deleteAllTenants();
                                    onUserAccountEdited.onUserAccountEdited();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    AlertDisplay alertDisplay = new AlertDisplay(ErrorCode.EPF002.getErrorCode(), ErrorCode.EPF002.getErrorMessage(), getContext());
                                    alertDisplay.displayAlert();
                                    // TODO: 02-07-2022 report error
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            AlertDisplay alertDisplay = new AlertDisplay(ErrorCode.EPF001.getErrorCode(), ErrorCode.EPF001.getErrorMessage(), getContext());
                            alertDisplay.displayAlert();
                            // TODO: 02-07-2022 report error
                        }

                    });

        } else {
            Toast.makeText(getContext(), "No Changes were made", Toast.LENGTH_SHORT).show();
        }

    }

    private void editCancelBtn() {
        ViewUserProfileFragment viewUserProfileFragment = new ViewUserProfileFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userProfilePageContainer, viewUserProfileFragment);
        fragmentTransaction.commit();
    }

    private void openEditBranchView() {
        Toast.makeText(getContext(), "Cannot update branch", Toast.LENGTH_LONG).show();
    }

    private void openEditNativeLanguageView() {
        dialog.setContentView(R.layout.edit_native_language_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText nativeLanguageEt = dialog.findViewById(R.id.editNativeLangViewEt);
        ImageButton cancelBtn = dialog.findViewById(R.id.editNativeLangViewCancelBtn);
        ImageButton saveBtn = dialog.findViewById(R.id.editNativeLangViewSaveBtn);

        saveBtn.setOnClickListener(v -> {
            final String nativeLanguage = nativeLanguageEt.getText().toString().trim();
            if (TextUtils.isEmpty(nativeLanguage)) {
                nativeLanguageEt.setError("Language cannot be empty");
                return;
            }
            setTextFieldValue(nativeLanguageTxt, nativeLanguage);
            closeEditView();
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

        saveBtn.setOnClickListener(v -> {
            final String contactNumber = contactNumberEt.getText().toString().trim();

            if (TextUtils.isEmpty(contactNumber)) {
                contactNumberEt.setError("Contact number is required");
                return;
            }

            final String pattern = "[+][0-9]{11,14}";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(contactNumber);

            if (!m.matches()) {
                contactNumberEt.setError("Invalid or Incorrect Number");
                return;
            }

            setTextFieldValue(contactNumberTxt, contactNumber);
            closeEditView();
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
        saveBtn.setOnClickListener(v -> {
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
        });
        cancelBtn.setOnClickListener(this);
        dialog.show();
    }

    private void setTextFieldValue(MaterialTextView textView, String value) {
        textView.setText(value);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof iOnUserAccountDeleted) {
            this.onUserAccountEdited = (iOnUserAccountEdited) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.onUserAccountEdited != null) {
            this.onUserAccountEdited = null;
        }
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