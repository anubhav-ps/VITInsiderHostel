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
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PublicProfileFragment extends Fragment implements View.OnClickListener, iOnNotifyDbProcess {

    //firebase fireStore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference publicSection = db.collection(Mod.PUBL.toString());
    private final CollectionReference userDetailsSection = db.collection(Mod.USD.toString());

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
            enableSwitch.setChecked(User.getInstance().isHasPublicProfile());
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
        final String mail = User.getInstance().getUserMailID();
        final String interests = Objects.requireNonNull(topicEt.getText()).toString().trim();

        if (flag){
            if (TextUtils.isEmpty(interests)) {
                topicEt.setError("Cannot Be Empty");
                topicEt.requestFocus();
                return;
            }
        }

        if (flag) {
            Map<String, Object> publicProfile = new HashMap<>();
            publicProfile.put("userName", User.getInstance().getUserName());
            publicProfile.put("avatar", User.getInstance().getAvatar());
            publicProfile.put("userMailID", User.getInstance().getUserMailID());
            publicProfile.put("branch", User.getInstance().getStudentBranch());
            publicProfile.put("interests", interests);

            publicSection.document(mail).set(publicProfile).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userDetailsSection
                            .document(Mod.USSTU.toString())
                            .collection(Mod.DET.toString())
                            .document(User.getInstance().getUser_Id())
                            .update("hasPublicProfile", true).addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {
                            callSnackBar("Public Profile Has Been Turned On");
                            User.getInstance().setHasPublicProfile(true);
                            LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(getContext(), PublicProfileFragment.this);
                            localSqlDatabase.updateUserInBackground(User.getInstance());
                        }
                    });
                }
            });

        } else {

            publicSection.document(mail).delete().addOnCompleteListener(task -> userDetailsSection
                    .document(Mod.USSTU.toString())
                    .collection(Mod.DET.toString())
                    .document(User.getInstance().getUser_Id())
                    .update("hasPublicProfile", false).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            callSnackBar("Public Profile Has Been Turned Off");
                            User.getInstance().setHasPublicProfile(false);
                            LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(getContext(), PublicProfileFragment.this);
                            localSqlDatabase.updateUserInBackground(User.getInstance());
                        }
                    })).addOnFailureListener(e -> callSnackBar(e.getMessage()));

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