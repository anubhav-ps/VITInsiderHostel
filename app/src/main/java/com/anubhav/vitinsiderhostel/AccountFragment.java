package com.anubhav.vitinsiderhostel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AccountFragment extends Fragment implements View.OnClickListener {


    private View view;

    private MaterialTextView userNameTxt, userMailIdTxt;
    private MaterialTextView userProfileTxt, ticketHistoryTxt, aboutAppTxt, shareAppTxt, reportIssueTxt, signOutTxt, versionCodeTxt;

    private String username;
    private String userMailId;

    //firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;


    private onUserProfileCalledListener callbackToFragmentContainer;

    public interface onUserProfileCalledListener {
        void onUserProfileCalled();
    }


    public AccountFragment() {
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
        view = inflater.inflate(R.layout.fragment_account, container, false);

        userNameTxt = view.findViewById(R.id.accountPgeUserName);
        userMailIdTxt = view.findViewById(R.id.accountPgeUserMail);

        userProfileTxt = view.findViewById(R.id.accountPgeViewUserProfile);
        ticketHistoryTxt = view.findViewById(R.id.accountPgeViewTicketHistory);
        aboutAppTxt = view.findViewById(R.id.accountPgeViewAbout);
        shareAppTxt = view.findViewById(R.id.accountPgeViewShareApp);
        reportIssueTxt = view.findViewById(R.id.accountPgeViewReportIssue);

        signOutTxt = view.findViewById(R.id.accountPgeSignOut);
        versionCodeTxt = view.findViewById(R.id.accountPgeAppVersion);

        if (User.getInstance() != null) {
            username = User.getInstance().getUserName();
            userMailId = User.getInstance().getUserMailID();
        }

        userNameTxt.setText(username);
        userMailIdTxt.setText(userMailId);

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {


            } else {

            }
        };

        signOutTxt.setOnClickListener(this);
        userProfileTxt.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accountPgeSignOut) {
            processSignOut();
        } else if (id == R.id.accountPgeViewUserProfile) {
            openUserProfile();
        }
    }

    private void openUserProfile() {
        this.callbackToFragmentContainer.onUserProfileCalled();
    }

    private void processSignOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity(), "Logging out, see you soon !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        requireActivity().finish();
    }

    //process 0 and 1
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
        Activity activity = (Activity) context;
        try {
            this.callbackToFragmentContainer = (onUserProfileCalledListener) activity;
        }  catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+"is not implementing onUserProfileCalledListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.callbackToFragmentContainer!=null){
            this.callbackToFragmentContainer = null;
        }
    }
}