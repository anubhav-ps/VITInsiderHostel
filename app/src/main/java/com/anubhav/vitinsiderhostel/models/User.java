package com.anubhav.vitinsiderhostel.models;

import android.app.Application;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User extends Application {


    private static User instance;
    // 12 data members
    private String user_Id;

    private String userName;

    private String userMailID;

    private String userContactNumber;

    private String userType;           // F->Faculty            S->Student

    private String studentBlock;         // A , B , C

    private String studentBranch;

    private String studentRegisterNumber;

    private String studentNativeLanguage;

    private String roomNo;           //  block,RoomNo

    private String roomType;         //  bed,1   or bed,0         1-> AC , 2-> Non-Ac

    private Boolean isAdmin = false;   // false = 0 , true = 1

    private int avatar;

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

    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String userId) {
        this.user_Id = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMailID() {
        return userMailID;
    }

    public void setUserMailID(String userMailID) {
        this.userMailID = userMailID;
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

    public String getStudentNativeLanguage() {
        return studentNativeLanguage;
    }

    public void setStudentNativeLanguage(String studentNativeLanguage) {
        this.studentNativeLanguage = studentNativeLanguage;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getStudentRegisterNumber() {
        return studentRegisterNumber;
    }

    public void setStudentRegisterNumber(String studentRegisterNumber) {
        this.studentRegisterNumber = studentRegisterNumber;
    }
}
