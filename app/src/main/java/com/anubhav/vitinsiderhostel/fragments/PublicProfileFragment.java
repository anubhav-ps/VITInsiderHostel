package com.anubhav.vitinsiderhostel.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.Path;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class PublicProfileFragment extends Fragment implements View.OnClickListener, iOnNotifyDbProcess {

    //firebase fireStore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference accountsSection = db.collection(Path.ACCOUNTS.getPath());

    private View rootView;
    private TextInputEditText topicEt;
    private SwitchCompat enableSwitch;
    private MaterialButton applyBtn;


    private boolean flagHasChanged = false;

    public PublicProfileFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_public_profile, container, false);

        topicEt = rootView.findViewById(R.id.publicProfilePgeInterestEt);
        enableSwitch = rootView.findViewById(R.id.publicProfilePgeEnableSwitch);
        applyBtn = rootView.findViewById(R.id.publicProfilePgeApplyBtn);

        // check for null value before accessing object data members or methods
        if (User.getInstance() != null) {
            enableSwitch.setChecked(User.getInstance().getHasPublicProfile());
        }

        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            flagHasChanged = true;
        });

        applyBtn.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.publicProfilePgeApplyBtn) {
            try {
                updateProfileStatus();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProfileStatus() throws NoSuchAlgorithmException {
        final boolean flag = enableSwitch.isChecked();
        final String bio = Objects.requireNonNull(topicEt.getText()).toString().trim();

        if (flag) {
            if (TextUtils.isEmpty(bio)) {
                topicEt.setError("Cannot Be Empty");
                topicEt.requestFocus();
                return;
            }
        }

        DocumentReference docUserRef = accountsSection
                .document(Path.STUDENTS.getPath())
                .collection(Path.FILES.getPath())
                .document(User.getInstance().getUser_UID());

        if (flag) {

            docUserRef.update(
                            "hasPublicProfile", true,
                            "publicBio", bio,
                            "publicColor", Color.BLACK)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            callSnackBar("Successfully Updated, Public Profile Created");

                            User.getInstance().setHasPublicProfile(true);
                            User.getInstance().setPublicBio(bio);
                            User.getInstance().setPublicColor(Color.BLACK);

                            LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(getContext(), PublicProfileFragment.this);
                            localSqlDatabase.updateUserInBackground(User.getInstance());

                        }
                    }).addOnFailureListener(e -> callSnackBar("Couldn't perform the update,Please try again after sometime"));

        } else {

            docUserRef.update("hasPublicProfile", false)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            callSnackBar("Successfully Updated, Public Profile Deleted");

                            User.getInstance().setHasPublicProfile(false);

                            LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(getContext(), PublicProfileFragment.this);
                            localSqlDatabase.updateUserInBackground(User.getInstance());

                        }
                    }).addOnFailureListener(e -> callSnackBar("Couldn't perform the update,Please try again after sometime"));


        }

    }


    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(requireContext(), rootView.findViewById(R.id.publicProfileFragment), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navy_blue));
        snackbar.show();
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
}