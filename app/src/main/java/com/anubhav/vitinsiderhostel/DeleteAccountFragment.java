package com.anubhav.vitinsiderhostel;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;


public class DeleteAccountFragment extends Fragment implements View.OnClickListener {

    private final String deleteKey = "DeLEtE";
    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userSection = db.collection("Users");
    private final CollectionReference userBlockRecord = db.collection("UserBlockRec");
    MaterialButton abort, delete;
    EditText inputEt;
    ProgressBar progressBar;
    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private onUserAccountDeletedListener callBackToAccountDeletion;

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
        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();

        inputEt = view.findViewById(R.id.deleteAccountET);
        progressBar = view.findViewById(R.id.deleteAccountProgressBar);
        abort = view.findViewById(R.id.deleteAccountAbortBtn);
        delete = view.findViewById(R.id.deleteAccountDeleteBtn);

        inputEt.setTextColor(Color.parseColor("#E6626161"));
        delete.setEnabled(false);

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

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.deleteAccountAbortBtn) {
            abortAction();
        } else if (id == R.id.deleteAccountDeleteBtn) {
            progressBar.setVisibility(View.VISIBLE);
            deleteAccount();
        }
    }

    private void abortAction() {
        ViewUserProfileFragment viewUserProfileFragment = new ViewUserProfileFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userProfilePageContainer, viewUserProfileFragment);
        fragmentTransaction.commit();
    }

    private void deleteAccount() {

        final String userDoc = User.getInstance().getDoc_Id();
        final String userType = User.getInstance().getUserType();
        final String userBlock = User.getInstance().getStudentBlock();
        final String mailId = User.getInstance().getUserMailID().toLowerCase(Locale.ROOT);

        if (userType.equalsIgnoreCase("S")) {
            userSection
                    .document(userType)
                    .collection(userBlock)
                    .document(userDoc)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            userBlockRecord
                                    .document(mailId)
                                    .delete()
                                    .addOnCompleteListener(task12 -> {
                                        if (task12.isSuccessful()) {
                                            user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user != null) {
                                                user.delete()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                callBackToAccountDeletion.userAccountDeleted();

                                                            } else {
                                                                // TODO: report -> user-account deletion problem
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                FirebaseAuth.getInstance().signOut();
                                                                callBackToAccountDeletion.userAccountDeleted();

                                                            }
                                                        });
                                            }
                                        } else {
                                            //TODO: report -> user block record deletion error
                                            progressBar.setVisibility(View.INVISIBLE);
                                            FirebaseAuth.getInstance().signOut();
                                            callBackToAccountDeletion.userAccountDeleted();

                                        }
                                    });
                        } else {
                            //TODO: report -> users record deletion error
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onUserAccountDeletedListener) {
            this.callBackToAccountDeletion = (onUserAccountDeletedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.callBackToAccountDeletion != null) {
            this.callBackToAccountDeletion = null;
        }
    }

    public interface onUserAccountDeletedListener {
        void userAccountDeleted();
    }
}