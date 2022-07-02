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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserAccountDeleted;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;


public class DeleteAccountFragment extends Fragment implements View.OnClickListener {

    private final String deleteKey = "DeLEtE";

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userDetailsSection = db.collection(Mod.USD.toString());
    private final CollectionReference hostelDetailsSection = db.collection(Mod.HOD.toString());
    private final CollectionReference reportSection = db.collection(Mod.RES.toString());

    MaterialButton abort, delete;
    EditText inputEt;
    ProgressBar progressBar;
    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
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
        fragmentTransaction.replace(R.id.userProfilePageContainer, viewUserProfileFragment);
        fragmentTransaction.commit();
    }

    private void deleteAccount() throws NoSuchAlgorithmException {

        final String mailId = User.getInstance().getUserMailID().toLowerCase(Locale.ROOT);
        final String scrambleValue = Scramble.getScramble(mailId.toLowerCase(Locale.ROOT));

        userDetailsSection.document(Mod.USSTU.toString())
                .collection(Mod.DET.toString())
                .document(User.getInstance().getUser_Id())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        hostelDetailsSection
                                .document(Mod.TED.toString())
                                .collection(Mod.DET.toString())
                                .document(scrambleValue)
                                .delete()
                                .addOnCompleteListener(task1 -> {
                                    if (!task1.isSuccessful()) {
                                        // TODO: 02-07-2022 report error DF003
                                        deleteAccountID();
                                    }
                                });

                        deleteAccountID();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        AlertDisplay alertDisplay = new AlertDisplay(ErrorCode.DF001.getErrorCode(), ErrorCode.DF001.getErrorMessage(), getContext());
                        alertDisplay.displayAlert();
                        // TODO: 02-07-2022  report error
                    }
                });
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
                    // TODO: 02-07-2022 report error  DF002
                    FirebaseAuth.getInstance().signOut();
                }
                progressBar.setVisibility(View.INVISIBLE);
                callBackToAccountDeletion.userAccountDeleted();
            });
        }
    }

}