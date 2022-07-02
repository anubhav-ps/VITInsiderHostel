package com.anubhav.vitinsiderhostel.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.fragments.DeleteAccountFragment;
import com.anubhav.vitinsiderhostel.fragments.EditProfileFragment;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;


public class ViewUserProfileFragment extends Fragment implements View.OnClickListener {


    MaterialTextView userTypeTxt, userNameTxt, userMailIdTxt, userContactNumTxt, userNativeLanguageTxt, userBranchTxt ,userRegisterNumTxt;

    private String userType;
    private String username;
    private String userMailId;
    private String userRegisterNum;
    private String avatar;
    private String userContactNum;
    private String userNativeLanguage;
    private String userBranch;
    private String strUserType;


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

        editBtn = view.findViewById(R.id.viewUserProfilePgeEdit);
        closeAccountBtn = view.findViewById(R.id.viewUserProfilePgeCloseAccount);

        userTypeTxt = view.findViewById(R.id.viewUserProfilePgeUserTypeTxt);
        userNameTxt = view.findViewById(R.id.viewUserProfilePgeUsernameTxt);
        userMailIdTxt = view.findViewById(R.id.viewUserProfilePgeUserMailIdTxt);
        userRegisterNumTxt = view.findViewById(R.id.viewUserProfilePgeRegisterNumberTxt);
        userContactNumTxt = view.findViewById(R.id.viewUserProfilePgeContactNumberTxt);
        userNativeLanguageTxt = view.findViewById(R.id.viewUserProfilePgeNativeLanguageTxt);
        userBranchTxt = view.findViewById(R.id.viewUserProfilePgeBranchTxt);

        editBtn.setOnClickListener(this);
        closeAccountBtn.setOnClickListener(this);

        if (User.getInstance() != null) {
            userType = User.getInstance().getUserType();
            username = User.getInstance().getUserName();
            userMailId = User.getInstance().getUserMailID();
            userRegisterNum = User.getInstance().getStudentRegisterNumber();
            userContactNum = User.getInstance().getUserContactNumber();
            userNativeLanguage = User.getInstance().getStudentNativeLanguage();
            userBranch = User.getInstance().getStudentBranch();
        }

       strUserType = "STUDENT";

        userTypeTxt.setText(strUserType);
        userNameTxt.setText(username);
        userMailIdTxt.setText(userMailId);
        userRegisterNumTxt.setText(userRegisterNum);
        userContactNumTxt.setText(userContactNum);
        userNativeLanguageTxt.setText(userNativeLanguage);
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

    private void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userProfilePageContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}