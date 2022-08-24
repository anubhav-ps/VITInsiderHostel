package com.anubhav.vitinsiderhostel.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnAppErrorCreated;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserAccountDeleted;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.AppError;
import com.anubhav.vitinsiderhostel.models.User;
import com.anubhav.vitinsiderhostel.notifications.AppNotification;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;


public class DeleteAccountFragment extends Fragment implements View.OnClickListener, iOnAppErrorCreated {

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userDetailsSection = db.collection(Mod.USD.toString());
    private final CollectionReference feedbackSection = db.collection(Mod.FBK.toString());
    //string objects
    private final String deleteKey = "DeLEtE";
    // firebase auth declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    //views
    private View rootView;
    private MaterialButton abort, delete;
    private EditText inputEt;
    private ProgressBar progressBar;
    //listeners
    private iOnAppErrorCreated onAppErrorCreated;
    private iOnUserAccountDeleted callBackToAccountDeletion;

    public DeleteAccountFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_delete_account, container, false);

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();

        inputEt = rootView.findViewById(R.id.deleteAccountET);
        progressBar = rootView.findViewById(R.id.deleteAccountProgressBar);
        abort = rootView.findViewById(R.id.deleteAccountAbortBtn);
        delete = rootView.findViewById(R.id.deleteAccountDeleteBtn);

        inputEt.setTextColor(Color.parseColor("#E6626161"));
        delete.setEnabled(false);

        onAppErrorCreated = this;

        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputEt.getText().toString().equals(deleteKey)) {
                    delete.setEnabled(true);
                    inputEt.setTextColor(Color.parseColor("#FFFF4444"));
                } else {
                    delete.setEnabled(false);
                    inputEt.setTextColor(Color.parseColor("#E6626161"));
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        abort.setOnClickListener(this);
        delete.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.deleteAccountAbortBtn) {
            abortAction();
        } else if (id == R.id.deleteAccountDeleteBtn) {
            progressBar.setVisibility(View.VISIBLE);
            try {
                deleteAccount();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private void abortAction() {
        ViewUserProfileFragment viewUserProfileFragment = new ViewUserProfileFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.accountMenuPgeFragmentContainer, viewUserProfileFragment);
        fragmentTransaction.commit();
    }

    private void deleteAccount() throws NoSuchAlgorithmException {

        userDetailsSection.document(Mod.USSTU.toString())
                .collection(Mod.DET.toString())
                .document(User.getInstance().getUser_Id())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deleteAccountID();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        AlertDisplay alertDisplay = new AlertDisplay(ErrorCode.DF001.getErrorCode(), ErrorCode.DF001.getErrorMessage(), getContext());
                        alertDisplay.displayAlert();
                        AppError appError = new AppError(ErrorCode.DF001.getErrorCode(), User.getInstance().getUserMailID());
                        onAppErrorCreated.checkIfAlreadyReported(appError, "Issue Has Been Reported,You Will Be Contacted Soon");
                    }
                });

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
                .make(requireContext(), rootView.findViewById(R.id.deleteAccountFragment), message, Snackbar.LENGTH_SHORT);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof iOnUserAccountDeleted) {
            this.callBackToAccountDeletion = (iOnUserAccountDeleted) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.callBackToAccountDeletion != null) {
            this.callBackToAccountDeletion = null;
        }
    }


    public void deleteAccountID() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_LONG).show();
                } else {
                    AppNotification.getInstance().unSubscribeAllTopics();
                    FirebaseAuth.getInstance().signOut();
                    AppError appError = new AppError(ErrorCode.DF002.getErrorCode(), User.getInstance().getUserMailID());
                    onAppErrorCreated.checkIfAlreadyReported(appError, "Issue Has Been Reported,You Will Be Contacted Soon");
                }
                progressBar.setVisibility(View.INVISIBLE);
                callBackToAccountDeletion.userAccountDeleted();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                AppError appError = new AppError(ErrorCode.DF002.getErrorCode(), User.getInstance().getUserMailID());
                onAppErrorCreated.checkIfAlreadyReported(appError, "Issue Has Been Reported,You Will Be Contacted Soon");
                AppNotification.getInstance().unSubscribeAllTopics();
                FirebaseAuth.getInstance().signOut();
                progressBar.setVisibility(View.INVISIBLE);
                callBackToAccountDeletion.userAccountDeleted();
            });
        }
    }

}