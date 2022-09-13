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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.BuildConfig;
import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.activities.LoginActivity;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.interfaces.iOnAccountMenuClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnTicketSectionClicked;
import com.anubhav.vitinsiderhostel.models.User;
import com.anubhav.vitinsiderhostel.notifications.AppNotification;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment implements View.OnClickListener {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference tokenSection = db.collection(Mod.FCM.toString());

    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //view declaration
    private View rootView;
    private ImageView avatarImg;
    private MaterialTextView userNameTxt, userMailIdTxt;
    private Dialog dialog;

    //listeners
    private iOnTicketSectionClicked onTicketSectionChosen;

    //string object
    private String username, userMailId;


    //listeners
    private iOnAccountMenuClicked onAccountMenuClicked;

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

        avatarImg = rootView.findViewById(R.id.accountPgeAvatarIcon);

        MaterialTextView userProfileTxt = rootView.findViewById(R.id.accountPgeViewUserProfile);
        MaterialTextView publicProfileTxt = rootView.findViewById(R.id.accountPgeViewPublicProfile);
        MaterialTextView ticketHistoryTxt = rootView.findViewById(R.id.accountPgeViewTicketHistory);
        MaterialTextView notificationTxt = rootView.findViewById(R.id.accountPgeNotificationSetting);

        MaterialTextView aboutAppTxt = rootView.findViewById(R.id.accountPgeViewAbout);
        MaterialTextView shareAppTxt = rootView.findViewById(R.id.accountPgeViewShareApp);

        MaterialTextView reportIssueTxt = rootView.findViewById(R.id.accountPgeViewReportIssue);
        MaterialTextView bugTxt = rootView.findViewById(R.id.accountPgeViewSpottedBug);
        MaterialTextView suggestionTxt = rootView.findViewById(R.id.accountPgeViewSuggestion);

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


        userProfileTxt.setOnClickListener(this);
        publicProfileTxt.setOnClickListener(this);
        ticketHistoryTxt.setOnClickListener(this);
        notificationTxt.setOnClickListener(this);

        shareAppTxt.setOnClickListener(this);
        aboutAppTxt.setOnClickListener(this);

        reportIssueTxt.setOnClickListener(this);
        bugTxt.setOnClickListener(this);
        suggestionTxt.setOnClickListener(this);

        signOutTxt.setOnClickListener(this);

        return rootView;
    }


    private void setUserDetails() {
        if (User.getInstance() != null) {
            setAvatar(User.getInstance().getAvatar());
            username = User.getInstance().getUserName();
            userMailId = User.getInstance().getUserMailID();
        }

        userNameTxt.setText(username);
        userMailIdTxt.setText(userMailId);
    }

    private void setAvatar(int icon){
        final String iconStr = "av_"+icon;
        int imageId = requireContext().getResources().getIdentifier(iconStr, "drawable", requireContext().getPackageName());
        avatarImg.setImageResource(imageId);
    }

    // on-click listeners onUserProfileCalledListener
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accountPgeSignOut) {
            processSignOut();
        } else if (id == R.id.accountPgeViewUserProfile) {
            openUserProfile();
        } else if (id == R.id.accountPgeViewPublicProfile) {
            openPublicProfile();
        } else if (id == R.id.accountPgeViewTicketHistory) {
            promptTicketSection();
        } else if (id == R.id.accountPgeViewShareApp) {
            openShareAppDialog();
        } else if (id == R.id.accountPgeViewSuggestion) {
            openSuggestionFragment();
        } else if (id == R.id.accountPgeViewReportIssue) {
            openReportFragment();
        } else if (id == R.id.accountPgeViewSpottedBug) {
            openBugFragment();
        } else if (id == R.id.accountPgeViewAbout) {
            openAboutFragment();
        } else if (id == R.id.accountPgeNotificationSetting) {
            openNotificationFragment();
        }
    }

    private void openUserProfile() {
        this.onAccountMenuClicked.userProfileClicked();
    }

    private void openPublicProfile() {
        if (User.getInstance().getUserContactNumber() == null || User.getInstance().getUserContactNumber().isEmpty() || User.getInstance().getUserContactNumber().equalsIgnoreCase("N/A")) {
            callSnackBar("Update contact number for public profile feature");
            return;
        }
        this.onAccountMenuClicked.publicProfileClicked();
    }

    private void openNotificationFragment() {
        this.onAccountMenuClicked.notificationsClicked();
    }

    private void openAboutFragment() {
        this.onAccountMenuClicked.aboutAppClicked();
    }

    private void openReportFragment() {
        callSnackBar("In Beta , Use The Google Form Provided To Report The Issues You Faced");
        //this.onAccountMenuClicked.reportIssueClicked();
    }

    private void openBugFragment() {
        callSnackBar("In Beta , Use The Google Form Provided To Share The Bugs You Found");
        //this.onAccountMenuClicked.spottedBugClicked();
    }

    private void openSuggestionFragment() {
        callSnackBar("Currently not taking any suggestion");
        //this.onAccountMenuClicked.haveSuggestionsClicked();
    }

    private void openShareAppDialog() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Insider Hostel");
        String shareMessage = "\nHey dude, check out this app for VIT Chennai Hostel\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n";
        intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(intent, "Select One"));
    }

    private void promptTicketSection() {
        dialog.setContentView(R.layout.choose_ticket_section_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        MaterialTextView roomTicket = dialog.findViewById(R.id.chooseTicketRoomTxt);
        MaterialTextView blockTicket = dialog.findViewById(R.id.chooseTicketBlockTxt);
        MaterialTextView outingTicket = dialog.findViewById(R.id.chooseTicketOutingTxt);

        roomTicket.setOnClickListener(v -> {
            dialog.dismiss();
            callSnackBar("Will be available in future update");
           // openRoomTickets();
        });

        blockTicket.setOnClickListener(v -> {
            dialog.dismiss();
            callSnackBar("Will be available in future update");
           // openBlockTickets();
        });

        outingTicket.setOnClickListener(v -> {
            dialog.dismiss();
            openOutingTickets();
        });


        dialog.show();


    }

    private void openRoomTickets() {
        onTicketSectionChosen.roomTicketClicked();
    }

    private void openBlockTickets() {
        onTicketSectionChosen.blockTicketClicked();
    }

    private void openOutingTickets() {
        onTicketSectionChosen.outingTicketClicked();
    }


    // method to handle the sign out process
    private void processSignOut() {
        AppNotification.getInstance().unSubscribeAllTopics();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tokenSection.document(User.getInstance().getUser_Id()).delete();
        }

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
            this.onAccountMenuClicked = (iOnAccountMenuClicked) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "is not implementing iOnAccountClicked");
        }
        try {
            this.onTicketSectionChosen = (iOnTicketSectionClicked) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "is not implementing iOnTicketSectionClicked");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.onAccountMenuClicked != null) {
            this.onAccountMenuClicked = null;
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