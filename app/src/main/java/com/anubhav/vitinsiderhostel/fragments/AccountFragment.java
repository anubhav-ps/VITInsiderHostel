package com.anubhav.vitinsiderhostel.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.BuildConfig;
import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.activities.LoginActivity;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.interfaces.iOnTicketSectionClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserProfileClicked;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment implements View.OnClickListener {

    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //view declaration
    private View rootView;
    private MaterialTextView userNameTxt, userMailIdTxt;
    private Dialog dialog;

    //listeners
    private iOnTicketSectionClicked onTicketSectionChosen;

    //string object
    private String username, userMailId;


    //listeners
    private iOnUserProfileClicked callbackToFragmentContainer;

    //local database
    private LocalSqlDatabase localSqlDatabase;

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
        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        dialog = new Dialog(getContext());

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();

        localSqlDatabase = new LocalSqlDatabase(getContext());


        // view declarations
        userNameTxt = rootView.findViewById(R.id.accountPgeUserName);
        userMailIdTxt = rootView.findViewById(R.id.accountPgeUserMail);

        MaterialTextView userProfileTxt = rootView.findViewById(R.id.accountPgeViewUserProfile);
        MaterialTextView ticketHistoryTxt = rootView.findViewById(R.id.accountPgeViewTicketHistory);
        MaterialTextView aboutAppTxt = rootView.findViewById(R.id.accountPgeViewAbout);
        MaterialTextView shareAppTxt = rootView.findViewById(R.id.accountPgeViewShareApp);
        MaterialTextView reportIssueTxt = rootView.findViewById(R.id.accountPgeViewReportIssue);

        MaterialTextView signOutTxt = rootView.findViewById(R.id.accountPgeSignOut);
        MaterialTextView versionCodeTxt = rootView.findViewById(R.id.accountPgeAppVersion);

        String versionName = "v ";
        try {
            versionName = versionName + BuildConfig.VERSION_NAME;
        } catch (Exception exception) {
            callSnackBar("Error fetching version name");
        }
        versionCodeTxt.setText(versionName);

        setUserDetails();

        signOutTxt.setOnClickListener(this);
        userProfileTxt.setOnClickListener(this);
        ticketHistoryTxt.setOnClickListener(this);

        return rootView;
    }

    private void setUserDetails() {
        if (User.getInstance() != null) {
            username = User.getInstance().getUserName();
            userMailId = User.getInstance().getUserMailID();
        }

        userNameTxt.setText(username);
        userMailIdTxt.setText(userMailId);
    }

    // on-click listeners onUserProfileCalledListener
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accountPgeSignOut) {
            processSignOut();
        } else if (id == R.id.accountPgeViewUserProfile) {
            openUserProfile();
        } else if (id == R.id.accountPgeViewTicketHistory) {
            promptTicketSection();
        }
    }

    private void promptTicketSection() {
        dialog.setContentView(R.layout.choose_ticket_section_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        MaterialTextView roomTicket = dialog.findViewById(R.id.chooseTicketRoomTxt);
        MaterialTextView blockTicket = dialog.findViewById(R.id.chooseTicketBlockTxt);

        roomTicket.setOnClickListener(v -> {
            dialog.dismiss();
            openRoomTickets();
        });

        blockTicket.setOnClickListener(v -> {
            dialog.dismiss();
            openBlockTickets();
        });

        dialog.show();


    }

    private void openRoomTickets() {
        onTicketSectionChosen.roomTicketClicked();
    }

    private void openBlockTickets() {
        onTicketSectionChosen.blockTicketClicked();
    }

    // callback to
    private void openUserProfile() {
        this.callbackToFragmentContainer.userProfileCalled();
    }

    // method to handle the sign out process
    private void processSignOut() {
        FirebaseAuth.getInstance().signOut();
        LocalSqlDatabase localSqlDatabase = new LocalSqlDatabase(getActivity());
        localSqlDatabase.deleteCurrentUser();
        localSqlDatabase.deleteAllTenants();
        Toast.makeText(getActivity(), "Logging out, see you soon !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        requireActivity().finish();
    }

    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(requireContext(), rootView.findViewById(R.id.accountFragment), message, Snackbar.LENGTH_SHORT);
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

        if (User.getInstance() == null) {
            User user = null;
            user = localSqlDatabase.getCurrentUser();
        }

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
            this.callbackToFragmentContainer = (iOnUserProfileClicked) activity;
            this.onTicketSectionChosen = (iOnTicketSectionClicked) activity;
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
        if (this.onTicketSectionChosen != null) {
            this.onTicketSectionChosen = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserDetails();
    }


}