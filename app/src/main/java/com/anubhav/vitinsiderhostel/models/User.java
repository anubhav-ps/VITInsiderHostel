package com.anubhav.vitinsiderhostel.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Entity(tableName = "AppUserData")
public class User extends Application {


    private static User instance;
    // 11 data members
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "app_user_id")
    private String user_Id;
    @ColumnInfo(name = "app_user_doc_id")
    private String doc_Id;
    @ColumnInfo(name = "app_user_name")
    private String userName;
    @ColumnInfo(name = "app_user_mail_id")
    private String userMailID;
    @ColumnInfo(name = "app_user_contact_num")
    private String userContactNumber;
    @ColumnInfo(name = "app_user_type")
    private String userType;           // F->Faculty            S->Student
    @ColumnInfo(name = "app_user_student_block")
    private String studentBlock;         // A , B , C
    @ColumnInfo(name = "app_user_student_branch")
    private String studentBranch;
    @ColumnInfo(name = "app_user_student_language")
    private String studentNativeLanguage;
    @ColumnInfo(name = "app_user_room_no")
    private String roomNo;           //  block,RoomNo
    @ColumnInfo(name = "app_user_room_type")
    private String roomType;         //  bed,1   or bed,0         1-> AC , 2-> Non-Ac
    @ColumnInfo(name = "app_user_room_is_admin")
    private Boolean isAdmin = false;

    public User() {
    }

    public static User getInstance() {
        if (instance == null)
            instance = new User();
        return instance;
    }

    public static void setInstance(User instance) {
        User.instance = instance;
    }

    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String userId) {
        this.user_Id = userId;
    }

    public String getDoc_Id() {
        return doc_Id;
    }

    public void setDoc_Id(String docId) {
        this.doc_Id = docId;
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
}
