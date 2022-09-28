package com.anubhav.vitinsiderhostel.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.Path;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnAppErrorCreated;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.AppError;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditProfileFragment extends Fragment implements View.OnClickListener, iOnNotifyDbProcess, iOnAppErrorCreated {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference accountsSection = db.collection(Path.ACCOUNTS.getPath());
    private final CollectionReference feedbackSection = db.collection(Path.FEEDBACKS.getPath());
    //listeners
    iOnAppErrorCreated onAppErrorCreated;
    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    //views
    private View rootView;
    private ImageView profileAvatar;
    private MaterialTextView userNameTxt, studentNameTxt, parentMailIdTxt, userMailIdTxt, contactNumberTxt, nativeStateTxt, branchTxt, registerNumberTxt;
    private ProgressBar progressBar;
    private Dialog dialog;
    //flags
    private boolean hasChanged = false;
    private String[] nativeStates;
    private ArrayAdapter<String> nativeStateAdapter;

    private int icon_Id = 100, avatar = 100;


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
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();


        MaterialTextView changeAvatarTxt = rootView.findViewById(R.id.editPgeAvatar);
        progressBar = rootView.findViewById(R.id.editPgeProgressBar);
        userNameTxt = rootView.findViewById(R.id.editPgeUserName);
        studentNameTxt = rootView.findViewById(R.id.editPgeStudentName);
        parentMailIdTxt = rootView.findViewById(R.id.editPgeParentMailId);
        userMailIdTxt = rootView.findViewById(R.id.editPgeMailId);
        contactNumberTxt = rootView.findViewById(R.id.editPgeContactNumber);
        nativeStateTxt = rootView.findViewById(R.id.editPgeNativeState);
        branchTxt = rootView.findViewById(R.id.editPgeBranch);
        registerNumberTxt = rootView.findViewById(R.id.editPgeRegisterNumber);
        profileAvatar = rootView.findViewById(R.id.editPgeAvatarIcon);

        ImageButton cancelBtn = rootView.findViewById(R.id.editPgeCancelBtn);
        MaterialButton saveBtn = rootView.findViewById(R.id.editPgeSaveBtn);

        dialog = new Dialog(getContext());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if (User.getInstance() != null) {
            setAvatar(User.getInstance().getAvatar());
            userNameTxt.setText(User.getInstance().getUserName());

            String name = transformText(User.getInstance().getStudentName().split(" "));
            studentNameTxt.setText(name);
            userMailIdTxt.setText(User.getInstance().getUserMailId());
            parentMailIdTxt.setText(User.getInstance().getParentMailId());
            contactNumberTxt.setText(User.getInstance().getUserContactNumber());
            nativeStateTxt.setText(User.getInstance().getStudentNativeState());
            branchTxt.setText(User.getInstance().getStudentBranch());
            registerNumberTxt.setText(User.getInstance().getStudentRegisterNumber());
            avatar = User.getInstance().getAvatar();
        }

        profileAvatar.setOnClickListener(this);
        changeAvatarTxt.setOnClickListener(this);
        userNameTxt.setOnClickListener(this);
        studentNameTxt.setOnClickListener(this);
        userMailIdTxt.setOnClickListener(this);
        parentMailIdTxt.setOnClickListener(this);
        contactNumberTxt.setOnClickListener(this);
        nativeStateTxt.setOnClickListener(this);
        branchTxt.setOnClickListener(this);
        registerNumberTxt.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.editPgeAvatarIcon) {
            hasChanged = true;
            openEditAvatarView();
        } else if (id == R.id.editPgeAvatar) {
            hasChanged = true;
            openEditAvatarView();
        } else if (id == R.id.editPgeUserName) {
            hasChanged = true;
            openEditUserNameView();
        } else if (id == R.id.editPgeMailId) {
            hasChanged = false;
            openEditMailIdView();
        } else if (id == R.id.editPgeStudentName) {
            hasChanged = false;
            openEditStudentName();
        } else if (id == R.id.editPgeParentMailId) {
            hasChanged = false;
            openEditParentMailId();
        } else if (id == R.id.editPgeContactNumber) {
            hasChanged = true;
            openEditContactNumberView();
        } else if (id == R.id.editPgeNativeState) {
            hasChanged = true;
            if (User.getInstance().getNativeStateChanges() < 3) {
                openEditNativeStateView();
            } else {
                callSnackBar("You cannot edit your native state anymore!");
            }
        } else if (id == R.id.editPgeBranch) {
            hasChanged = false;
            openEditBranchView();
        } else if (id == R.id.editPgeRegisterNumber) {
            hasChanged = false;
            openEditRegisterNumberView();
        } else if (id == R.id.editPgeCancelBtn) {
            if (hasChanged) {
                AlertDisplay alertDisplay = new AlertDisplay("Unsaved changes", "You have unsaved changes. Are you sure you want to cancel?", getContext());
                alertDisplay.getBuilder().setPositiveButton("Yes", (dialogInterface, i) -> editCancelBtn());
                alertDisplay.getBuilder().setNegativeButton("No", null);
                alertDisplay.getBuilder().setCancelable(false);
                alertDisplay.display();
            } else {
                editCancelBtn();
            }
        } else if (id == R.id.editPgeSaveBtn) {
            editSaveBtn();
        } else if (id == R.id.editNameViewCancelBtn || id == R.id.editContactNumberViewCancelBtn || id == R.id.editNativeLangViewCancelBtn) {
            closeEditView();
        } else if (id == R.id.avatar_2) {
            icon_Id = 201;
            setAvatar(icon_Id);
        } else if (id == R.id.avatar_3) {
            icon_Id = 202;
            setAvatar(icon_Id);
        } else if (id == R.id.avatar_4) {
            icon_Id = 203;
            setAvatar(icon_Id);
        } else if (id == R.id.avatar_5) {
            icon_Id = 204;
            setAvatar(icon_Id);
        } else if (id == R.id.avatar_6) {
            icon_Id = 205;
            setAvatar(icon_Id);
        } else if (id == R.id.avatar_7) {
            icon_Id = 206;
            setAvatar(icon_Id);
        } else if (id == R.id.avatar_8) {
            icon_Id = 207;
            setAvatar(icon_Id);
        }
    }

    private void openEditParentMailId() {
        callSnackBar("Cannot update parent mail ID");
    }

    private void openEditStudentName() {
        callSnackBar("Cannot update student name");
    }

    private void openEditAvatarView() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_avatar);

        MaterialButton apply = dialog.findViewById(R.id.dialogChooseAvatarApplyBtn);

        ImageView avatar_2 = dialog.findViewById(R.id.avatar_2);
        ImageView avatar_3 = dialog.findViewById(R.id.avatar_3);
        ImageView avatar_4 = dialog.findViewById(R.id.avatar_4);
        ImageView avatar_5 = dialog.findViewById(R.id.avatar_5);
        ImageView avatar_6 = dialog.findViewById(R.id.avatar_6);
        ImageView avatar_7 = dialog.findViewById(R.id.avatar_7);
        ImageView avatar_8 = dialog.findViewById(R.id.avatar_8);

        avatar_2.setOnClickListener(this);
        avatar_3.setOnClickListener(this);
        avatar_4.setOnClickListener(this);
        avatar_5.setOnClickListener(this);
        avatar_6.setOnClickListener(this);
        avatar_7.setOnClickListener(this);
        avatar_8.setOnClickListener(this);

        apply.setOnClickListener(v -> {
            avatar = icon_Id;
            dialog.dismiss();
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void setAvatar(int icon) {
        final String iconStr = "av_" + icon;
        int imageId = requireContext().getResources().getIdentifier(iconStr, "drawable", requireContext().getPackageName());
        profileAvatar.setImageResource(imageId);
    }

    private void openEditRegisterNumberView() {
        callSnackBar("Cannot update register number");
    }

    private void closeEditView() {
        dialog.dismiss();
    }

    private void editSaveBtn() {

        if (hasChanged) {


            progressBar.setVisibility(View.VISIBLE);
            final String userName = userNameTxt.getText().toString().trim();
            final String userContactNumber = contactNumberTxt.getText().toString().trim();
            final String userNativeState = nativeStateTxt.getText().toString().trim();

            DocumentReference docUserRef = accountsSection
                    .document(Path.STUDENTS.getPath())
                    .collection(Path.FILES.getPath())
                    .document(User.getInstance().getUser_UID());

            docUserRef.update(
                            "userName", userName,
                            "userContactNumber", userContactNumber,
                            "studentNativeState", userNativeState,
                            "nativeStateChanges", User.getInstance().getNativeStateChanges(),
                            "avatar", avatar)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            callSnackBar("Successfully Updated");
                            progressBar.setVisibility(View.GONE);
                            hasChanged = false;

                            User.getInstance().setUserName(userName);
                            User.getInstance().setUserContactNumber(userContactNumber);
                            User.getInstance().setStudentNativeState(userNativeState);
                            User.getInstance().setAvatar(avatar);

                            LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(getContext(), EditProfileFragment.this);
                            localSqlDatabase.updateUserInBackground(User.getInstance());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            AlertDisplay alertDisplay = new AlertDisplay(ErrorCode.EPF001.getErrorCode(), ErrorCode.EPF001.getErrorMessage(), getContext());
                            alertDisplay.displayAlert();
                            AppError appError = new AppError(ErrorCode.EPF001.getErrorCode(), User.getInstance().getUserMailId());
                            onAppErrorCreated.checkIfAlreadyReported(appError, "Issue Has Been Reported,Will Be Looked Upon");
                        }
                    }).addOnFailureListener(e -> callSnackBar("Couldn't perform the update,Please try again after sometime"));

        } else {
            callSnackBar("No Changes were made");
        }

    }

    private void editCancelBtn() {
        ViewUserProfileFragment viewUserProfileFragment = new ViewUserProfileFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.accountMenuPgeFragmentContainer, viewUserProfileFragment);
        fragmentTransaction.commit();
    }

    private void openEditBranchView() {
        callSnackBar("Cannot update branch");
    }

    private void openEditNativeStateView() {
        dialog.setContentView(R.layout.edit_native_state_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        AutoCompleteTextView nativeStateEt = dialog.findViewById(R.id.editNativeStateViewEt);
        nativeStateEt.setAdapter(nativeStateAdapter);
        nativeStateEt.setSelection(0);
        ImageButton cancelBtn = dialog.findViewById(R.id.editNativeLangViewCancelBtn);
        ImageButton saveBtn = dialog.findViewById(R.id.editNativeStateViewSaveBtn);

        nativeStateEt.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                nativeStateEt.setError("Select a valid state name");
                nativeStateEt.requestFocus();
                return;
            }
        });

        saveBtn.setOnClickListener(v -> {
            final String nativeState = nativeStateEt.getText().toString().trim();
            if (nativeState.equalsIgnoreCase("SELECT")) {
                nativeStateEt.setError("Select a valid state name");
                nativeStateEt.requestFocus();
                return;
            }
            User.getInstance().setNativeStateChanges(User.getInstance().getNativeStateChanges() + 1);
            setTextFieldValue(nativeStateTxt, nativeState);
            closeEditView();
        });


        cancelBtn.setOnClickListener(this);
        dialog.show();
    }

    private String transformText(String[] words) {
        StringBuilder name = new StringBuilder();
        for (String w : words) {
            String n = firstCaps(w) + " ";
            name.append(n);
        }
        return name.toString();
    }

    private String firstCaps(String name) {
        name = name.toLowerCase(Locale.ROOT);
        char[] arr = name.toCharArray();
        arr[0] = String.valueOf(arr[0]).toUpperCase(Locale.ROOT).charAt(0);
        return String.valueOf(arr);
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
        callSnackBar("Cannot update student mail ID");
    }

    private void openEditUserNameView() {
        dialog.setContentView(R.layout.edit_name_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText usernameEt = dialog.findViewById(R.id.editNameViewNameEt);
        ImageButton cancelBtn = dialog.findViewById(R.id.editNameViewCancelBtn);
        ImageButton saveBtn = dialog.findViewById(R.id.editNameViewSaveBtn);

        final String pattern = "^(?=.{8,20}$)(?!.*[_]{2})[a-zA-Z0-9_]+";

        saveBtn.setOnClickListener(v -> {
            final String username = usernameEt.getText().toString().trim();
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(username);
            if (!m.matches()) {
                usernameEt.setError("Username must be 8 to 20 characters wide.No special characters allowed except '-'");
                return;
            }
            setTextFieldValue(userNameTxt, username.trim());
            closeEditView();
        });
        cancelBtn.setOnClickListener(this);
        dialog.show();
    }

    private void setTextFieldValue(MaterialTextView textView, String value) {
        textView.setText(value);
    }

    @Override
    public void checkIfAlreadyReported(AppError appError, String message) {
        feedbackSection
                .document(Path.ISSUES.getPath())
                .collection(Path.FILES.getPath())
                .whereEqualTo("errorCode", appError.getErrorCode()).whereEqualTo("reporter", appError.getReporter()).whereEqualTo("status", TicketStatus.BOOKED.toString())
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
                .document(Path.ISSUES.getPath())
                .collection(Path.FILES.getPath())
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
                .make(requireContext(), rootView.findViewById(R.id.editProfileFragment), message, Snackbar.LENGTH_LONG);
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


    @Override
    public void notifyCompleteDataDownload() {

    }

    @Override
    public void notifyUserUpdated() {
        if (!LocalSqlDatabase.getExecutors().isTerminated()) {
            LocalSqlDatabase.stopExecutors();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        nativeStates = getResources().getStringArray(R.array.native_state);
        nativeStateAdapter = new ArrayAdapter<>(requireContext(), R.layout.visit_purpose_drop_down_item, nativeStates);

    }
}