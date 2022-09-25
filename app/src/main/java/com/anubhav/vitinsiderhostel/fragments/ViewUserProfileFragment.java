package com.anubhav.vitinsiderhostel.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;


public class ViewUserProfileFragment extends Fragment implements View.OnClickListener, iOnNotifyDbProcess {


    private MaterialTextView userTypeTxt, userNameTxt, studentNameTxt, parentMailIdTxt, userMailIdTxt, userContactNumTxt, userNativeStateTxt, userBranchTxt, userRegisterNumTxt;
    private ImageView avatarIcon;
    private String userType;
    private String username;
    private String studentName;
    private String parentMailId;
    private String userMailId;
    private String userRegisterNum;
    private String avatar;
    private String userContactNum;
    private String userNativeState;
    private String userBranch;
    private String strUserType;

    //local database
    private LocalSqlDatabase localSqlDatabase;


    public ViewUserProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_view_user_profile, container, false);

        MaterialButton editBtn, closeAccountBtn;

        localSqlDatabase = new LocalSqlDatabase(getContext(), this);

        editBtn = view.findViewById(R.id.viewUserProfilePgeEdit);
        closeAccountBtn = view.findViewById(R.id.viewUserProfilePgeCloseAccount);

        avatarIcon = view.findViewById(R.id.viewUserProfilePgeAvatar);
        userTypeTxt = view.findViewById(R.id.viewUserProfilePgeUserTypeTxt);
        userNameTxt = view.findViewById(R.id.viewUserProfilePgeUsernameTxt);
        studentNameTxt = view.findViewById(R.id.viewUserProfilePgeStudentNameTxt);
        parentMailIdTxt = view.findViewById(R.id.viewUserProfilePgeParentMailIdTxt);
        userMailIdTxt = view.findViewById(R.id.viewUserProfilePgeUserMailIdTxt);
        userRegisterNumTxt = view.findViewById(R.id.viewUserProfilePgeRegisterNumberTxt);
        userContactNumTxt = view.findViewById(R.id.viewUserProfilePgeContactNumberTxt);
        userNativeStateTxt = view.findViewById(R.id.viewUserProfilePgeNativeStateTxt);
        userBranchTxt = view.findViewById(R.id.viewUserProfilePgeBranchTxt);

        editBtn.setOnClickListener(this);
        closeAccountBtn.setOnClickListener(this);

        if (User.getInstance() != null) {
            setAvatar(User.getInstance().getAvatar());
            userType = User.getInstance().getUserType();
            username = User.getInstance().getUserName();
            studentName = User.getInstance().getStudentName();
            userMailId = User.getInstance().getUserMailId();
            parentMailId = User.getInstance().getParentMailId();
            userRegisterNum = User.getInstance().getStudentRegisterNumber();
            userContactNum = User.getInstance().getUserContactNumber();
            userNativeState = User.getInstance().getStudentNativeState();
            userBranch = User.getInstance().getStudentBranch();
        }

        String name = transformText(studentName.split(" "));

        strUserType = "STUDENT";

        userTypeTxt.setText(strUserType);
        userNameTxt.setText(username);
        studentNameTxt.setText(name);
        userMailIdTxt.setText(userMailId);
        parentMailIdTxt.setText(parentMailId);
        userRegisterNumTxt.setText(userRegisterNum);
        userContactNumTxt.setText(userContactNum);
        userNativeStateTxt.setText(userNativeState);
        userBranchTxt.setText(userBranch);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.viewUserProfilePgeEdit) {
            EditProfileFragment editProfileFragment = new EditProfileFragment();
            changeFragment(editProfileFragment);
        } else if (id == R.id.viewUserProfilePgeCloseAccount) {
            DeleteAccountFragment deleteAccountFragment = new DeleteAccountFragment();
            changeFragment(deleteAccountFragment);
        }
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

    @Override
    public void onStart() {
        super.onStart();
        if (localSqlDatabase == null) {
            localSqlDatabase = new LocalSqlDatabase(getContext(), this);
        }
    }

    private void setAvatar(int icon) {
        final String iconStr = "av_" + icon;
        int imageId = requireContext().getResources().getIdentifier(iconStr, "drawable", requireContext().getPackageName());
        avatarIcon.setImageResource(imageId);
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.accountMenuPgeFragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void notifyCompleteDataDownload() {

    }

    @Override
    public void notifyUserUpdated() {

    }
}