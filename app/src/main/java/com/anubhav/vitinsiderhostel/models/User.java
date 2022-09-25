package com.anubhav.vitinsiderhostel.models;

import android.app.Application;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User extends Application {


    private static User instance;
    // 12 data members
    private String user_UID;

    private String userMailId;

    private String studentRegisterNumber;

    private String studentName;

    private String userName;

    private String studentBranch;

    private String userContactNumber;

    private String studentNativeState;

    private int nativeStateChanges;

    private String userType;           // F->Faculty            S->Student

    private int avatar;

    private String parentMailId;

    private String studentBlock;         // A , B , C

    private String roomNo;           //  block,RoomNo

    private int beds;         //  bed,1   or bed,0         1-> AC , 2-> Non-Ac

    private boolean ac;

    private String mess;

    private boolean hasPublicProfile;

    private String publicBio;

    private int publicColor;

    private String privateProfileId;

    private String fcmToken;


    public User() {
    }


    public static User getInstance() {
        if (instance == null)
            instance = new User();
        return instance;
    }

    public void setInstance(User instance) {
        User.instance = instance;
    }


    public String getUser_UID() {
        return user_UID;
    }

    public void setUser_UID(String user_UID) {
        this.user_UID = user_UID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMailId() {
        return userMailId;
    }

    public void setUserMailId(String userMailId) {
        this.userMailId = userMailId;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public void setUserContactNumber(String userContactNumber) {
        this.userContactNumber = userContactNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentBlock() {
        return studentBlock;
    }

    public void setStudentBlock(String studentBlock) {
        this.studentBlock = studentBlock;
    }

    public String getStudentBranch() {
        return studentBranch;
    }

    public void setStudentBranch(String studentBranch) {
        this.studentBranch = studentBranch;
    }

    public String getStudentRegisterNumber() {
        return studentRegisterNumber;
    }

    public void setStudentRegisterNumber(String studentRegisterNumber) {
        this.studentRegisterNumber = studentRegisterNumber;
    }

    public String getStudentNativeState() {
        return studentNativeState;
    }

    public void setStudentNativeState(String studentNativeState) {
        this.studentNativeState = studentNativeState;
    }

    public String getParentMailId() {
        return parentMailId;
    }

    public void setParentMailId(String parentMailId) {
        this.parentMailId = parentMailId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public boolean getAc() {
        return ac;
    }

    public void setAc(boolean ac) {
        this.ac = ac;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public int getNativeStateChanges() {
        return nativeStateChanges;
    }

    public void setNativeStateChanges(int nativeStateChanges) {
        this.nativeStateChanges = nativeStateChanges;
    }

    public boolean getHasPublicProfile() {
        return hasPublicProfile;
    }

    public void setHasPublicProfile(boolean hasPublicProfile) {
        this.hasPublicProfile = hasPublicProfile;
    }

    public String getPrivateProfileId() {
        return privateProfileId;
    }

    public void setPrivateProfileId(String privateProfileId) {
        this.privateProfileId = privateProfileId;
    }

    public String getPublicBio() {
        return publicBio;
    }

    public void setPublicBio(String publicBio) {
        this.publicBio = publicBio;
    }

    public int getPublicColor() {
        return publicColor;
    }

    public void setPublicColor(int publicColor) {
        this.publicColor = publicColor;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
