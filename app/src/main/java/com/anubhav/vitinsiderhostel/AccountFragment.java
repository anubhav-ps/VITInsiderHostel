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
import androidx.lifecycle.ViewModelProvider;

import com.anubhav.vitinsiderhostel.appviewmodel.AppViewModel;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountFragment extends Fragment implements View.OnClickListener {


    // user instance values
    private String username;
    private String userMailId;

    //when user profile activity is called
    private onUserProfileCalledListener callbackToFragmentContainer;

    private AppViewModel appViewModel;

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
        View view = inflater.inflate(R.layout.fragment_account, container, false);


        // view declarations
        MaterialTextView userNameTxt = view.findViewById(R.id.accountPgeUserName);
        MaterialTextView userMailIdTxt = view.findViewById(R.id.accountPgeUserMail);

        MaterialTextView userProfileTxt = view.findViewById(R.id.accountPgeViewUserProfile);
        MaterialTextView ticketHistoryTxt = view.findViewById(R.id.accountPgeViewTicketHistory);
        MaterialTextView aboutAppTxt = view.findViewById(R.id.accountPgeViewAbout);
        MaterialTextView shareAppTxt = view.findViewById(R.id.accountPgeViewShareApp);
        MaterialTextView reportIssueTxt = view.findViewById(R.id.accountPgeViewReportIssue);

        MaterialTextView signOutTxt = view.findViewById(R.id.accountPgeSignOut);
        MaterialTextView versionCodeTxt = view.findViewById(R.id.accountPgeAppVersion);


        if (User.getInstance() != null) {
            username = User.getInstance().getUserName();
            userMailId = User.getInstance().getUserMailID();
        }

        userNameTxt.setText(username);
        userMailIdTxt.setText(userMailId);

        signOutTxt.setOnClickListener(this);
        userProfileTxt.setOnClickListener(this);

        return view;
    }

    // on-click listeners onUserProfileCalledListener
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accountPgeSignOut) {
            processSignOut();
        } else if (id == R.id.accountPgeViewUserProfile) {
            openUserProfile();
        }
    }

    // callback to
    private void openUserProfile() {
        this.callbackToFragmentContainer.onUserProfileCalled();
    }

    // method to handle the sign out process
    private void processSignOut() {
        FirebaseAuth.getInstance().signOut();
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        appViewModel.deleteAllUsers();
        appViewModel.deleteAllTenants();
        Toast.makeText(getActivity(), "Logging out, see you soon !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        requireActivity().finish();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            this.callbackToFragmentContainer = (onUserProfileCalledListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "is not implementing onUserProfileCalledListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.callbackToFragmentContainer != null) {
            this.callbackToFragmentContainer = null;
        }
    }

    // listener to listen to click on user profile text lbl
    public interface onUserProfileCalledListener {
        void onUserProfileCalled();
    }
}